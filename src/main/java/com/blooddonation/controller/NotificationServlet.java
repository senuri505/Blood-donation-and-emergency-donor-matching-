package com.blooddonation.controller;

import com.blooddonation.dao.NotificationDAO;
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

@WebServlet("/NotificationServlet")
public class NotificationServlet extends HttpServlet {

    private NotificationDAO notificationDAO = new NotificationDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.getWriter().write("[]");
            return;
        }

        User user = (User) session.getAttribute("user");
        String action = request.getParameter("action");

        if ("unreadCount".equals(action)) {
            int count = notificationDAO.getUnreadCountByUserId(user.getId());
            Map<String, Integer> res = new HashMap<>();
            res.put("unreadCount", count);
            response.getWriter().write(gson.toJson(res));
            return;
        }

        List<Notification> list = notificationDAO.getNotificationsByUserId(user.getId());
        response.getWriter().write(gson.toJson(list));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.getWriter().write("{\"success\":false, \"message\":\"Unauthorized\"}");
            return;
        }

        User user = (User) session.getAttribute("user");
        String action = request.getParameter("action");
        Map<String, Object> respMap = new HashMap<>();

        if ("markRead".equals(action)) {
            String idStr = request.getParameter("id");
            if (idStr != null) {
                boolean ok = notificationDAO.markAsRead(Integer.parseInt(idStr));
                respMap.put("success", ok);
            }
        } else if ("markAllRead".equals(action)) {
            boolean ok = notificationDAO.markAllAsRead(user.getId());
            respMap.put("success", ok);
            respMap.put("message", "All notifications marked as read.");
        }

        response.getWriter().write(gson.toJson(respMap));
    }
}
