package com.blooddonation.controller;

import com.blooddonation.dao.BloodStockDAO;
import com.blooddonation.dao.DonationDAO;
import com.blooddonation.dao.DonorDAO;
import com.blooddonation.dao.SettingsDAO;
import com.blooddonation.model.BloodUnit;
import com.blooddonation.model.Donation;
import com.blooddonation.model.Donor;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/DonationServlet")
public class DonationServlet extends HttpServlet {

    private DonationDAO donationDAO = new DonationDAO();
    private DonorDAO donorDAO = new DonorDAO();
    private BloodStockDAO bloodStockDAO = new BloodStockDAO();
    private SettingsDAO settingsDAO = new SettingsDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String donorIdStr = request.getParameter("donorId");
        String hospitalIdStr = request.getParameter("hospitalId");

        if (donorIdStr != null) {
            List<Donation> list = donationDAO.getDonationsByDonorId(Integer.parseInt(donorIdStr));
            response.getWriter().write(gson.toJson(list));
            return;
        } else if (hospitalIdStr != null) {
            List<Donation> list = donationDAO.getDonationsByHospitalId(Integer.parseInt(hospitalIdStr));
            response.getWriter().write(gson.toJson(list));
            return;
        }

        List<Donation> list = donationDAO.getAllDonations();
        response.getWriter().write(gson.toJson(list));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> respMap = new HashMap<>();

        String donorIdStr = request.getParameter("donorId");
        String hospitalName = request.getParameter("hospitalName");
        String hospitalIdStr = request.getParameter("hospitalId");
        String notes = request.getParameter("notes");
        String volumeStr = request.getParameter("volumeMl");

        if (donorIdStr == null || donorIdStr.trim().isEmpty()) {
            respMap.put("success", false);
            respMap.put("message", "Please select a valid donor.");
            response.getWriter().write(gson.toJson(respMap));
            return;
        }

        int donorId = Integer.parseInt(donorIdStr);
        Donor donor = donorDAO.getDonorById(donorId);
        if (donor == null) {
            respMap.put("success", false);
            respMap.put("message", "Donor not found.");
            response.getWriter().write(gson.toJson(respMap));
            return;
        }

        int volumeMl = 450;
        if (volumeStr != null && !volumeStr.trim().isEmpty()) {
            try {
                volumeMl = Integer.parseInt(volumeStr);
            } catch (NumberFormatException ignored) {}
        }

        int hospitalId = 0;
        if (hospitalIdStr != null && !hospitalIdStr.trim().isEmpty()) {
            try {
                hospitalId = Integer.parseInt(hospitalIdStr);
            } catch (NumberFormatException ignored) {}
        }

        Date todayDate = Date.valueOf(LocalDate.now());
        Timestamp nowTime = new Timestamp(System.currentTimeMillis());

        Donation donation = new Donation();
        donation.setDonorId(donor.getId());
        donation.setDonorName(donor.getName());
        donation.setBloodType(donor.getBloodType());
        donation.setRhFactor(donor.getRhFactor());
        donation.setVolumeMl(volumeMl);
        donation.setDonatedAt(nowTime);
        donation.setHospitalName(hospitalName != null ? hospitalName : "Central Blood Bank");
        donation.setHospitalId(hospitalId);
        donation.setNotes(notes != null ? notes : "Routine Donation");

        boolean added = donationDAO.addDonation(donation);

        if (added) {
            // 1. Update donor's last donation date and increment total donations
            donorDAO.updateDonationHistory(donor.getId(), todayDate);

            // 2. Add to blood stock inventory
            int expiryDays = 42;
            try {
                expiryDays = Integer.parseInt(settingsDAO.getValue("expiry_days", "42"));
            } catch (Exception ignored) {}

            BloodUnit unit = new BloodUnit();
            unit.setBloodType(donor.getBloodType());
            unit.setRhFactor(donor.getRhFactor());
            unit.setVolumeMl(volumeMl);
            unit.setCollectedDate(todayDate);
            unit.setExpiresDate(Date.valueOf(LocalDate.now().plusDays(expiryDays)));
            unit.setDonorId(donor.getId());
            unit.setDonorName(donor.getName());
            unit.setStatus("available");

            bloodStockDAO.addBloodUnit(unit);

            respMap.put("success", true);
            respMap.put("message", "Donation recorded successfully! Blood stock increased.");
        } else {
            respMap.put("success", false);
            respMap.put("message", "Failed to record donation.");
        }

        response.getWriter().write(gson.toJson(respMap));
    }
}
