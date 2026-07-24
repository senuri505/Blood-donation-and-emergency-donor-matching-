package com.blooddonation.util;

public class BloodCompatibility {

    // Which donor blood types can give to which recipient blood types
    private static final String[][] COMPATIBILITY = {
        // Recipient, Compatible Donors
        {"A+",  "A+", "A-", "O+", "O-"},
        {"A-",  "A-", "O-"},
        {"B+",  "B+", "B-", "O+", "O-"},
        {"B-",  "B-", "O-"},
        {"AB+", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"},
        {"AB-", "A-", "B-", "AB-", "O-"},
        {"O+",  "O+", "O-"},
        {"O-",  "O-"}
    };

    public static boolean isCompatible(String donorType, String recipientType) {
        if (donorType == null || recipientType == null) return false;
        donorType = donorType.trim();
        recipientType = recipientType.trim();

        for (String[] row : COMPATIBILITY) {
            if (row[0].equalsIgnoreCase(recipientType)) {
                for (int i = 1; i < row.length; i++) {
                    if (row[i].equalsIgnoreCase(donorType)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static java.util.List<String> getCompatibleDonorTypes(String recipientType) {
        java.util.List<String> types = new java.util.ArrayList<>();
        if (recipientType == null) return types;
        recipientType = recipientType.trim();

        for (String[] row : COMPATIBILITY) {
            if (row[0].equalsIgnoreCase(recipientType)) {
                for (int i = 1; i < row.length; i++) {
                    types.add(row[i]);
                }
                break;
            }
        }
        return types;
    }
}
