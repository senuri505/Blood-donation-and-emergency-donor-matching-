package com.blooddonation.controller;

import com.blooddonation.dao.BloodRequestDAO;
import com.blooddonation.dao.DonorDAO;
import com.blooddonation.dao.SettingsDAO;
import com.blooddonation.model.BloodRequest;
import com.blooddonation.model.Donor;
import com.blooddonation.model.Notification;
import com.blooddonation.util.BloodCompatibility;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@WebServlet("/MatchingServlet")
public class MatchingServlet extends HttpServlet {

    private BloodRequestDAO requestDAO = new BloodRequestDAO();
    private DonorDAO donorDAO = new DonorDAO();
    private SettingsDAO settingsDAO = new SettingsDAO();
    private Gson gson = new Gson();

    public static class DonorMatch {
        private Donor donor;
        private int score;
        private String eligibilityReason;

        public DonorMatch(Donor donor, int score, String eligibilityReason) {
            this.donor = donor;
            this.score = score;
            this.eligibilityReason = eligibilityReason;
        }

        public Donor getDonor() { return donor; }
        public int getScore() { return score; }
        public String getEligibilityReason() { return eligibilityReason; }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String requestIdStr = request.getParameter("requestId");
        if (requestIdStr == null || requestIdStr.trim().isEmpty()) {
            response.getWriter().write("{\"success\":false, \"message\":\"Request ID is required.\"}");
            return;
        }

        int requestId = Integer.parseInt(requestIdStr);
        BloodRequest req = requestDAO.getRequestById(requestId);
        if (req == null) {
            response.getWriter().write("{\"success\":false, \"message\":\"Blood request not found.\"}");
            return;
        }
        String status = req.getStatus();
        if (status != null && ("accepted".equalsIgnoreCase(status) || "completed".equalsIgnoreCase(status) || "cancelled".equalsIgnoreCase(status))) {
            response.getWriter().write("{\"success\":false, \"message\":\"Request is no longer active for matching.\"}");
            return;
        }

        String requestedType = req.getBloodType() + req.getRhFactor();
        boolean isCritical = "critical".equalsIgnoreCase(req.getUrgency());

        int cooldownDays = 56;
        try {
            cooldownDays = Integer.parseInt(settingsDAO.getValue("cooldown_days", "56"));
        } catch (Exception ignored) {}

        List<Donor> approvedDonors = donorDAO.getCompatibleDonors(requestedType, req.getCity(), cooldownDays);
        List<DonorMatch> matches = new ArrayList<>();

        for (Donor d : approvedDonors) {
            int score = 0;
            String donorType = d.getBloodType() + d.getRhFactor();

            if (donorType.equalsIgnoreCase(requestedType)) {
                score += isCritical ? 35 : 50;
            } else if ("O-".equalsIgnoreCase(donorType)) {
                score += isCritical ? 28 : 30;
            } else {
                score += isCritical ? 20 : 25;
            }

            if (d.getCity() != null && req.getCity() != null && d.getCity().equalsIgnoreCase(req.getCity())) {
                score += isCritical ? 25 : 30;
            } else {
                score += 10;
            }

            score += 10;
            if (d.getLastDonationDate() != null) {
                long daysSince = ChronoUnit.DAYS.between(d.getLastDonationDate().toLocalDate(), LocalDate.now());
                long monthsSince = daysSince / 30;
                score += Math.min(10, (int) monthsSince);
            } else {
                score += 10;
            }

            if (score > 100) score = 100;
            matches.add(new DonorMatch(d, score, "Eligible Donor Match"));
        }

        Collections.sort(matches, (a, b) -> Integer.compare(b.getScore(), a.getScore()));
        response.getWriter().write(gson.toJson(matches));
    }
}
