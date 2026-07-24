package com.blooddonation.model;

import java.sql.Date;
import java.sql.Timestamp;

public class BloodRequest {
    private int id;
    private String patientName;
    private String bloodType;
    private String rhFactor;
    private int unitsNeeded;
    private String urgency;
    private int hospitalId;
    private String hospitalName;
    private String city;
    private String contactPerson;
    private String phone;
    private String notes;
    private Date requiredDate;
    private String status;
    private String matchedDonorIds;
    private String donorResponses;
    private int acceptedUnits;
    private String acceptedDonorIds;
    private int assignedDonorId;
    private Donor assignedDonor;
    private Timestamp acceptedAt;
    private String hospitalNote;
    private String referenceId;
    private Timestamp createdAt;

    public BloodRequest() {}

    public BloodRequest(int id, String patientName, String bloodType, String rhFactor, int unitsNeeded, String urgency,
                        int hospitalId, String hospitalName, String city, String contactPerson, String phone, String notes,
                        Date requiredDate, String status, String matchedDonorIds, String donorResponses,
                        int acceptedUnits, String acceptedDonorIds, Timestamp acceptedAt, String hospitalNote, String referenceId, Timestamp createdAt) {
        this.id = id;
        this.patientName = patientName;
        this.bloodType = bloodType;
        this.rhFactor = rhFactor;
        this.unitsNeeded = unitsNeeded;
        this.urgency = urgency;
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.city = city;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.notes = notes;
        this.requiredDate = requiredDate;
        this.status = status;
        this.matchedDonorIds = matchedDonorIds;
        this.donorResponses = donorResponses;
        this.acceptedUnits = acceptedUnits;
        this.acceptedDonorIds = acceptedDonorIds;
        this.acceptedAt = acceptedAt;
        this.hospitalNote = hospitalNote;
        this.referenceId = referenceId;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public String getRhFactor() { return rhFactor; }
    public void setRhFactor(String rhFactor) { this.rhFactor = rhFactor; }

    public int getUnitsNeeded() { return unitsNeeded; }
    public void setUnitsNeeded(int unitsNeeded) { this.unitsNeeded = unitsNeeded; }

    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }

    public int getHospitalId() { return hospitalId; }
    public void setHospitalId(int hospitalId) { this.hospitalId = hospitalId; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Date getRequiredDate() { return requiredDate; }
    public void setRequiredDate(Date requiredDate) { this.requiredDate = requiredDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMatchedDonorIds() { return matchedDonorIds; }
    public void setMatchedDonorIds(String matchedDonorIds) { this.matchedDonorIds = matchedDonorIds; }

    public String getDonorResponses() { return donorResponses; }
    public void setDonorResponses(String donorResponses) { this.donorResponses = donorResponses; }

    public int getAcceptedUnits() { return acceptedUnits; }
    public void setAcceptedUnits(int acceptedUnits) { this.acceptedUnits = acceptedUnits; }

    public String getAcceptedDonorIds() { return acceptedDonorIds; }
    public void setAcceptedDonorIds(String acceptedDonorIds) { this.acceptedDonorIds = acceptedDonorIds; }

    public int getAssignedDonorId() { return assignedDonorId; }
    public void setAssignedDonorId(int assignedDonorId) { this.assignedDonorId = assignedDonorId; }

    public Donor getAssignedDonor() { return assignedDonor; }
    public void setAssignedDonor(Donor assignedDonor) { this.assignedDonor = assignedDonor; }

    public int getUnitsRemaining() { return Math.max(0, unitsNeeded - acceptedUnits); }

    public Timestamp getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(Timestamp acceptedAt) { this.acceptedAt = acceptedAt; }

    public String getHospitalNote() { return hospitalNote; }
    public void setHospitalNote(String hospitalNote) { this.hospitalNote = hospitalNote; }

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getFullBloodGroup() {
        return (bloodType != null ? bloodType : "") + (rhFactor != null ? rhFactor : "");
    }
}
