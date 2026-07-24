package com.blooddonation.dao;

import com.blooddonation.model.Donor;
import com.blooddonation.util.BloodCompatibility;
import com.blooddonation.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DonorDAO {

    public List<Donor> getAllDonors() {
        List<Donor> list = new ArrayList<>();
        String sql = "SELECT * FROM donors ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Donor> getApprovedDonors() {
        List<Donor> list = new ArrayList<>();
        String sql = "SELECT * FROM donors WHERE approval_status = 'approved' ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Donor> getCompatibleDonors(String recipientType, String city, int cooldownDays) {
        List<Donor> list = new ArrayList<>();
        java.util.List<String> compatibleTypes = BloodCompatibility.getCompatibleDonorTypes(recipientType);
        if (compatibleTypes.isEmpty()) {
            return list;
        }

        StringBuilder sql = new StringBuilder("SELECT * FROM donors WHERE approval_status = 'approved' AND is_available = TRUE AND (last_donation_date IS NULL OR DATE_ADD(last_donation_date, INTERVAL ? DAY) <= CURRENT_DATE()) AND CONCAT(blood_type, rh_factor) IN (");
        for (int i = 0; i < compatibleTypes.size(); i++) {
            if (i > 0) sql.append(",");
            sql.append("?");
        }
        sql.append(")");
        if (city != null && !city.trim().isEmpty()) {
            sql.append(" AND LOWER(city) LIKE ?");
        }
        sql.append(" ORDER BY id DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setInt(idx++, cooldownDays);
            for (String type : compatibleTypes) {
                ps.setString(idx++, type);
            }
            if (city != null && !city.trim().isEmpty()) {
                ps.setString(idx++, "%" + city.trim().toLowerCase() + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Donor> getAwaitingApproval() {
        List<Donor> list = new ArrayList<>();
        String sql = "SELECT * FROM donors WHERE approval_status = 'awaiting' ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Donor getDonorById(int id) {
        String sql = "SELECT d.*, u.email FROM donors d LEFT JOIN users u ON d.user_id = u.id WHERE d.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Donor d = mapResultSet(rs);
                    try { d.setEmail(rs.getString("email")); } catch (SQLException ignored) {}
                    return d;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Donor getDonorByUserId(int userId) {
        String sql = "SELECT d.*, u.email FROM donors d LEFT JOIN users u ON d.user_id = u.id WHERE d.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Donor d = mapResultSet(rs);
                    try { d.setEmail(rs.getString("email")); } catch (SQLException ignored) {}
                    return d;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Donor> searchDonors(String bloodType, String rhFactor, String city, String status) {
        List<Donor> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM donors WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (bloodType != null && !bloodType.trim().isEmpty()) {
            sql.append(" AND blood_type = ?");
            params.add(bloodType.trim());
        }
        if (rhFactor != null && !rhFactor.trim().isEmpty()) {
            sql.append(" AND rh_factor = ?");
            params.add(rhFactor.trim());
        }
        if (city != null && !city.trim().isEmpty()) {
            sql.append(" AND LOWER(city) LIKE ?");
            params.add("%" + city.trim().toLowerCase() + "%");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND approval_status = ?");
            params.add(status.trim());
        }

        sql.append(" ORDER BY id DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addDonor(Donor donor) {
        String sql = "INSERT INTO donors (user_id, name, blood_type, rh_factor, age, gender, city, address, weight, phone, medical_conditions, last_donation_date, is_available, total_donations, approval_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, donor.getUserId());
            ps.setString(2, donor.getName());
            ps.setString(3, donor.getBloodType());
            ps.setString(4, donor.getRhFactor());
            ps.setInt(5, donor.getAge());
            ps.setString(6, donor.getGender());
            ps.setString(7, donor.getCity());
            ps.setString(8, donor.getAddress());
            ps.setInt(9, donor.getWeight());
            ps.setString(10, donor.getPhone());
            ps.setString(11, donor.getMedicalConditions());
            ps.setDate(12, donor.getLastDonationDate());
            ps.setBoolean(13, donor.isAvailable());
            ps.setInt(14, donor.getTotalDonations());
            ps.setString(15, donor.getApprovalStatus() != null ? donor.getApprovalStatus() : "awaiting");

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        donor.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateDonor(Donor donor) {
        String sql = "UPDATE donors SET name=?, age=?, gender=?, city=?, address=?, weight=?, phone=?, medical_conditions=?, is_available=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, donor.getName());
            ps.setInt(2, donor.getAge());
            ps.setString(3, donor.getGender());
            ps.setString(4, donor.getCity());
            ps.setString(5, donor.getAddress());
            ps.setInt(6, donor.getWeight());
            ps.setString(7, donor.getPhone());
            ps.setString(8, donor.getMedicalConditions());
            ps.setBoolean(9, donor.isAvailable());
            ps.setInt(10, donor.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateApprovalStatus(int id, String status) {
        String sql = "UPDATE donors SET approval_status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateAvailability(int id, boolean isAvailable) {
        String sql = "UPDATE donors SET is_available = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, isAvailable);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateDonationHistory(int donorId, Date lastDonationDate) {
        String sql = "UPDATE donors SET last_donation_date = ?, total_donations = total_donations + 1 WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, lastDonationDate);
            ps.setInt(2, donorId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteDonor(int id) {
        String sqlUnits = "UPDATE blood_units SET donor_id = NULL WHERE donor_id = ?";
        String sqlReqs = "UPDATE blood_requests SET assigned_donor_id = NULL WHERE assigned_donor_id = ?";
        String sqlDonations = "DELETE FROM donations WHERE donor_id = ?";
        String sql = "DELETE FROM donors WHERE id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sqlUnits)) { ps.setInt(1, id); ps.executeUpdate(); } catch (SQLException ignored) {}
            try (PreparedStatement ps = conn.prepareStatement(sqlReqs)) { ps.setInt(1, id); ps.executeUpdate(); } catch (SQLException ignored) {}
            try (PreparedStatement ps = conn.prepareStatement(sqlDonations)) { ps.setInt(1, id); ps.executeUpdate(); } catch (SQLException ignored) {}
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Map<String, Integer> getDonorCountByBloodGroup() {
        Map<String, Integer> counts = new HashMap<>();
        String[] groups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        for (String g : groups) counts.put(g, 0);

        String sql = "SELECT CONCAT(blood_type, rh_factor) AS full_group, COUNT(*) AS cnt FROM donors WHERE approval_status = 'approved' GROUP BY blood_type, rh_factor";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                counts.put(rs.getString("full_group"), rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return counts;
    }

    public int getTotalApprovedDonorCount() {
        String sql = "SELECT COUNT(*) FROM donors WHERE approval_status = 'approved'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Donor mapResultSet(ResultSet rs) throws SQLException {
        Donor d = new Donor();
        d.setId(rs.getInt("id"));
        d.setUserId(rs.getInt("user_id"));
        d.setName(rs.getString("name"));
        d.setBloodType(rs.getString("blood_type"));
        d.setRhFactor(rs.getString("rh_factor"));
        d.setAge(rs.getInt("age"));
        d.setGender(rs.getString("gender"));
        d.setCity(rs.getString("city"));
        d.setAddress(rs.getString("address"));
        d.setWeight(rs.getInt("weight"));
        d.setPhone(rs.getString("phone"));
        d.setMedicalConditions(rs.getString("medical_conditions"));
        d.setLastDonationDate(rs.getDate("last_donation_date"));
        d.setAvailable(rs.getBoolean("is_available"));
        d.setTotalDonations(rs.getInt("total_donations"));
        d.setApprovalStatus(rs.getString("approval_status"));
        return d;
    }
}
