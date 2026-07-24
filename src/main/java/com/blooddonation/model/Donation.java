package com.blooddonation.model;

import java.sql.Timestamp;

public class Donation {
    private int id;
    private int requestId;
    private int donorId;
    private String donorName;
    private String bloodType;
    private String rhFactor;
    private int volumeMl;
    private Timestamp requestDate;
    private Timestamp donatedAt;
    private String hospitalName;
    private int hospitalId;
    private String status;
    private String hospitalNote;
    private String referenceId;
    private String notes;

    public Donation() {}

    public Donation(int id, int requestId, int donorId, String donorName, String bloodType, String rhFactor,
                    int volumeMl, Timestamp requestDate, Timestamp donatedAt, String hospitalName, int hospitalId,
                    String status, String hospitalNote, String referenceId, String notes) {
        this.id = id;
        this.requestId = requestId;
        this.donorId = donorId;
        this.donorName = donorName;
        this.bloodType = bloodType;
        this.rhFactor = rhFactor;
        this.volumeMl = volumeMl;
        this.requestDate = requestDate;
        this.donatedAt = donatedAt;
        this.hospitalName = hospitalName;
        this.hospitalId = hospitalId;
        this.status = status;
        this.hospitalNote = hospitalNote;
        this.referenceId = referenceId;
        this.notes = notes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public int getDonorId() { return donorId; }
    public void setDonorId(int donorId) { this.donorId = donorId; }

    public String getDonorName() { return donorName; }
    public void setDonorName(String donorName) { this.donorName = donorName; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public String getRhFactor() { return rhFactor; }
    public void setRhFactor(String rhFactor) { this.rhFactor = rhFactor; }

    public int getVolumeMl() { return volumeMl; }
    public void setVolumeMl(int volumeMl) { this.volumeMl = volumeMl; }

    public Timestamp getRequestDate() { return requestDate; }
    public void setRequestDate(Timestamp requestDate) { this.requestDate = requestDate; }

    public Timestamp getDonatedAt() { return donatedAt; }
    public void setDonatedAt(Timestamp donatedAt) { this.donatedAt = donatedAt; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public int getHospitalId() { return hospitalId; }
    public void setHospitalId(int hospitalId) { this.hospitalId = hospitalId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getHospitalNote() { return hospitalNote; }
    public void setHospitalNote(String hospitalNote) { this.hospitalNote = hospitalNote; }

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
