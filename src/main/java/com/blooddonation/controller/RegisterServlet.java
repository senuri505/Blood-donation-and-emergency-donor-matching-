package com.blooddonation.controller;

import com.blooddonation.dao.DonorDAO;
import com.blooddonation.dao.HospitalDAO;
import com.blooddonation.dao.NotificationDAO;
import com.blooddonation.dao.UserDAO;
import com.blooddonation.model.Donor;
import com.blooddonation.model.Hospital;
import com.blooddonation.model.Notification;
import com.blooddonation.model.User;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private DonorDAO donorDAO = new DonorDAO();
    private HospitalDAO hospitalDAO = new HospitalDAO();
    private NotificationDAO notificationDAO = new NotificationDAO();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> respMap = new HashMap<>();

        String role = request.getParameter("role");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

        // Common validation
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            name == null || name.trim().isEmpty() ||
            role == null || role.trim().isEmpty()) {
            respMap.put("success", false);
            respMap.put("message", "Please fill in all required user fields.");
            response.getWriter().write(gson.toJson(respMap));
            return;
        }

        // Check if username exists
        if (userDAO.getUserByUsername(username.trim()) != null) {
            respMap.put("success", false);
            respMap.put("message", "Username is already taken.");
            response.getWriter().write(gson.toJson(respMap));
            return;
        }

        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(password.trim());
        user.setRole(role.trim().toLowerCase());
        user.setName(name.trim());
        user.setEmail(email != null ? email.trim() : "");
        user.setPhone(phone != null ? phone.trim() : "");

        if ("donor".equalsIgnoreCase(role)) {
            // Donor validation
            String bloodType = request.getParameter("bloodType");
            String rhFactor = request.getParameter("rhFactor");
            String ageStr = request.getParameter("age");
            String gender = request.getParameter("gender");
            String weightStr = request.getParameter("weight");
            String city = request.getParameter("city");
            String address = request.getParameter("address");
            String medicalConditions = request.getParameter("medicalConditions");

            if (bloodType == null || rhFactor == null || ageStr == null || weightStr == null || city == null) {
                respMap.put("success", false);
                respMap.put("message", "Please fill in all required donor registration fields.");
                response.getWriter().write(gson.toJson(respMap));
                return;
            }

            int age = 0;
            int weight = 0;
            try {
                age = Integer.parseInt(ageStr);
                weight = Integer.parseInt(weightStr);
            } catch (NumberFormatException e) {
                respMap.put("success", false);
                respMap.put("message", "Invalid age or weight format.");
                response.getWriter().write(gson.toJson(respMap));
                return;
            }

            if (age < 18 || age > 65) {
                respMap.put("success", false);
                respMap.put("message", "Donor age must be between 18 and 65.");
                response.getWriter().write(gson.toJson(respMap));
                return;
            }
            if (weight < 50) {
                respMap.put("success", false);
                respMap.put("message", "Minimum weight requirement for donor is 50 kg.");
                response.getWriter().write(gson.toJson(respMap));
                return;
            }

            int userId = userDAO.createUser(user);
            if (userId > 0) {
                Donor donor = new Donor();
                donor.setUserId(userId);
                donor.setName(name.trim());
                donor.setBloodType(bloodType.trim());
                donor.setRhFactor(rhFactor.trim());
                donor.setAge(age);
                donor.setGender(gender != null ? gender.trim() : "Male");
                donor.setCity(city.trim());
                donor.setAddress(address != null ? address.trim() : "");
                donor.setWeight(weight);
                donor.setPhone(phone != null ? phone.trim() : "");
                donor.setMedicalConditions(medicalConditions != null ? medicalConditions.trim() : "None");
                donor.setAvailable(true);
                donor.setTotalDonations(0);
                donor.setApprovalStatus("awaiting");

                donorDAO.addDonor(donor);

                // Notify admin
                Notification notif = new Notification();
                notif.setUserId(1); // Admin user ID
                notif.setTitle("New Donor Registration");
                notif.setMessage(name + " (" + bloodType + rhFactor + ") registered as a donor and is awaiting approval.");
                notif.setType("approval");
                notif.setRead(false);
                notificationDAO.addNotification(notif);

                respMap.put("success", true);
                respMap.put("message", "Donor registration submitted successfully! Awaiting admin approval.");
            } else {
                respMap.put("success", false);
                respMap.put("message", "Failed to create user account.");
            }

        } else if ("hospital".equalsIgnoreCase(role)) {
            String city = request.getParameter("city");
            String address = request.getParameter("address");
            String license = request.getParameter("license");
            String type = request.getParameter("type");

            if (city == null || license == null) {
                respMap.put("success", false);
                respMap.put("message", "Please fill in all required hospital fields.");
                response.getWriter().write(gson.toJson(respMap));
                return;
            }

            int userId = userDAO.createUser(user);
            if (userId > 0) {
                Hospital hospital = new Hospital();
                hospital.setUserId(userId);
                hospital.setName(name.trim());
                hospital.setCity(city.trim());
                hospital.setAddress(address != null ? address.trim() : "");
                hospital.setLicense(license.trim());
                hospital.setType(type != null ? type.trim() : "Private");
                hospital.setApprovalStatus("awaiting");

                hospitalDAO.addHospital(hospital);

                // Notify admin
                Notification notif = new Notification();
                notif.setUserId(1); // Admin user ID
                notif.setTitle("New Hospital Registration");
                notif.setMessage(name + " registered as a hospital and is awaiting approval.");
                notif.setType("approval");
                notif.setRead(false);
                notificationDAO.addNotification(notif);

                respMap.put("success", true);
                respMap.put("message", "Hospital registration submitted successfully! Awaiting admin approval.");
            } else {
                respMap.put("success", false);
                respMap.put("message", "Failed to create user account.");
            }
        } else {
            respMap.put("success", false);
            respMap.put("message", "Invalid registration role specified.");
        }

        response.getWriter().write(gson.toJson(respMap));
    }
}
