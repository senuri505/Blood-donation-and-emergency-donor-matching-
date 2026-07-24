package com.blooddonation.controller;

import com.blooddonation.dao.BloodStockDAO;
import com.blooddonation.dao.SettingsDAO;
import com.blooddonation.model.BloodUnit;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/BloodStockServlet")
public class BloodStockServlet extends HttpServlet {

    private BloodStockDAO bloodStockDAO = new BloodStockDAO();
    private SettingsDAO settingsDAO = new SettingsDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Clean expired units on fetch
        bloodStockDAO.updateExpiredUnits();

        String action = request.getParameter("action");
        if ("summary".equals(action)) {
            Map<String, Object> summary = new HashMap<>();
            Map<String, Integer> stockByGroup = bloodStockDAO.getStockSummaryByGroup();
            int lowStockThreshold = Integer.parseInt(settingsDAO.getValue("low_stock_threshold", "3"));

            Map<String, Boolean> lowStockWarnings = new HashMap<>();
            for (Map.Entry<String, Integer> entry : stockByGroup.entrySet()) {
                lowStockWarnings.put(entry.getKey(), entry.getValue() < lowStockThreshold);
            }

            summary.put("stock", stockByGroup);
            summary.put("warnings", lowStockWarnings);
            summary.put("lowStockThreshold", lowStockThreshold);
            summary.put("totalAvailable", bloodStockDAO.getTotalAvailableUnits());

            response.getWriter().write(gson.toJson(summary));
            return;
        }

        List<BloodUnit> units = bloodStockDAO.getAllBloodUnits();
        response.getWriter().write(gson.toJson(units));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> respMap = new HashMap<>();

        String bloodType = request.getParameter("bloodType");
        String rhFactor = request.getParameter("rhFactor");
        String volumeStr = request.getParameter("volumeMl");
        String donorName = request.getParameter("donorName");
        String unitsCountStr = request.getParameter("unitsCount");

        if (bloodType == null || rhFactor == null) {
            respMap.put("success", false);
            respMap.put("message", "Blood type and Rh factor are required.");
            response.getWriter().write(gson.toJson(respMap));
            return;
        }

        int volumeMl = 450;
        if (volumeStr != null && !volumeStr.trim().isEmpty()) {
            try { volumeMl = Integer.parseInt(volumeStr); } catch (Exception ignored) {}
        }

        int unitsCount = 1;
        if (unitsCountStr != null && !unitsCountStr.trim().isEmpty()) {
            try { unitsCount = Integer.parseInt(unitsCountStr); } catch (Exception ignored) {}
        }

        int expiryDays = 42;
        try { expiryDays = Integer.parseInt(settingsDAO.getValue("expiry_days", "42")); } catch (Exception ignored) {}

        Date collectedDate = Date.valueOf(LocalDate.now());
        Date expiresDate = Date.valueOf(LocalDate.now().plusDays(expiryDays));

        boolean success = true;
        for (int i = 0; i < unitsCount; i++) {
            BloodUnit unit = new BloodUnit();
            unit.setBloodType(bloodType.trim());
            unit.setRhFactor(rhFactor.trim());
            unit.setVolumeMl(volumeMl);
            unit.setCollectedDate(collectedDate);
            unit.setExpiresDate(expiresDate);
            unit.setDonorName(donorName != null && !donorName.trim().isEmpty() ? donorName.trim() : "Manual Addition");
            unit.setStatus("available");

            if (!bloodStockDAO.addBloodUnit(unit)) {
                success = false;
            }
        }

        respMap.put("success", success);
        respMap.put("message", success ? unitsCount + " blood unit(s) added successfully." : "Failed to add blood units.");
        response.getWriter().write(gson.toJson(respMap));
    }
}
