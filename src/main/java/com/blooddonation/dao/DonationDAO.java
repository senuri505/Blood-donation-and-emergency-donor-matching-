package com.blooddonation.dao;

import com.blooddonation.model.Donation;
import com.blooddonation.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonationDAO {

    public List<Donation> getAllDonations() {
        List<Donation> list = new ArrayList<>();
        String sql = "SELECT * FROM donations ORDER BY id DESC";
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

    public List<Donation> getDonationsByDonorId(int donorId) {
        List<Donation> list = new ArrayList<>();
        String sql = "SELECT * FROM donations WHERE donor_id = ? ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, donorId);
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

    public List<Donation> getDonationsByHospitalId(int hospitalId) {
        List<Donation> list = new ArrayList<>();
        String sql = "SELECT * FROM donations WHERE hospital_id = ? ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, hospitalId);
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

    public boolean addDonation(Donation donation) {
        String sql = "INSERT INTO donations (request_id, donor_id, donor_name, blood_type, rh_factor, volume_ml, request_date, donated_at, hospital_name, hospital_id, status, hospital_note, reference_id, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (donation.getRequestId() > 0) {
                ps.setInt(1, donation.getRequestId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setInt(2, donation.getDonorId());
            ps.setString(3, donation.getDonorName());
            ps.setString(4, donation.getBloodType());
            ps.setString(5, donation.getRhFactor());
            ps.setInt(6, donation.getVolumeMl() > 0 ? donation.getVolumeMl() : 450);
            ps.setTimestamp(7, donation.getRequestDate());
            ps.setTimestamp(8, donation.getDonatedAt() != null ? donation.getDonatedAt() : new Timestamp(System.currentTimeMillis()));
            ps.setString(9, donation.getHospitalName());
            if (donation.getHospitalId() > 0) {
                ps.setInt(10, donation.getHospitalId());
            } else {
                ps.setNull(10, Types.INTEGER);
            }
            ps.setString(11, donation.getStatus() != null ? donation.getStatus() : "completed");
            ps.setString(12, donation.getHospitalNote());
            ps.setString(13, donation.getReferenceId());
            ps.setString(14, donation.getNotes());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        donation.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getTotalDonationsCount() {
        String sql = "SELECT COUNT(*) FROM donations";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Donation mapResultSet(ResultSet rs) throws SQLException {
        Donation d = new Donation();
        d.setId(rs.getInt("id"));
        d.setRequestId(rs.getInt("request_id"));
        d.setDonorId(rs.getInt("donor_id"));
        d.setDonorName(rs.getString("donor_name"));
        d.setBloodType(rs.getString("blood_type"));
        d.setRhFactor(rs.getString("rh_factor"));
        d.setVolumeMl(rs.getInt("volume_ml"));
        d.setRequestDate(rs.getTimestamp("request_date"));
        d.setDonatedAt(rs.getTimestamp("donated_at"));
        d.setHospitalName(rs.getString("hospital_name"));
        d.setHospitalId(rs.getInt("hospital_id"));
        d.setStatus(rs.getString("status"));
        d.setHospitalNote(rs.getString("hospital_note"));
        d.setReferenceId(rs.getString("reference_id"));
        d.setNotes(rs.getString("notes"));
        return d;
    }
}
