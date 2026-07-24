package com.blooddonation.controller;

import com.blooddonation.dao.HospitalDAO;
import com.blooddonation.dao.NotificationDAO;
import com.blooddonation.dao.UserDAO;
import com.blooddonation.model.Hospital;
import com.blooddonation.model.Notification;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/HospitalServlet")
public class HospitalServlet extends HttpServlet {

    private HospitalDAO hospitalDAO = new HospitalDAO();
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

        String city = request.getParameter("city");
        List<Hospital> list;

        if ("awaiting".equals(action)) {
            list = hospitalDAO.getAwaitingApproval();
        } else if ("approved".equals(action)) {
            list = hospitalDAO.getApprovedHospitals();
        } else if ("get".equals(action)) {
            String idStr = request.getParameter("id");
            if (idStr != null) {
                Hospital h = hospitalDAO.getHospitalById(Integer.parseInt(idStr));
                response.getWriter().write(gson.toJson(h));
                return;
            }
            list = hospitalDAO.getAllHospitals();
        } else {
            list = hospitalDAO.getAllHospitals();
        }

        if (city != null && !city.trim().isEmpty()) {
            String targetCity = city.trim().toLowerCase();
            list = list.stream()
                    .filter(h -> h.getCity() != null && h.getCity().toLowerCase().contains(targetCity))
                    .collect(java.util.stream.Collectors.toList());
        }

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
                boolean ok = hospitalDAO.updateApprovalStatus(id, "approved");
                if (ok) {
                    Hospital h = hospitalDAO.getHospitalById(id);
                    if (h != null && h.getUserId() != 0) {
                        Notification n = new Notification();
                        n.setUserId(h.getUserId());
                        n.setTitle("Hospital Account Approved");
                        n.setMessage("Your hospital registration has been approved. You can now post blood requests.");
                        n.setType("approval");
                        notificationDAO.addNotification(n);
                    }
                    respMap.put("success", true);
                    respMap.put("message", "Hospital approved successfully.");
                } else {
                    respMap.put("success", false);
                    respMap.put("message", "Failed to approve hospital.");
                }
            }
        } else if ("reject".equals(action)) {
            String idStr = request.getParameter("id");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                boolean ok = hospitalDAO.updateApprovalStatus(id, "rejected");
                if (ok) {
                    Hospital h = hospitalDAO.getHospitalById(id);
                    if (h != null && h.getUserId() != 0) {
                        Notification n = new Notification();
                        n.setUserId(h.getUserId());
                        n.setTitle("Hospital Account Rejected");
                        n.setMessage("Your hospital registration request was not approved.");
                        n.setType("alert");
                        notificationDAO.addNotification(n);
                    }
                    respMap.put("success", true);
                    respMap.put("message", "Hospital registration rejected.");
                } else {
                    respMap.put("success", false);
                    respMap.put("message", "Failed to reject hospital.");
                }
            }
        } else if ("update".equals(action)) {
            String idStr = request.getParameter("id");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                Hospital h = hospitalDAO.getHospitalById(id);
                if (h != null) {
                    h.setName(request.getParameter("name"));
                    h.setCity(request.getParameter("city"));
                    h.setAddress(request.getParameter("address"));
                    h.setLicense(request.getParameter("license"));
                    h.setType(request.getParameter("type"));

                    boolean ok = hospitalDAO.updateHospital(h);
                    respMap.put("success", ok);
                    respMap.put("message", ok ? "Hospital details updated." : "Failed to update hospital.");
                }
            }
        } else if ("delete".equals(action)) {
            String idStr = request.getParameter("id");
            String userIdStr = request.getParameter("userId");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                Hospital h = hospitalDAO.getHospitalById(id);
                if (h == null && userIdStr != null) {
                    h = hospitalDAO.getHospitalByUserId(Integer.parseInt(userIdStr));
                }
                if (h == null) {
                    h = hospitalDAO.getHospitalByUserId(id);
                }
                if (h != null) {
                    int hospitalId = h.getId();
                    int userId = h.getUserId();
                    boolean delHosp = hospitalDAO.deleteHospital(hospitalId);
                    boolean delUser = true;
                    if (userId != 0) {
                        delUser = userDAO.deleteUser(userId);
                    }
                    boolean ok = delHosp || delUser;
                    respMap.put("success", ok);
                    respMap.put("message", ok ? "Hospital deleted successfully." : "Failed to delete hospital record.");
                } else {
                    int uid = (userIdStr != null) ? Integer.parseInt(userIdStr) : id;
                    boolean ok = userDAO.deleteUser(uid);
                    respMap.put("success", ok);
                    respMap.put("message", ok ? "User deleted successfully." : "Hospital not found.");
                }
            }
        }

        response.getWriter().write(gson.toJson(respMap));
    }
}
