package com.blooddonation.dao;

import com.blooddonation.model.ContactMessage;
import com.blooddonation.model.Notification;
import com.blooddonation.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContactMessageDAO {

    private NotificationDAO notificationDAO = new NotificationDAO();

    public boolean addContactMessage(ContactMessage msg) {
        String sql = "INSERT INTO contact_messages (name, email, message, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, msg.getName());
            ps.setString(2, msg.getEmail());
            ps.setString(3, msg.getMessage());
            ps.setString(4, msg.getStatus() != null ? msg.getStatus() : "unread");

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        msg.setId(rs.getInt(1));
                    }
                }

                // Notify admin (user_id = 1)
                Notification notif = new Notification();
                notif.setUserId(1);
                notif.setTitle("New Contact Message");
                notif.setMessage("From: " + msg.getName() + " (" + msg.getEmail() + ") - " + msg.getMessage());
                notif.setType("contact");
                notif.setRead(false);
                notificationDAO.addNotification(notif);

                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<ContactMessage> getAllContactMessages() {
        List<ContactMessage> list = new ArrayList<>();
        String sql = "SELECT * FROM contact_messages ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean markAsRead(int id) {
        String sql = "UPDATE contact_messages SET status = 'read' WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getUnreadCount() {
        String sql = "SELECT COUNT(*) FROM contact_messages WHERE status = 'unread'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private ContactMessage mapResultSet(ResultSet rs) throws SQLException {
        ContactMessage msg = new ContactMessage();
        msg.setId(rs.getInt("id"));
        msg.setName(rs.getString("name"));
        msg.setEmail(rs.getString("email"));
        msg.setMessage(rs.getString("message"));
        msg.setStatus(rs.getString("status"));
        msg.setCreatedAt(rs.getTimestamp("created_at"));
        return msg;
    }
}
