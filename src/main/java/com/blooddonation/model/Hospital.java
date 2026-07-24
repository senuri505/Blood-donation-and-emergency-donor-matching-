package com.blooddonation.model;

public class Hospital {
    private int id;
    private int userId;
    private String name;
    private String city;
    private String address;
    private String license;
    private String type;
    private String approvalStatus;

    public Hospital() {}

    public Hospital(int id, int userId, String name, String city, String address, String license, String type, String approvalStatus) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.city = city;
        this.address = address;
        this.license = license;
        this.type = type;
        this.approvalStatus = approvalStatus;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getLicense() { return license; }
    public void setLicense(String license) { this.license = license; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }
}
