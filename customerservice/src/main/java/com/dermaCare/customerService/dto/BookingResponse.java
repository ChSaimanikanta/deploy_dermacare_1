package com.dermaCare.customerService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingResponse {
	
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
	private String channelId;
	private String reasonForCancel;
	private String notes;
	private ReportsDtoList reports;
	private String BookedAt;
	private String status;
	private double totalFee;

}
