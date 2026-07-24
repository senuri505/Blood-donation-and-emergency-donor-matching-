package com.blooddonation.controller;

import com.blooddonation.dao.*;
import com.blooddonation.model.*;
import com.blooddonation.util.BloodCompatibility;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/DashboardServlet")
public class DashboardServlet extends HttpServlet {

    private DonorDAO donorDAO = new DonorDAO();
    private HospitalDAO hospitalDAO = new HospitalDAO();
    private BloodStockDAO stockDAO = new BloodStockDAO();
    private BloodRequestDAO requestDAO = new BloodRequestDAO();
    private DonationDAO donationDAO = new DonationDAO();
    private NotificationDAO notificationDAO = new NotificationDAO();
    private SettingsDAO settingsDAO = new SettingsDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        String action = request.getParameter("action");
        if (action == null) action = "admin";

        Map<String, Object> data = new HashMap<>();

        if ("donor".equalsIgnoreCase(action)) {
            if (session != null && session.getAttribute("user") != null) {
                User user = (User) session.getAttribute("user");
                Donor d = donorDAO.getDonorByUserId(user.getId());

                if (d != null) {
                    data.put("donor", d);

                    // Check eligibility
                    int cooldownDays = 56;
                    try {
                        cooldownDays = Integer.parseInt(settingsDAO.getValue("cooldown_days", "56"));
                    } catch (Exception ignored) {}

                    boolean isEligible = true;
                    String reason = "Eligible to donate";
                    long daysRemaining = 0;
                    String nextEligibleDate = LocalDate.now().toString();

                    if (!d.isAvailable()) {
                        isEligible = false;
                        reason = "Status set to Unavailable";
                    } else if (d.getAge() < 18 || d.getAge() > 65) {
                        isEligible = false;
                        reason = "Age restriction (must be 18 - 65)";
                    } else if (d.getWeight() < 50) {
                        isEligible = false;
                        reason = "Weight requirement not met (min 50 kg)";
                    } else if (d.getLastDonationDate() != null) {
                        LocalDate lastDonated = d.getLastDonationDate().toLocalDate();
                        long daysSince = ChronoUnit.DAYS.between(lastDonated, LocalDate.now());
                        if (daysSince < cooldownDays) {
                            isEligible = false;
                            daysRemaining = cooldownDays - daysSince;
                            LocalDate nextDate = lastDonated.plusDays(cooldownDays);
                            nextEligibleDate = nextDate.toString();
                            reason = "Cooldown period — " + daysRemaining + " days remaining";
                        }
                    }

                    data.put("isEligible", isEligible);
                    data.put("eligibilityReason", reason);
                    data.put("cooldownDaysRemaining", daysRemaining);
                    data.put("nextEligibleDate", nextEligibleDate);
                    data.put("totalDonations", d.getTotalDonations());

                    // Matched requests for this donor based on blood compatibility, availability, and matching status
                    List<BloodRequest> matchedRequests = new ArrayList<>();
                    if (d.isAvailable()) {
                        List<BloodRequest> allReqs = requestDAO.getAllRequests();
                        String donorType = d.getBloodType() + d.getRhFactor();
                        for (BloodRequest req : allReqs) {
                            if (!"matching".equalsIgnoreCase(req.getStatus()) && !"accepted".equalsIgnoreCase(req.getStatus()) && !"completed".equalsIgnoreCase(req.getStatus())) {
                                continue;
                            }
                            String requestType = req.getBloodType() + req.getRhFactor();
                            if (!BloodCompatibility.isCompatible(donorType, requestType)) {
                                continue;
                            }

                            boolean isAssignedToMe = (req.getAssignedDonorId() == d.getId());
                            boolean isInMatchedList = false;

                            if (req.getMatchedDonorIds() != null && !req.getMatchedDonorIds().trim().isEmpty()) {
                                String[] ids = req.getMatchedDonorIds().split(",");
                                for (String idStr : ids) {
                                    try {
                                        if (!idStr.trim().isEmpty() && Integer.parseInt(idStr.trim()) == d.getId()) {
                                            isInMatchedList = true;
                                            break;
                                        }
                                    } catch (NumberFormatException ignored) {}
                                }
                            } else {
                                isInMatchedList = true;
                            }

                            if (isInMatchedList || isAssignedToMe) {
                                matchedRequests.add(req);
                            }
                        }
                    }
                    data.put("matchedRequests", matchedRequests);

                    // Donor donation history
                    data.put("history", donationDAO.getDonationsByDonorId(d.getId()));
                }
            }
            response.getWriter().write(gson.toJson(data));
            return;

        } else if ("hospital".equalsIgnoreCase(action)) {
            if (session != null && session.getAttribute("user") != null) {
                User user = (User) session.getAttribute("user");
                Hospital h = hospitalDAO.getHospitalByUserId(user.getId());

                if (h != null) {
                    data.put("hospital", h);
                    List<BloodRequest> hospitalRequests = requestDAO.getRequestsByHospitalId(h.getId());
                    data.put("totalRequests", hospitalRequests.size());

                    long activeCount = hospitalRequests.stream()
                            .filter(r -> "pending".equalsIgnoreCase(r.getStatus()) || "matching".equalsIgnoreCase(r.getStatus()) || "accepted".equalsIgnoreCase(r.getStatus()) || "fulfilled".equalsIgnoreCase(r.getStatus()))
                            .count();
                    data.put("activeRequests", activeCount);

                    List<Donation> hospitalDonations = donationDAO.getDonationsByHospitalId(h.getId());
                    data.put("donationsCount", hospitalDonations.size());
                    data.put("availableUnits", stockDAO.getTotalAvailableUnits());

                    for (BloodRequest req : hospitalRequests) {
                        if (req.getAssignedDonorId() > 0) {
                            req.setAssignedDonor(donorDAO.getDonorById(req.getAssignedDonorId()));
                        }
                    }

                    data.put("recentRequests", hospitalRequests);
                    data.put("recentDonations", hospitalDonations);
                }
            }
            response.getWriter().write(gson.toJson(data));
            return;
        }

        // Default Admin Dashboard Stats
        int approvedDonors = donorDAO.getTotalApprovedDonorCount();
        int totalBloodUnits = stockDAO.getTotalAvailableUnits();
        int activeRequests = requestDAO.getActiveRequestCount();
        int criticalRequests = requestDAO.getCriticalRequestCount();
        int totalHospitals = hospitalDAO.getTotalApprovedHospitalCount();
        int pendingApprovals = donorDAO.getAwaitingApproval().size() + hospitalDAO.getAwaitingApproval().size();
        int totalDonations = donationDAO.getTotalDonationsCount();

        data.put("approvedDonors", approvedDonors);
        data.put("bloodUnits", totalBloodUnits);
        data.put("activeRequests", activeRequests);
        data.put("criticalRequests", criticalRequests);
        data.put("hospitals", totalHospitals);
        data.put("pendingApprovals", pendingApprovals);
        data.put("totalDonations", totalDonations);

        data.put("bloodStock", stockDAO.getStockSummaryByGroup());
        data.put("donorDistribution", donorDAO.getDonorCountByBloodGroup());

        // Notifications for admin (user_id = 1)
        data.put("notifications", notificationDAO.getNotificationsByUserId(1));

        response.getWriter().write(gson.toJson(data));
    }
}
