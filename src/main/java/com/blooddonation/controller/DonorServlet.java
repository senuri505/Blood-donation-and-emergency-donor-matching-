package com.blooddonation.controller;

import com.blooddonation.dao.DonorDAO;
import com.blooddonation.dao.NotificationDAO;
import com.blooddonation.dao.UserDAO;
import com.blooddonation.model.Donor;
import com.blooddonation.util.BloodCompatibility;
import com.blooddonation.model.Notification;
import com.blooddonation.model.User;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/DonorServlet")
public class DonorServlet extends HttpServlet {

    private DonorDAO donorDAO = new DonorDAO();
    private UserDAO userDAO = new UserDAO();
    private NotificationDAO notificationDAO = new NotificationDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) action = "list";

        if ("get".equals(action)) {
            String idStr = request.getParameter("id");
            if (idStr != null) {
                Donor d = donorDAO.getDonorById(Integer.parseInt(idStr));
                response.getWriter().write(gson.toJson(d));
                return;
            }
        } else if ("me".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("user") != null) {
                User user = (User) session.getAttribute("user");
                Donor d = donorDAO.getDonorByUserId(user.getId());
                response.getWriter().write(gson.toJson(d));
                return;
            }
        } else if ("awaiting".equals(action)) {
            List<Donor> list = donorDAO.getAwaitingApproval();
            response.getWriter().write(gson.toJson(list));
            return;
        } else if ("search".equals(action)) {
            String bloodType = request.getParameter("bloodType");
            String rhFactor = request.getParameter("rhFactor");
            String city = request.getParameter("city");
            String status = request.getParameter("status");
            List<Donor> list = donorDAO.searchDonors(bloodType, rhFactor, city, status);
            response.getWriter().write(gson.toJson(list));
            return;
        } else if ("searchCompatible".equals(action)) {
            String recipientBloodType = request.getParameter("recipientBloodType");
            String recipientRh = request.getParameter("recipientRh");
            String city = request.getParameter("city");
            String availability = request.getParameter("availability"); // available, unavailable, all

            List<Donor> candidates = donorDAO.getApprovedDonors();
            List<Donor> filtered = new java.util.ArrayList<>();
            String requestGroup = (recipientBloodType != null ? recipientBloodType.trim() : "") + (recipientRh != null ? recipientRh.trim() : "");
            
            for (Donor d : candidates) {
                // Filter availability if specified
                if ("available".equalsIgnoreCase(availability) && !d.isAvailable()) continue;
                if ("unavailable".equalsIgnoreCase(availability) && d.isAvailable()) continue;
                if (availability == null && !d.isAvailable()) continue; // Default to available donors

                // Filter city
                if (city != null && !city.trim().isEmpty()) {
                    if (d.getCity() == null || !d.getCity().toLowerCase().contains(city.trim().toLowerCase())) continue;
                }

                // Filter blood compatibility if blood group specified
                if (!requestGroup.isEmpty()) {
                    String donorGroup = d.getBloodType() + d.getRhFactor();
                    if (!BloodCompatibility.isCompatible(donorGroup, requestGroup)) {
                        continue;
                    }
                }

                filtered.add(d);
            }
            response.getWriter().write(gson.toJson(filtered));
            return;
        }

        // Default list
        List<Donor> list = donorDAO.getAllDonors();
        response.getWriter().write(gson.toJson(list));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        Map<String, Object> respMap = new HashMap<>();

        if ("approve".equals(action)) {
            String idStr = request.getParameter("id");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                boolean ok = donorDAO.updateApprovalStatus(id, "approved");
                if (ok) {
                    Donor d = donorDAO.getDonorById(id);
                    if (d != null && d.getUserId() != 0) {
                        Notification n = new Notification();
                        n.setUserId(d.getUserId());
                        n.setTitle("Registration Approved");
                        n.setMessage("Your donor registration has been approved! You can now participate in blood requests.");
                        n.setType("approval");
                        n.setRead(false);
                        notificationDAO.addNotification(n);
                    }
                    respMap.put("success", true);
                    respMap.put("message", "Donor approved successfully.");
                    respMap.put("approvalStatus", "approved");
                } else {
                    respMap.put("success", false);
                    respMap.put("message", "Failed to approve donor.");
                }
            }
        } else if ("reject".equals(action)) {
            String idStr = request.getParameter("id");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                boolean ok = donorDAO.updateApprovalStatus(id, "rejected");
                if (ok) {
                    Donor d = donorDAO.getDonorById(id);
                    if (d != null && d.getUserId() != 0) {
                        Notification n = new Notification();
                        n.setUserId(d.getUserId());
                        n.setTitle("Registration Rejected");
                        n.setMessage("Your donor registration request was not approved.");
                        n.setType("alert");
                        n.setRead(false);
                        notificationDAO.addNotification(n);
                    }
                    respMap.put("success", true);
                    respMap.put("message", "Donor registration rejected.");
                    respMap.put("approvalStatus", "rejected");
                } else {
                    respMap.put("success", false);
                    respMap.put("message", "Failed to reject donor.");
                }
            }
        } else if ("toggleAvailability".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("user") != null) {
                User user = (User) session.getAttribute("user");
                Donor d = donorDAO.getDonorByUserId(user.getId());
                if (d != null) {
                    boolean newStatus = !d.isAvailable();
                    donorDAO.updateAvailability(d.getId(), newStatus);
                    respMap.put("success", true);
                    respMap.put("available", newStatus);
                    respMap.put("message", "Availability updated to " + (newStatus ? "Available" : "Unavailable"));
                } else {
                    respMap.put("success", false);
                    respMap.put("message", "Donor profile not found.");
                }
            } else {
                respMap.put("success", false);
                respMap.put("message", "Unauthorized.");
            }
        } else if ("update".equals(action)) {
            String idStr = request.getParameter("id");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                Donor d = donorDAO.getDonorById(id);
                if (d != null) {
                    d.setName(request.getParameter("name"));
                    d.setAge(Integer.parseInt(request.getParameter("age")));
                    d.setGender(request.getParameter("gender"));
                    d.setCity(request.getParameter("city"));
                    d.setAddress(request.getParameter("address"));
                    d.setWeight(Integer.parseInt(request.getParameter("weight")));
                    d.setPhone(request.getParameter("phone"));
                    d.setMedicalConditions(request.getParameter("medicalConditions"));
                    if (request.getParameter("isAvailable") != null) {
                        d.setAvailable(Boolean.parseBoolean(request.getParameter("isAvailable")));
                    }

                    boolean ok = donorDAO.updateDonor(d);
                    respMap.put("success", ok);
                    respMap.put("message", ok ? "Donor profile updated successfully." : "Failed to update profile.");
                }
            }
        } else if ("delete".equals(action)) {
            String idStr = request.getParameter("id");
            String userIdStr = request.getParameter("userId");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                Donor d = donorDAO.getDonorById(id);
                if (d == null && userIdStr != null) {
                    d = donorDAO.getDonorByUserId(Integer.parseInt(userIdStr));
                }
                if (d == null) {
                    d = donorDAO.getDonorByUserId(id);
                }
                if (d != null) {
                    int donorId = d.getId();
                    int userId = d.getUserId();
                    boolean delDonor = donorDAO.deleteDonor(donorId);
                    boolean delUser = true;
                    if (userId != 0) {
                        delUser = userDAO.deleteUser(userId);
                    }
                    boolean ok = delDonor || delUser;
                    respMap.put("success", ok);
                    respMap.put("message", ok ? "Donor deleted successfully." : "Failed to delete donor record.");
                } else {
                    int uid = (userIdStr != null) ? Integer.parseInt(userIdStr) : id;
                    boolean ok = userDAO.deleteUser(uid);
                    respMap.put("success", ok);
                    respMap.put("message", ok ? "User deleted successfully." : "Donor not found.");
                }
            }
        }

        response.getWriter().write(gson.toJson(respMap));
    }
}
