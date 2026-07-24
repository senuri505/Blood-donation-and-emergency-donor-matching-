package com.blooddonation.controller;

import com.blooddonation.dao.BloodRequestDAO;
import com.blooddonation.dao.BloodStockDAO;
import com.blooddonation.dao.DonationDAO;
import com.blooddonation.dao.DonorDAO;
import com.blooddonation.dao.HospitalDAO;
import com.blooddonation.dao.NotificationDAO;
import com.blooddonation.model.BloodRequest;
import com.blooddonation.model.Donation;
import com.blooddonation.model.Donor;
import com.blooddonation.model.Hospital;
import com.blooddonation.model.Notification;
import com.blooddonation.util.BloodCompatibility;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@WebServlet("/BloodRequestServlet")
public class BloodRequestServlet extends HttpServlet {

    private BloodRequestDAO requestDAO = new BloodRequestDAO();
    private BloodStockDAO stockDAO = new BloodStockDAO();
    private DonationDAO donationDAO = new DonationDAO();
    private DonorDAO donorDAO = new DonorDAO();
    private HospitalDAO hospitalDAO = new HospitalDAO();
    private NotificationDAO notificationDAO = new NotificationDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String hospitalIdStr = request.getParameter("hospitalId");
        String city = request.getParameter("city");
        List<BloodRequest> list;

        if (hospitalIdStr != null) {
            list = requestDAO.getRequestsByHospitalId(Integer.parseInt(hospitalIdStr));
        } else if ("get".equals(action)) {
            String idStr = request.getParameter("id");
            if (idStr != null) {
                BloodRequest req = requestDAO.getRequestById(Integer.parseInt(idStr));
                if (req != null && req.getAssignedDonorId() > 0) {
                    req.setAssignedDonor(donorDAO.getDonorById(req.getAssignedDonorId()));
                }
                response.getWriter().write(gson.toJson(req));
                return;
            }
            list = requestDAO.getAllRequests();
        } else {
            list = requestDAO.getAllRequests();
        }

        if (city != null && !city.trim().isEmpty()) {
            String targetCity = city.trim().toLowerCase();
            list = list.stream()
                    .filter(r -> r.getCity() != null && r.getCity().toLowerCase().contains(targetCity))
                    .collect(java.util.stream.Collectors.toList());
        }

        for (BloodRequest r : list) {
            if (r.getAssignedDonorId() > 0) {
                r.setAssignedDonor(donorDAO.getDonorById(r.getAssignedDonorId()));
            }
        }

        response.getWriter().write(gson.toJson(list));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        Map<String, Object> respMap = new HashMap<>();

