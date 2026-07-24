package com.blooddonation.dao;

import com.blooddonation.model.Setting;
import com.blooddonation.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsDAO {

    public List<Setting> getAllSettings() {
        List<Setting> list = new ArrayList<>();
        String sql = "SELECT * FROM settings ORDER BY id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Setting(rs.getInt("id"), rs.getString("setting_key"), rs.getString("setting_value")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Map<String, String> getSettingsMap() {
        Map<String, String> map = new HashMap<>();
        String sql = "SELECT setting_key, setting_value FROM settings";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("setting_key"), rs.getString("setting_value"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public Setting getSetting(String key) {
        String sql = "SELECT * FROM settings WHERE setting_key = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Setting(rs.getInt("id"), rs.getString("setting_key"), rs.getString("setting_value"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getValue(String key, String defaultValue) {
        Setting s = getSetting(key);
        return (s != null && s.getSettingValue() != null) ? s.getSettingValue() : defaultValue;
    }

    public boolean updateSetting(String key, String value) {
        String sql = "UPDATE settings SET setting_value = ? WHERE setting_key = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, value);
            ps.setString(2, key);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                // Insert if not exists
                String insertSql = "INSERT INTO settings (setting_key, setting_value) VALUES (?, ?)";
                try (PreparedStatement ips = conn.prepareStatement(insertSql)) {
                    ips.setString(1, key);
                    ips.setString(2, value);
                    return ips.executeUpdate() > 0;
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
