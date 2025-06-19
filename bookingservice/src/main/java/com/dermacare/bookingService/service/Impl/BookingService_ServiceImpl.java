package com.dermacare.bookingService.service.Impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.dermacare.bookingService.dto.BookingRequset;
import com.dermacare.bookingService.dto.BookingResponse;
import com.dermacare.bookingService.dto.DoctorNotesDTO;
import com.dermacare.bookingService.dto.ReportsDTO;
import com.dermacare.bookingService.dto.ReportsDtoList;
import com.dermacare.bookingService.entity.Booking;
import com.dermacare.bookingService.entity.DoctorNotes;
import com.dermacare.bookingService.entity.Reports;
import com.dermacare.bookingService.entity.ReportsList;
import com.dermacare.bookingService.feign.ClinicAdminFeign;
import com.dermacare.bookingService.feign.DoctorFeign;
import com.dermacare.bookingService.repository.BookingServiceRepository;
import com.dermacare.bookingService.service.BookingService_Service;
import com.dermacare.bookingService.util.ExtractFeignMessage;
import com.dermacare.bookingService.util.Response;
import com.dermacare.bookingService.util.ResponseStructure;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;


@Service
public class BookingService_ServiceImpl implements BookingService_Service {


	@Autowired
	private BookingServiceRepository repository;
		
	@Autowired
	private ClinicAdminFeign clinicAdminFeign;

	@Override
	public BookingResponse addService(BookingRequset request) {
		Booking entity = toEntity(request);
		try {
			entity.setStatus("Pending");			
		}catch (Exception e) {
			throw new RuntimeException("Unable to book service");
		}
		Booking response= repository.save(entity);
		return toResponse(response);
	}
	private static Booking toEntity(BookingRequset request) {
		Booking entity = new ObjectMapper().convertValue(request, Booking.class);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a", Locale.ENGLISH);
		String nowFormatted = LocalDateTime.now().format(formatter);
		entity.setBookedAt(nowFormatted);
		return entity;
	}

	private static BookingResponse toResponse(Booking entity) {
		BookingResponse response = new ObjectMapper().convertValue(entity,BookingResponse.class);
		if(entity.getConsultationType().equalsIgnoreCase("video consultation") || entity.getConsultationType().equalsIgnoreCase("online consultation") ) {
			response.setChannelId(randomNumber());
		}else {
			response.setChannelId(null) ;
		}		
		return response;
	}
	
	private static String randomNumber() {
        Random random = new Random();    
        int sixDigitNumber = 100000 + random.nextInt(900000); // Generates number from 100000 to 999999
        return String.valueOf(sixDigitNumber);
    }

	private List<BookingResponse> toResponses(List<Booking> bookings) {
		return bookings.stream().map(BookingService_ServiceImpl::toResponse).toList();
	}

	public BookingResponse getBookedService(String id) {
		Booking entity = repository.findByBookingId(new ObjectId(id))
				.orElseThrow(() -> new RuntimeException("Invalid Booking Id Please provide Valid Id"));
		return toResponse(entity);
	}

	@Override
	public BookingResponse deleteService(String id) {
		Booking entity = repository.findByBookingId(new ObjectId(id))
				.orElseThrow(() -> new RuntimeException("Invalid Booking Id Please provide Valid Id"));
		repository.deleteById(id);
		return toResponse(entity);
	}

	@Override
	public List<BookingResponse> getBookedServices(String mobileNumber) {
		List<Booking> bookings = repository.findByMobileNumber(mobileNumber);
		if (bookings == null) {
			return null;
		}
		return toResponses(bookings);
	}
	
	@Override
	public List<BookingResponse> getAllBookedServices() {
		List<Booking> bookings = repository.findAll();
		if (bookings == null) {
			return null;
		}
		return toResponses(bookings);
	}

	@Override
	public List<BookingResponse> bookingByDoctorId(String doctorId) {
		List<Booking> bookings = repository.findByDoctorId(doctorId);
		if (bookings == null) {
			return null;
		}
		return toResponses(bookings);
	}

	@Override
	public List<BookingResponse> bookingByServiceId(String serviceId) {
		List<Booking> bookings = repository.findBySubServiceId(serviceId);
		if (bookings.isEmpty()) {
			return null;
		}
		return toResponses(bookings);
	}

	@Override
	public List<BookingResponse> bookingByClinicId(String clinicId) {
		List<Booking> bookings = repository.findByClinicId(clinicId);
		if(bookings==null || bookings.isEmpty()) {
		 return null;
		}
		return toResponses(bookings);
	}
	
	
	public ResponseEntity<?> updateAppointment(BookingResponse bookingResponse){
		try {
		Booking entity = repository.findById(bookingResponse.getBookingId())
	.orElseThrow(() -> new RuntimeException("Invalid Booking Id Please provide Valid Id"));
		entity.setAge(bookingResponse.getAge());
		entity.setBookedAt( bookingResponse.getBookedAt());
		entity.setBookingFor( bookingResponse.getBookingFor());
		entity.setClinicId( bookingResponse.getClinicId());
		entity.setConsultationFee( bookingResponse.getConsultationFee());
		entity.setConsultationType( bookingResponse.getConsultationType());
		entity.setDoctorId( bookingResponse.getDoctorId());
		entity.setGender( bookingResponse.getGender());
		entity.setMobileNumber( bookingResponse.getMobileNumber());
		entity.setName( bookingResponse.getName());
		entity.setProblem( bookingResponse.getProblem());
		entity.setServiceDate(bookingResponse.getServiceDate());
		entity.setServicetime( bookingResponse.getServicetime());
		entity.setStatus( bookingResponse.getStatus());
		entity.setNotes(bookingResponse.getNotes());
		entity.setReports(new ObjectMapper().convertValue(bookingResponse.getReports(),ReportsList.class));
		entity.setSubServiceId( bookingResponse.getSubServiceId());
		entity.setSubServiceName( bookingResponse.getSubServiceName());
		entity.setReasonForCancel( bookingResponse.getReasonForCancel());
		entity.setTotalFee(bookingResponse.getTotalFee());
		Booking e = repository.save(entity);
			
		if(e != null) {	
		return new ResponseEntity<>(ResponseStructure.buildResponse(e,
				"Booking updated sucessfully",HttpStatus.OK, HttpStatus.OK.value()),
				HttpStatus.OK);			
		}else {
			return new ResponseEntity<>(ResponseStructure.buildResponse(null,
					"Booking Not Updated", HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value()),
					HttpStatus.NOT_FOUND);
		}}catch(Exception e) {
			return new ResponseEntity<>(ResponseStructure.buildResponse(null,
					e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}}
}
