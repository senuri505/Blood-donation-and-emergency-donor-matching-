package com.blooddonation.controller;

import com.blooddonation.dao.SettingsDAO;
import com.blooddonation.model.Setting;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/SettingsServlet")
public class SettingsServlet extends HttpServlet {

    private SettingsDAO settingsDAO = new SettingsDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        List<Setting> list = settingsDAO.getAllSettings();
        response.getWriter().write(gson.toJson(list));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> respMap = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();

        boolean updated = false;
        while (paramNames.hasMoreElements()) {
            String key = paramNames.nextElement();
            String value = request.getParameter(key);
            if (key != null && !key.trim().isEmpty() && value != null) {
                settingsDAO.updateSetting(key.trim(), value.trim());
                updated = true;
            }
        }

        respMap.put("success", updated);
        respMap.put("message", updated ? "Settings updated successfully." : "No settings were changed.");
        response.getWriter().write(gson.toJson(respMap));
    }
}
