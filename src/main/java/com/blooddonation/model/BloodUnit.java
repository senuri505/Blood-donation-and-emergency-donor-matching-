package com.blooddonation.model;

import java.sql.Date;

public class BloodUnit {
    private int id;
    private String bloodType;
    private String rhFactor;
    private int volumeMl;
    private Date collectedDate;
    private Date expiresDate;
    private Integer donorId;
    private String donorName;
    private String status;

    public BloodUnit() {}

    public BloodUnit(int id, String bloodType, String rhFactor, int volumeMl, Date collectedDate, Date expiresDate,
                     Integer donorId, String donorName, String status) {
        this.id = id;
        this.bloodType = bloodType;
        this.rhFactor = rhFactor;
        this.volumeMl = volumeMl;
        this.collectedDate = collectedDate;
        this.expiresDate = expiresDate;
        this.donorId = donorId;
        this.donorName = donorName;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public String getRhFactor() { return rhFactor; }
    public void setRhFactor(String rhFactor) { this.rhFactor = rhFactor; }

    public int getVolumeMl() { return volumeMl; }
    public void setVolumeMl(int volumeMl) { this.volumeMl = volumeMl; }

    public Date getCollectedDate() { return collectedDate; }
    public void setCollectedDate(Date collectedDate) { this.collectedDate = collectedDate; }

    public Date getExpiresDate() { return expiresDate; }
    public void setExpiresDate(Date expiresDate) { this.expiresDate = expiresDate; }

    public Integer getDonorId() { return donorId; }
    public void setDonorId(Integer donorId) { this.donorId = donorId; }

    public String getDonorName() { return donorName; }
    public void setDonorName(String donorName) { this.donorName = donorName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFullGroup() {
        return (bloodType != null ? bloodType : "") + (rhFactor != null ? rhFactor : "");
    }
}
