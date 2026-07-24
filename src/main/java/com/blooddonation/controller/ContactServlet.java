package com.blooddonation.controller;

import com.blooddonation.dao.ContactMessageDAO;
import com.blooddonation.model.ContactMessage;
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

@WebServlet("/ContactServlet")
public class ContactServlet extends HttpServlet {

    private ContactMessageDAO contactDAO = new ContactMessageDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        List<ContactMessage> list = contactDAO.getAllContactMessages();
        response.getWriter().write(gson.toJson(list));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        Map<String, Object> respMap = new HashMap<>();

        if ("markRead".equals(action)) {
            String idStr = request.getParameter("id");
            if (idStr != null) {
                boolean ok = contactDAO.markAsRead(Integer.parseInt(idStr));
                respMap.put("success", ok);
                respMap.put("message", "Message marked as read.");
            } else {
                respMap.put("success", false);
                respMap.put("message", "Invalid message ID.");
            }
        } else {
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String message = request.getParameter("message");

            if (name == null || name.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                message == null || message.trim().isEmpty()) {
                respMap.put("success", false);
                respMap.put("message", "Please fill in all required fields.");
            } else {
                ContactMessage msg = new ContactMessage();
                msg.setName(name.trim());
                msg.setEmail(email.trim());
                msg.setMessage(message.trim());
                msg.setStatus("unread");

                boolean ok = contactDAO.addContactMessage(msg);
                if (ok) {
                    respMap.put("success", true);
                    respMap.put("message", "Your message has been sent successfully.");
                } else {
                    respMap.put("success", false);
                    respMap.put("message", "Failed to submit contact message. Please try again.");
                }
            }
        }

        response.getWriter().write(gson.toJson(respMap));
    }
}
