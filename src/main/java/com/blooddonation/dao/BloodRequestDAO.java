package com.blooddonation.dao;

import com.blooddonation.model.BloodRequest;
import com.blooddonation.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BloodRequestDAO {

    public List<BloodRequest> getAllRequests() {
        List<BloodRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM blood_requests ORDER BY id DESC";
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

    public List<BloodRequest> getRequestsByHospitalId(int hospitalId) {
        List<BloodRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM blood_requests WHERE hospital_id = ? ORDER BY id DESC";
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

    public BloodRequest getRequestById(int id) {
        String sql = "SELECT * FROM blood_requests WHERE id = ?";
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

    public boolean addRequest(BloodRequest request) {
        String sql = "INSERT INTO blood_requests (patient_name, blood_type, rh_factor, units_needed, urgency, hospital_id, hospital_name, city, contact_person, phone, notes, required_date, status, accepted_units, accepted_donor_ids, reference_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, request.getPatientName());
            ps.setString(2, request.getBloodType());
            ps.setString(3, request.getRhFactor());
            ps.setInt(4, request.getUnitsNeeded());
            ps.setString(5, request.getUrgency() != null ? request.getUrgency() : "routine");
            ps.setInt(6, request.getHospitalId());
            ps.setString(7, request.getHospitalName());
            ps.setString(8, request.getCity());
            ps.setString(9, request.getContactPerson());
            ps.setString(10, request.getPhone());
            ps.setString(11, request.getNotes());
            ps.setDate(12, request.getRequiredDate());
            ps.setString(13, request.getStatus() != null ? request.getStatus() : "pending");
            ps.setInt(14, request.getAcceptedUnits());
            ps.setString(15, request.getAcceptedDonorIds());
            ps.setString(16, request.getReferenceId());
 
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        request.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE blood_requests SET status = ? WHERE id = ?";
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

    public boolean updateMatchedDonors(int id, String matchedDonorIds, String status) {
        String sql = "UPDATE blood_requests SET matched_donor_ids = ?, status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matchedDonorIds);
            ps.setString(2, status);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
 
    public boolean updateAcceptance(int requestId, int acceptedUnits, String acceptedDonorIds, String status, String donorResponses, Timestamp acceptedAt) {
        String sql = "UPDATE blood_requests SET accepted_units = ?, accepted_donor_ids = ?, status = ?, donor_responses = ?, accepted_at = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, acceptedUnits);
            ps.setString(2, acceptedDonorIds);
            ps.setString(3, status);
            ps.setString(4, donorResponses);
            ps.setTimestamp(5, acceptedAt);
            ps.setInt(6, requestId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
 
    public boolean updateHospitalNoteAndStatus(int requestId, String hospitalNote, String status) {
        String sql = "UPDATE blood_requests SET hospital_note = ?, status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hospitalNote);
            ps.setString(2, status);
            ps.setInt(3, requestId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
 
    public boolean updateDonorResponse(int requestId, int donorId, String response) {
        BloodRequest req = getRequestById(requestId);
        if (req == null) return false;

        String currentResponses = req.getDonorResponses();
        StringBuilder sb = new StringBuilder();
        if (currentResponses != null && !currentResponses.trim().isEmpty()) {
            sb.append(currentResponses).append(";");
        }
        sb.append("donor_").append(donorId).append(":").append(response);

        String sql = "UPDATE blood_requests SET donor_responses = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sb.toString());
            ps.setInt(2, requestId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getActiveRequestCount() {
        String sql = "SELECT COUNT(*) FROM blood_requests WHERE status IN ('pending', 'matching', 'accepted', 'fulfilled')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
 
    public int getCriticalRequestCount() {
        String sql = "SELECT COUNT(*) FROM blood_requests WHERE urgency = 'critical' AND status IN ('pending', 'matching', 'accepted', 'fulfilled')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean assignFirstDonor(int requestId, int donorId) {
        String sql = "UPDATE blood_requests SET status = 'fulfilled', assigned_donor_id = ?, accepted_units = 1, accepted_donor_ids = ?, accepted_at = NOW() WHERE id = ? AND (assigned_donor_id IS NULL OR assigned_donor_id = 0) AND status IN ('pending', 'matching')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, donorId);
            ps.setString(2, String.valueOf(donorId));
            ps.setInt(3, requestId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private BloodRequest mapResultSet(ResultSet rs) throws SQLException {
        BloodRequest req = new BloodRequest();
        req.setId(rs.getInt("id"));
        req.setPatientName(rs.getString("patient_name"));
        req.setBloodType(rs.getString("blood_type"));
        req.setRhFactor(rs.getString("rh_factor"));
        req.setUnitsNeeded(rs.getInt("units_needed"));
        req.setUrgency(rs.getString("urgency"));
        req.setHospitalId(rs.getInt("hospital_id"));
        req.setHospitalName(rs.getString("hospital_name"));
        req.setCity(rs.getString("city"));
        req.setContactPerson(rs.getString("contact_person"));
        req.setPhone(rs.getString("phone"));
        req.setNotes(rs.getString("notes"));
        req.setRequiredDate(rs.getDate("required_date"));
        req.setStatus(rs.getString("status"));
        req.setMatchedDonorIds(rs.getString("matched_donor_ids"));
        req.setDonorResponses(rs.getString("donor_responses"));
        req.setAcceptedUnits(rs.getInt("accepted_units"));
        req.setAcceptedDonorIds(rs.getString("accepted_donor_ids"));
        try {
            req.setAssignedDonorId(rs.getInt("assigned_donor_id"));
        } catch (SQLException ignored) {}
        req.setAcceptedAt(rs.getTimestamp("accepted_at"));
        req.setHospitalNote(rs.getString("hospital_note"));
        req.setReferenceId(rs.getString("reference_id"));
        req.setCreatedAt(rs.getTimestamp("created_at"));
        return req;
    }
}
