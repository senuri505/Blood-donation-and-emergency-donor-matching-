package com.blooddonation.dao;

import com.blooddonation.model.BloodUnit;
import com.blooddonation.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BloodStockDAO {

    public List<BloodUnit> getAllBloodUnits() {
        List<BloodUnit> list = new ArrayList<>();
        String sql = "SELECT * FROM blood_units ORDER BY expires_date ASC";
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

    public List<BloodUnit> getAvailableUnits() {
        List<BloodUnit> list = new ArrayList<>();
        String sql = "SELECT * FROM blood_units WHERE status = 'available' AND expires_date >= CURRENT_DATE() ORDER BY expires_date ASC";
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

    public Map<String, Integer> getStockSummaryByGroup() {
        Map<String, Integer> counts = new HashMap<>();
        String[] groups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        for (String g : groups) counts.put(g, 0);

        String sql = "SELECT CONCAT(blood_type, rh_factor) AS full_group, COUNT(*) AS cnt FROM blood_units WHERE status = 'available' AND expires_date >= CURRENT_DATE() GROUP BY blood_type, rh_factor";
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

    public int getAvailableStockCount(String bloodType, String rhFactor) {
        String sql = "SELECT COUNT(*) FROM blood_units WHERE blood_type = ? AND rh_factor = ? AND status = 'available' AND expires_date >= CURRENT_DATE()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bloodType);
            ps.setString(2, rhFactor);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalAvailableUnits() {
        String sql = "SELECT COUNT(*) FROM blood_units WHERE status = 'available' AND expires_date >= CURRENT_DATE()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean addBloodUnit(BloodUnit unit) {
        String sql = "INSERT INTO blood_units (blood_type, rh_factor, volume_ml, collected_date, expires_date, donor_id, donor_name, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, unit.getBloodType());
            ps.setString(2, unit.getRhFactor());
            ps.setInt(3, unit.getVolumeMl() > 0 ? unit.getVolumeMl() : 450);
            ps.setDate(4, unit.getCollectedDate());
            ps.setDate(5, unit.getExpiresDate());
            if (unit.getDonorId() != null && unit.getDonorId() > 0) {
                ps.setInt(6, unit.getDonorId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.setString(7, unit.getDonorName() != null ? unit.getDonorName() : "Walk-in");
            ps.setString(8, unit.getStatus() != null ? unit.getStatus() : "available");

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        unit.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean consumeStock(String bloodType, String rhFactor, int unitsNeeded) {
        // First check if sufficient available stock exists
        int currentCount = getAvailableStockCount(bloodType, rhFactor);
        if (currentCount < unitsNeeded) {
            return false;
        }

        // Update status of oldest expiring available units to 'used'
        String sql = "UPDATE blood_units SET status = 'used' WHERE blood_type = ? AND rh_factor = ? AND status = 'available' AND expires_date >= CURRENT_DATE() ORDER BY expires_date ASC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bloodType);
            ps.setString(2, rhFactor);
            ps.setInt(3, unitsNeeded);
            return ps.executeUpdate() == unitsNeeded;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateExpiredUnits() {
        String sql = "UPDATE blood_units SET status = 'expired' WHERE expires_date < CURRENT_DATE() AND status = 'available'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private BloodUnit mapResultSet(ResultSet rs) throws SQLException {
        BloodUnit u = new BloodUnit();
        u.setId(rs.getInt("id"));
        u.setBloodType(rs.getString("blood_type"));
        u.setRhFactor(rs.getString("rh_factor"));
        u.setVolumeMl(rs.getInt("volume_ml"));
        u.setCollectedDate(rs.getDate("collected_date"));
        u.setExpiresDate(rs.getDate("expires_date"));
        int donorId = rs.getInt("donor_id");
        if (!rs.wasNull()) {
            u.setDonorId(donorId);
        }
        u.setDonorName(rs.getString("donor_name"));
        u.setStatus(rs.getString("status"));
        return u;
    }
}