        if ("add".equalsIgnoreCase(action)) {
            String patientName = request.getParameter("patientName");
            String bloodType = request.getParameter("bloodType");
            String rhFactor = request.getParameter("rhFactor");
            String unitsStr = request.getParameter("unitsNeeded");
            String urgency = request.getParameter("urgency");
            String hospitalIdStr = request.getParameter("hospitalId");
            String hospitalName = request.getParameter("hospitalName");
            String city = request.getParameter("city");
            String contactPerson = request.getParameter("contactPerson");
            String phone = request.getParameter("phone");
            String notes = request.getParameter("notes");
            String requiredDateStr = request.getParameter("requiredDate");

            if (patientName == null || bloodType == null || rhFactor == null || unitsStr == null || hospitalIdStr == null) {
                respMap.put("success", false);
                respMap.put("message", "Please fill in all required request fields.");
                response.getWriter().write(gson.toJson(respMap));
                return;
            }

            int unitsNeeded = 1;
            int hospitalId = 0;
            try {
                unitsNeeded = Integer.parseInt(unitsStr);
                hospitalId = Integer.parseInt(hospitalIdStr);
            } catch (Exception ignored) {}

            Hospital hosp = hospitalDAO.getHospitalById(hospitalId);
            if (hosp != null && !"approved".equalsIgnoreCase(hosp.getApprovalStatus())) {
                respMap.put("success", false);
                respMap.put("message", "Only registered and approved hospitals can create blood requests.");
                response.getWriter().write(gson.toJson(respMap));
                return;
            }

            java.sql.Date requiredDate = null;
            try {
                if (requiredDateStr != null && !requiredDateStr.trim().isEmpty()) {
                    requiredDate = java.sql.Date.valueOf(requiredDateStr);
                }
            } catch (IllegalArgumentException ignored) {}

            BloodRequest req = new BloodRequest();
            req.setPatientName(patientName.trim());
            req.setBloodType(bloodType.trim());
            req.setRhFactor(rhFactor.trim());
            req.setUnitsNeeded(unitsNeeded);
            req.setUrgency(urgency != null ? urgency.trim() : "routine");
            req.setHospitalId(hospitalId);
            req.setHospitalName(hospitalName != null ? hospitalName.trim() : "Hospital");
            req.setCity(city != null ? city.trim() : "");
            req.setContactPerson(contactPerson != null ? contactPerson.trim() : "");
            req.setPhone(phone != null ? phone.trim() : "");
            req.setRequiredDate(requiredDate);

            // Directly find compatible matching donors and broadcast request instantly
            List<Donor> matches = findCompatibleDonors(req);
            int notifyCount = Math.max(5, req.getUnitsNeeded() * 3);
            List<Donor> selectedDonors = matches.stream().limit(notifyCount).collect(java.util.stream.Collectors.toList());
            String matchedIds = selectedDonors.stream().map(m -> String.valueOf(m.getId())).collect(java.util.stream.Collectors.joining(","));

            req.setStatus("matching");
            req.setMatchedDonorIds(matchedIds);
            req.setAcceptedUnits(0);
            req.setAcceptedDonorIds("");
            req.setReferenceId("REQ-" + System.currentTimeMillis());

            boolean ok = requestDAO.addRequest(req);

            if (ok) {
                // Notify matched donors DIRECTLY
                for (Donor donor : selectedDonors) {
                    if (donor.getUserId() != 0) {
                        Notification n = new Notification();
                        n.setUserId(donor.getUserId());
                        n.setTitle("New Blood Request");
                        n.setMessage("A request for " + req.getUnitsNeeded() + " unit(s) of " + req.getBloodType() + req.getRhFactor() + " blood at " + req.getHospitalName() + " (" + req.getCity() + ") is available. Please respond if you can donate.");
                        n.setType("request");
                        n.setRead(false);
                        notificationDAO.addNotification(n);
                    }
                }

                respMap.put("success", true);
                respMap.put("requestId", req.getId());
                respMap.put("matchedCount", selectedDonors.size());
                respMap.put("message", "Blood request submitted successfully and sent directly to " + selectedDonors.size() + " matching donor(s).");
            } else {
                respMap.put("success", false);
                respMap.put("message", "Failed to submit blood request.");
            }

        } else if ("fulfill".equalsIgnoreCase(action)) {
            String idStr = request.getParameter("id");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                BloodRequest req = requestDAO.getRequestById(id);

                if (req == null) {
                    respMap.put("success", false);
                    respMap.put("message", "Blood request not found.");
                    response.getWriter().write(gson.toJson(respMap));
                    return;
                }

                if (req.getStatus() == null || "cancelled".equalsIgnoreCase(req.getStatus()) || "completed".equalsIgnoreCase(req.getStatus())) {
                    respMap.put("success", false);
                    respMap.put("message", "This request cannot be fulfilled at this time.");
                    response.getWriter().write(gson.toJson(respMap));
                    return;
                }

                List<Donor> matches = findCompatibleDonors(req);
                int notifyCount = Math.max(5, req.getUnitsNeeded() * 3);
                List<Donor> selectedDonors = matches.stream().limit(notifyCount).collect(java.util.stream.Collectors.toList());
                String matchedIds = selectedDonors.stream().map(m -> String.valueOf(m.getId())).collect(java.util.stream.Collectors.joining(","));
                requestDAO.updateMatchedDonors(id, matchedIds, "matching");

                for (Donor donor : selectedDonors) {
                    if (donor.getUserId() != 0) {
                        Notification n = new Notification();
                        n.setUserId(donor.getUserId());
                        n.setTitle("New Blood Request");
                        n.setMessage("A request for " + req.getUnitsNeeded() + " unit(s) of " + req.getBloodType() + req.getRhFactor() + " blood at " + req.getHospitalName() + " (" + req.getCity() + ") is available. Please respond if you can donate.");
                        n.setType("request");
                        n.setRead(false);
                        notificationDAO.addNotification(n);
                    }
                }

                Hospital h = hospitalDAO.getHospitalById(req.getHospitalId());
                if (h != null && h.getUserId() != 0) {
                    Notification hospitalNotification = new Notification();
                    hospitalNotification.setUserId(h.getUserId());
                    hospitalNotification.setTitle("Request Reviewed & Fulfilled");
                    hospitalNotification.setMessage("Your blood request #" + req.getId() + " has been reviewed & fulfilled by Admin. Matched donors have been notified.");
                    hospitalNotification.setType("success");
                    hospitalNotification.setRead(false);
                    notificationDAO.addNotification(hospitalNotification);
                }

                respMap.put("success", true);
                respMap.put("matches", selectedDonors);
                respMap.put("message", "Blood request fulfilled by Admin. Matched donors have been notified.");
            }
        } else if ("emergencyAlert".equalsIgnoreCase(action)) {
            String donorIdStr = request.getParameter("donorId");
            String hospitalName = request.getParameter("hospitalName");
            String bloodGroup = request.getParameter("bloodGroup");

            if (donorIdStr != null) {
                int donorId = Integer.parseInt(donorIdStr);
                Donor d = donorDAO.getDonorById(donorId);
                if (d != null && d.getUserId() != 0) {
                    Notification n = new Notification();
                    n.setUserId(d.getUserId());
                    n.setTitle("URGENT: Emergency Blood Needed!");
                    n.setMessage((hospitalName != null ? hospitalName : "Emergency Hospital") + " requires urgent " + (bloodGroup != null ? bloodGroup : "") + " blood group donation! Please contact the hospital immediately if available.");
                    n.setType("alert");
                    n.setRead(false);
                    notificationDAO.addNotification(n);

                    respMap.put("success", true);
                    respMap.put("message", "Emergency alert dispatched to donor successfully.");
                } else {
                    respMap.put("success", false);
                    respMap.put("message", "Donor profile not found.");
                }
            }
        } else if ("cancel".equalsIgnoreCase(action)) {
            String idStr = request.getParameter("id");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                requestDAO.updateStatus(id, "cancelled");
                respMap.put("success", true);
                respMap.put("message", "Blood request cancelled.");
            }
        } else if ("respond".equalsIgnoreCase(action)) {
            String requestIdStr = request.getParameter("requestId");
            String donorIdStr = request.getParameter("donorId");
            String responseStr = request.getParameter("response");

            if (requestIdStr != null && donorIdStr != null && responseStr != null) {
                int requestId = Integer.parseInt(requestIdStr);
                int donorId = Integer.parseInt(donorIdStr);
                BloodRequest req = requestDAO.getRequestById(requestId);

                if (req == null) {
                    respMap.put("success", false);
                    respMap.put("message", "Blood request not found.");
                    response.getWriter().write(gson.toJson(respMap));
                    return;
                }

                if (req.getStatus() == null || "pending".equalsIgnoreCase(req.getStatus()) || "cancelled".equalsIgnoreCase(req.getStatus()) || "completed".equalsIgnoreCase(req.getStatus())) {
                    respMap.put("success", false);
                    respMap.put("message", "This request is not available for responses.");
                    response.getWriter().write(gson.toJson(respMap));
                    return;
                }

                Donor donor = donorDAO.getDonorById(donorId);
                Hospital h = hospitalDAO.getHospitalById(req.getHospitalId());

                if ("accept".equalsIgnoreCase(responseStr)) {
                    // FIRST DONOR WINS: Atomic assignment
                    boolean won = requestDAO.assignFirstDonor(requestId, donorId);
                    if (!won) {
                        respMap.put("success", false);
                        respMap.put("isLocked", true);
                        respMap.put("message", "This request has already been accepted by another donor.");
                        response.getWriter().write(gson.toJson(respMap));
                        return;
                    }

                    requestDAO.updateDonorResponse(requestId, donorId, "accept");

                    if (h != null && h.getUserId() != 0) {
                        Notification n = new Notification();
                        n.setUserId(h.getUserId());
                        n.setTitle("Donor Accepted Request");
                        n.setMessage((donor != null ? donor.getName() : "A donor") + " has ACCEPTED blood request #" + requestId + ". Contact details are now available.");
                        n.setType("success");
                        n.setRead(false);
                        notificationDAO.addNotification(n);
                    }
                    if (donor != null && donor.getUserId() != 0) {
                        Notification dNotif = new Notification();
                        dNotif.setUserId(donor.getUserId());
                        dNotif.setTitle("Request Accepted");
                        dNotif.setMessage("You have accepted blood request #" + requestId + " for " + req.getHospitalName() + ". Thank you for donating!");
                        dNotif.setType("success");
                        dNotif.setRead(false);
                        notificationDAO.addNotification(dNotif);
                    }

                    respMap.put("success", true);
                    respMap.put("isAssigned", true);
                    respMap.put("message", "Request accepted and assigned to you!");
                } else if ("decline".equalsIgnoreCase(responseStr)) {
                    requestDAO.updateDonorResponse(requestId, donorId, "decline");
                    if (h != null && h.getUserId() != 0) {
                        Notification n = new Notification();
                        n.setUserId(h.getUserId());
                        n.setTitle("Donor Declined Request");
                        n.setMessage((donor != null ? donor.getName() : "A donor") + " has declined blood request #" + requestId + ".");
                        n.setType("alert");
                        n.setRead(false);
                        notificationDAO.addNotification(n);
                    }
                    respMap.put("success", true);
                    respMap.put("message", "Response recorded successfully (DECLINED).");
                }
            } else {
                respMap.put("success", false);
                respMap.put("message", "Invalid arguments for donor response.");
            }
        } else if ("complete".equalsIgnoreCase(action)) {
            String idStr = request.getParameter("id");
            String hospitalNote = request.getParameter("hospitalNote");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                BloodRequest req = requestDAO.getRequestById(id);
                if (req == null) {
                    respMap.put("success", false);
                    respMap.put("message", "Blood request not found.");
                    response.getWriter().write(gson.toJson(respMap));
                    return;
                }
                if ("completed".equalsIgnoreCase(req.getStatus()) || "cancelled".equalsIgnoreCase(req.getStatus()) || (req.getAssignedDonorId() <= 0 && req.getAcceptedUnits() <= 0)) {
                    respMap.put("success", false);
                    respMap.put("message", "This request cannot be completed because no donor has accepted it yet.");
                    response.getWriter().write(gson.toJson(respMap));
                    return;
                }

                boolean updated = requestDAO.updateHospitalNoteAndStatus(id, hospitalNote != null ? hospitalNote.trim() : "", "completed");
                if (updated) {
                    int assignedDonorId = req.getAssignedDonorId();
                    if (assignedDonorId <= 0) {
                        Set<Integer> acceptedIds = parseIdSet(req.getAcceptedDonorIds());
                        if (!acceptedIds.isEmpty()) assignedDonorId = acceptedIds.iterator().next();
                    }

                    if (assignedDonorId > 0) {
                        Donor donor = donorDAO.getDonorById(assignedDonorId);
                        if (donor != null) {
                            java.sql.Date todayDate = java.sql.Date.valueOf(java.time.LocalDate.now());
                            donorDAO.updateDonationHistory(donor.getId(), todayDate);

                            Donation donation = new Donation();
                            donation.setRequestId(req.getId());
                            donation.setDonorId(donor.getId());
                            donation.setDonorName(donor.getName());
                            donation.setBloodType(donor.getBloodType());
                            donation.setRhFactor(donor.getRhFactor());
                            donation.setVolumeMl(450);
                            donation.setRequestDate(req.getCreatedAt());
                            donation.setDonatedAt(new Timestamp(System.currentTimeMillis()));
                            donation.setHospitalName(req.getHospitalName());
                            donation.setHospitalId(req.getHospitalId());
                            donation.setStatus("completed");
                            donation.setHospitalNote(hospitalNote != null ? hospitalNote.trim() : "");
                            donation.setReferenceId("DON-" + System.currentTimeMillis() + "-" + donor.getId());
                            donation.setNotes(hospitalNote != null ? hospitalNote.trim() : "Donation completed");
                            donationDAO.addDonation(donation);

                            if (donor.getUserId() != 0) {
                                Notification dNotif = new Notification();
                                dNotif.setUserId(donor.getUserId());
                                dNotif.setTitle("Donation Completed");
                                dNotif.setMessage("Your donation for request #" + req.getId() + " was marked completed by the hospital. Note from " + req.getHospitalName() + ": \"" + hospitalNote + "\"");
                                dNotif.setType("success");
                                dNotif.setRead(false);
                                notificationDAO.addNotification(dNotif);
                            }
                        }
                    }

                    Hospital h = hospitalDAO.getHospitalById(req.getHospitalId());
                    if (h != null && h.getUserId() != 0) {
                        Notification hNotif = new Notification();
                        hNotif.setUserId(h.getUserId());
                        hNotif.setTitle("Donation Completed");
                        hNotif.setMessage("Request #" + req.getId() + " marked as completed with note: \"" + hospitalNote + "\".");
                        hNotif.setType("success");
                        hNotif.setRead(false);
                        notificationDAO.addNotification(hNotif);
                    }

                    respMap.put("success", true);
                    respMap.put("message", "Donation completed and history updated.");
                } else {
                    respMap.put("success", false);
                    respMap.put("message", "Failed to complete donation.");
                }
            }
        }

        response.getWriter().write(gson.toJson(respMap));
    }

    private Map<Integer, String> parseDonorResponses(String responses) {
        Map<Integer, String> map = new java.util.HashMap<>();
        if (responses == null || responses.trim().isEmpty()) {
            return map;
        }
        for (String part : responses.split(";")) {
            String[] kv = part.trim().split(":");
            if (kv.length == 2) {
                try {
                    int donorId = Integer.parseInt(kv[0].replace("donor_", "").trim());
                    map.put(donorId, kv[1].trim().toLowerCase());
                } catch (NumberFormatException ignored) {}
            }
        }
        return map;
    }

    private String buildResponseString(Map<Integer, String> responses) {
        return responses.entrySet().stream()
                .map(entry -> "donor_" + entry.getKey() + ":" + entry.getValue())
                .collect(java.util.stream.Collectors.joining(";"));
    }

    private Set<Integer> parseIdSet(String ids) {
        Set<Integer> set = new java.util.HashSet<>();
        if (ids == null || ids.trim().isEmpty()) return set;
        for (String id : ids.split(",")) {
            try {
                set.add(Integer.parseInt(id.trim()));
            } catch (NumberFormatException ignored) {}
        }
        return set;
    }

    private List<Donor> findCompatibleDonors(BloodRequest req) {
        int cooldownDays = 56;
        try {
            cooldownDays = Integer.parseInt(new com.blooddonation.dao.SettingsDAO().getValue("cooldown_days", "56"));
        } catch (Exception ignored) {}

        String requestedType = req.getBloodType() + req.getRhFactor();
        boolean isCritical = "critical".equalsIgnoreCase(req.getUrgency());

        List<Donor> candidates = donorDAO.getCompatibleDonors(requestedType, req.getCity(), cooldownDays);
        List<Donor> matches = new java.util.ArrayList<>();

        for (Donor d : candidates) {
            int score = calculateMatchScore(d, requestedType, req);
            d.setName(d.getName());
            matches.add(d);
        }

        matches.sort((a, b) -> Integer.compare(calculateMatchScore(b, requestedType, req), calculateMatchScore(a, requestedType, req)));
        return matches;
    }

    private int calculateMatchScore(Donor d, String requestedType, BloodRequest req) {
        boolean isCritical = "critical".equalsIgnoreCase(req.getUrgency());
        String donorType = d.getBloodType() + d.getRhFactor();
        int score = 0;
        if (donorType.equalsIgnoreCase(requestedType)) {
            score += isCritical ? 35 : 50;
        } else if ("O-".equalsIgnoreCase(donorType)) {
            score += isCritical ? 28 : 30;
        } else {
            score += isCritical ? 20 : 25;
        }
        if (d.getCity() != null && req.getCity() != null && d.getCity().equalsIgnoreCase(req.getCity())) {
            score += isCritical ? 25 : 30;
        } else {
            score += 10;
        }
        if (d.isAvailable()) score += 10;
        if (d.getLastDonationDate() != null) {
            long daysSince = java.time.temporal.ChronoUnit.DAYS.between(d.getLastDonationDate().toLocalDate(), LocalDate.now());
            score += Math.min(10, (int)(daysSince / 30));
        } else {
            score += 10;
        }
        return Math.min(score, 100);
    }
}
