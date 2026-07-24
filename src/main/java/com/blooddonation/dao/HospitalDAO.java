package com.blooddonation.dao;

import com.blooddonation.model.Hospital;
import com.blooddonation.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HospitalDAO {

    public List<Hospital> getAllHospitals() {
        List<Hospital> list = new ArrayList<>();
        String sql = "SELECT * FROM hospitals ORDER BY id DESC";
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

    public List<Hospital> getApprovedHospitals() {
        List<Hospital> list = new ArrayList<>();
        String sql = "SELECT * FROM hospitals WHERE approval_status = 'approved' ORDER BY id DESC";
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

    public List<Hospital> getAwaitingApproval() {
        List<Hospital> list = new ArrayList<>();
        String sql = "SELECT * FROM hospitals WHERE approval_status = 'awaiting' ORDER BY id DESC";
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

    public Hospital getHospitalById(int id) {
        String sql = "SELECT * FROM hospitals WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Hospital getHospitalByUserId(int userId) {
        String sql = "SELECT * FROM hospitals WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addHospital(Hospital hospital) {
        String sql = "INSERT INTO hospitals (user_id, name, city, address, license, type, approval_status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, hospital.getUserId());
            ps.setString(2, hospital.getName());
            ps.setString(3, hospital.getCity());
            ps.setString(4, hospital.getAddress());
            ps.setString(5, hospital.getLicense());
            ps.setString(6, hospital.getType() != null ? hospital.getType() : "Private");
            ps.setString(7, hospital.getApprovalStatus() != null ? hospital.getApprovalStatus() : "awaiting");

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        hospital.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateHospital(Hospital hospital) {
        String sql = "UPDATE hospitals SET name=?, city=?, address=?, license=?, type=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hospital.getName());
            ps.setString(2, hospital.getCity());
            ps.setString(3, hospital.getAddress());
            ps.setString(4, hospital.getLicense());
            ps.setString(5, hospital.getType());
            ps.setInt(6, hospital.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateApprovalStatus(int id, String status) {
        String sql = "UPDATE hospitals SET approval_status = ? WHERE id = ?";
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

    public boolean deleteHospital(int id) {
        String sqlReqs = "DELETE FROM blood_requests WHERE hospital_id = ?";
        String sqlDonations = "UPDATE donations SET hospital_id = NULL WHERE hospital_id = ?";
        String sql = "DELETE FROM hospitals WHERE id = ?";
        try (Connection conn = DBConnection.getConnection()) {
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

    public int getTotalApprovedHospitalCount() {
        String sql = "SELECT COUNT(*) FROM hospitals WHERE approval_status = 'approved'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Hospital mapResultSet(ResultSet rs) throws SQLException {
        Hospital h = new Hospital();
        h.setId(rs.getInt("id"));
        h.setUserId(rs.getInt("user_id"));
        h.setName(rs.getString("name"));
        h.setCity(rs.getString("city"));
        h.setAddress(rs.getString("address"));
        h.setLicense(rs.getString("license"));
        h.setType(rs.getString("type"));
        h.setApprovalStatus(rs.getString("approval_status"));
        return h;
    }
}
