package com.dermacare.notificationservice.dto;

import lombok.Data;

@Data
public class DoctorNotesDTO {
    private String bookingId;
    private String doctorId;
    private String patientPhoneNumber;
    private String notes;
}
