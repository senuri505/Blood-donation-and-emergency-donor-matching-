package com.blooddonation.model;

import java.sql.Date;

public class Donor {
    private int id;
    private int userId;
    private String name;
    private String bloodType;
    private String rhFactor;
    private int age;
    private String gender;
    private String city;
    private String address;
    private int weight;
    private String phone;
    private String medicalConditions;
    private Date lastDonationDate;
    private boolean isAvailable;
    private int totalDonations;
    private String approvalStatus;
    private String email;

    public Donor() {}

    public Donor(int id, int userId, String name, String bloodType, String rhFactor, int age, String gender,
                 String city, String address, int weight, String phone, String medicalConditions,
                 Date lastDonationDate, boolean isAvailable, int totalDonations, String approvalStatus) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.bloodType = bloodType;
        this.rhFactor = rhFactor;
        this.age = age;
        this.gender = gender;
        this.city = city;
        this.address = address;
        this.weight = weight;
        this.phone = phone;
        this.medicalConditions = medicalConditions;
        this.lastDonationDate = lastDonationDate;
        this.isAvailable = isAvailable;
        this.totalDonations = totalDonations;
        this.approvalStatus = approvalStatus;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public String getRhFactor() { return rhFactor; }
    public void setRhFactor(String rhFactor) { this.rhFactor = rhFactor; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getMedicalConditions() { return medicalConditions; }
    public void setMedicalConditions(String medicalConditions) { this.medicalConditions = medicalConditions; }

    public Date getLastDonationDate() { return lastDonationDate; }
    public void setLastDonationDate(Date lastDonationDate) { this.lastDonationDate = lastDonationDate; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public int getTotalDonations() { return totalDonations; }
    public void setTotalDonations(int totalDonations) { this.totalDonations = totalDonations; }

    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullBloodGroup() {
        return (bloodType != null ? bloodType : "") + (rhFactor != null ? rhFactor : "");
    }
}
