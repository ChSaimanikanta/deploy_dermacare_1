package com.dermacare.bookingService.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "bookings")
@Setter
@Getter
@NoArgsConstructor
public class Booking  {
	@Id
	private String bookingId;
	private String bookingFor;
	private String name;
	private String age;
	private String gender;
	private String mobileNumber;
	private String problem;
	private String subServiceName;
	private String subServiceId;
	private String doctorId;
	private String clinicId;
	private String serviceDate;
	private String servicetime;
	private String consultationType;
	private double consultationFee;
	private String reasonForCancel;
	private String notes;
	private ReportsList reports;
	private String BookedAt;
	private String status;
	private double totalFee;

}
