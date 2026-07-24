package com.blooddonation.controller;

import com.blooddonation.dao.DonorDAO;
import com.blooddonation.dao.HospitalDAO;
import com.blooddonation.dao.UserDAO;
import com.blooddonation.model.Donor;
import com.blooddonation.model.Hospital;
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
import java.util.Map;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private DonorDAO donorDAO = new DonorDAO();
    private HospitalDAO hospitalDAO = new HospitalDAO();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        Map<String, Object> respMap = new HashMap<>();

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            respMap.put("success", false);
            respMap.put("message", "Username and password are required.");
            response.getWriter().write(gson.toJson(respMap));
            return;
        }

        User user = userDAO.authenticate(username.trim(), password.trim());

        if (user != null) {
            if ("donor".equalsIgnoreCase(user.getRole())) {
                Donor donor = donorDAO.getDonorByUserId(user.getId());
                if (donor == null) {
                    respMap.put("success", false);
                    respMap.put("message", "Donor profile not found. Please contact admin.");
                    response.getWriter().write(gson.toJson(respMap));
                    return;
                }
                String status = donor.getApprovalStatus() != null ? donor.getApprovalStatus().trim().toLowerCase() : "awaiting";
                if ("rejected".equals(status)) {
                    respMap.put("success", false);
                    respMap.put("message", "Your account registration has been rejected.");
                    response.getWriter().write(gson.toJson(respMap));
                    return;
                }
                if (!"approved".equals(status)) {
                    respMap.put("success", false);
                    respMap.put("message", "Your account is waiting for admin approval.");
                    response.getWriter().write(gson.toJson(respMap));
                    return;
                }
            } else if ("hospital".equalsIgnoreCase(user.getRole())) {
                Hospital hospital = hospitalDAO.getHospitalByUserId(user.getId());
                if (hospital == null) {
                    respMap.put("success", false);
                    respMap.put("message", "Hospital profile not found. Please contact admin.");
                    response.getWriter().write(gson.toJson(respMap));
                    return;
                }
                String status = hospital.getApprovalStatus() != null ? hospital.getApprovalStatus().trim().toLowerCase() : "awaiting";
                if ("rejected".equals(status)) {
                    respMap.put("success", false);
                    respMap.put("message", "Your account registration has been rejected.");
                    response.getWriter().write(gson.toJson(respMap));
                    return;
                }
                if (!"approved".equals(status)) {
                    respMap.put("success", false);
                    respMap.put("message", "Your account is waiting for admin approval.");
                    response.getWriter().write(gson.toJson(respMap));
                    return;
                }
            }

            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);

            respMap.put("success", true);
            respMap.put("message", "Login successful.");
            respMap.put("role", user.getRole());
            respMap.put("username", user.getUsername());
            respMap.put("name", user.getName());
            respMap.put("userId", user.getId());

            String redirectUrl = "admin/dashboard.html";
            if ("donor".equalsIgnoreCase(user.getRole())) {
                redirectUrl = "donor/dashboard.html";
            } else if ("hospital".equalsIgnoreCase(user.getRole())) {
                redirectUrl = "hospital/dashboard.html";
            }
            respMap.put("redirect", redirectUrl);
        } else {
            respMap.put("success", false);
            respMap.put("message", "Invalid username or password.");
        }

        response.getWriter().write(gson.toJson(respMap));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Map<String, Object> respMap = new HashMap<>();

        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            respMap.put("authenticated", true);
            respMap.put("user", user);
        } else {
            respMap.put("authenticated", false);
        }

        response.getWriter().write(gson.toJson(respMap));
    }
}
