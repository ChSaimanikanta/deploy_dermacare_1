package com.dermacare.notificationservice.serviceimpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.dermacare.notificationservice.dto.BookingResponse;
import com.dermacare.notificationservice.dto.ClinicDTO;
import com.dermacare.notificationservice.dto.DoctorsDTO;
import com.dermacare.notificationservice.dto.NotificationDTO;
import com.dermacare.notificationservice.dto.NotificationResponse;
import com.dermacare.notificationservice.dto.NotificationToCustomer;
import com.dermacare.notificationservice.entity.Booking;
import com.dermacare.notificationservice.entity.NotificationEntity;
import com.dermacare.notificationservice.feign.AdminFeign;
import com.dermacare.notificationservice.feign.BookServiceFeign;
import com.dermacare.notificationservice.feign.CllinicFeign;
import com.dermacare.notificationservice.repository.NotificationRepository;
import com.dermacare.notificationservice.service.NotificationService;
import com.dermacare.notificationservice.util.ExtractFeignMessage;
import com.dermacare.notificationservice.util.ResBody;
import com.dermacare.notificationservice.util.Response;
import com.dermacare.notificationservice.util.ResponseStructure;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;


@Service
public class NotificationServiceImpl implements NotificationService {
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Autowired
	private  BookServiceFeign  bookServiceFeign;
	
	@Autowired
	private AdminFeign adminFeign;
	
	@Autowired
	private CllinicFeign cllinicFeign;
	
	
	Set<String> notificationtoadmin = new LinkedHashSet<>();
	
	public ResBody<String> notification(NotificationDTO notificationDTO) {
		try {
			NotificationEntity notificationEntity = new NotificationEntity();
			notificationEntity.setMessage(notificationDTO.getMessage());
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			String currentDate = LocalDate.now().format(dateFormatter);	
			notificationEntity.setDate(currentDate);
			LocalTime currentTime = LocalTime.now();
		    String time = String.valueOf(currentTime);
		    notificationEntity.setTime(time);
			notificationEntity.setData(new ObjectMapper().convertValue(notificationDTO.getData(),Booking.class));
			notificationEntity.setActions(notificationDTO.getActions());
			NotificationEntity entity =	notificationRepository.save(notificationEntity);
			if(entity != null) {
				return new ResBody<String>("Notification Saved Sucessfully",200,"Notificcation Successfully Created By Booking");
			}
			return new ResBody<String>("Notification Not Saved In The DataBase",404,null);
			
		}catch(Exception e) {
			return new ResBody<String>(e.getMessage(),500,null);
		}
	}
	
	
	public ResBody<List<NotificationDTO>> notificationtodoctorandclinic( String hospitalId,
			 String doctorId){
		ResBody<List<NotificationDTO>> res = new ResBody<List<NotificationDTO>>();
		List<NotificationDTO> eligibleNotifications = new ArrayList<>();
		try {
		List<NotificationEntity> entity = notificationRepository.findByDataClinicIdAndDataDoctorId(hospitalId, doctorId);
		List<NotificationDTO> dto = new ObjectMapper().convertValue(entity, new TypeReference<List<NotificationDTO>>() {});
		if(dto != null) {
		for(NotificationDTO n : dto) {					
			if(n.getData().getStatus().equalsIgnoreCase("Pending")) {
				eligibleNotifications.add(n);}}}
		if(eligibleNotifications!=null || !eligibleNotifications.isEmpty() ) {
		res = new ResBody<List<NotificationDTO>>("Notification sent Successfully",200,eligibleNotifications);
		}else {
			res = new ResBody<List<NotificationDTO>>("NotificationInfo Not Found",404,null);
			}}catch(Exception e) {
		res = new ResBody<List<NotificationDTO>>(e.getMessage(),500,null);
	}
		return res;
		}
				

	
	public ResBody<List<NotificationDTO>> sendNotificationToAdmin() {
		ResBody<List<NotificationDTO>> r = new ResBody<List<NotificationDTO>>();
		List<NotificationDTO> list = new ArrayList<>();
		try {
			List<NotificationEntity> entity = notificationRepository.findAll();
			List<NotificationDTO> dto = new ObjectMapper().convertValue(entity, new TypeReference<List<NotificationDTO>>() {});
			if(dto != null) {
			for(NotificationDTO n : dto) {												
				if(n.getData().getStatus().equalsIgnoreCase("Pending") && timeDifference(n.getTime())) {					
					list.add(n);}}}
		    if( list != null && ! list.isEmpty()) {
		    	r = new ResBody<List<NotificationDTO>>("Notifications Are sent to the admin",200,list);
		    }else {
		    r = new ResBody<List<NotificationDTO>>("Notifications Are Not Found",404,null); }  
		}catch(Exception e) {
			r = new ResBody<List<NotificationDTO>>(e.getMessage(),500,null);
		}
		return r;	
	}
	
	
	
	 private boolean timeDifference(String time1) {			
		   try {
		       LocalTime currentTime = LocalTime.now();
		       String time2 = String.valueOf(currentTime);

		       // to parse time in the format HH:MM:SS
		       SimpleDateFormat simpleDateFormat
		           = new SimpleDateFormat("HH:mm");

		       // Parsing the Time Period
		       Date date1 = simpleDateFormat.parse(time1);
		       Date date2 = simpleDateFormat.parse(time2);

		       // Calculating the difference in milliseconds
		       long differenceInMilliSeconds
		           = Math.abs(date2.getTime() - date1.getTime());     
		       
		       // Calculating the difference in Minutes
		       long differenceInMinutes
		           = (differenceInMilliSeconds / (60 * 1000)) % 60;
		      
		       if(differenceInMinutes!=0.0) {
		    	   if(differenceInMinutes >=1) {
		    		   return true;
		    	   }else {
		    		   return false;
		    	   }}else{return false;}
		   }catch(ParseException e) {
			   return false;}
		   }
	
	

	public ResBody<NotificationDTO> notificationResponse(NotificationResponse notificationResponse){
		try {
			ResponseEntity<ResponseStructure<BookingResponse>> res =  bookServiceFeign.getBookedService(notificationResponse.getAppointmentId());
			BookingResponse b = res.getBody().getData();
			if(b.getDoctorId().equalsIgnoreCase(notificationResponse.getDoctorId())&&b.getClinicId().
			equalsIgnoreCase(notificationResponse.getHospitalId())&&b.getBookingId().equalsIgnoreCase(notificationResponse.getAppointmentId())
			&&b.getSubServiceId().equalsIgnoreCase(notificationResponse.getSubServiceId())) {	
				switch(notificationResponse.getStatus()) {
				case "Accepted": b.setStatus("Confirmed");
				Optional<NotificationEntity> notificationEntity = notificationRepository.findById(notificationResponse.getNotificationId());
				if(notificationEntity.get()!=null) {
				NotificationEntity n = notificationEntity.get();
				n.getData().setStatus("Confirmed");
				notificationRepository.save(n);}
				else {
					return new ResBody<NotificationDTO>("Notification Not Found With Given Id",404,null);
				}
				break;
				
				case "Rejected": b.setStatus("Rejected");
				 b.setReasonForCancel(notificationResponse.getReasonForCancel());
				 Optional<NotificationEntity> obj = notificationRepository.findById(notificationResponse.getNotificationId());
				 if(obj.get()!=null) {
					NotificationEntity c = obj.get();
					c.getData().setStatus("Rejected");
					notificationRepository.save(c);}
				 else {
						return new ResBody<NotificationDTO>("Notification Not Found With Given Id",404,null);
					}
				break;
				
				default:b.setStatus("Pending");
				}	
				removeCompletedNotifications();
		    	ResponseEntity<?> book = bookServiceFeign.updateAppointment(b);
		    	if(book != null) {
		    		return new ResBody<NotificationDTO>("Appointment Status updated",200,null);
		    	}else {
		    	return new ResBody<NotificationDTO>("Appointment Status Not updated",404,null);}
		    	}else {
		    	return new ResBody<NotificationDTO>("Appointment Not updated",404,null);
		    }						
		}catch(FeignException e) {
			return new ResBody<NotificationDTO>(ExtractFeignMessage.clearMessage(e),500,null);
		}}
	
	
	
	private void removeCompletedNotifications() {
    	List<NotificationEntity> entity = notificationRepository.findAll();
    	if(entity!=null && !entity.isEmpty()) {
    		for(NotificationEntity e : entity) {
    			if(e.getData().getStatus().equals("Completed")) {
    			notificationRepository.delete(e);	    			
    		}}}}
	
	
	@Override
	public NotificationDTO getNotificationByBookingId(String bookingId) {
		NotificationEntity notification = notificationRepository.findByDataBookingId(bookingId);
		 NotificationDTO dto = new ObjectMapper().convertValue(notification, NotificationDTO.class );
		 return dto;
	}
	

	@Override
	public NotificationDTO updateNotification( NotificationDTO  notificationDTO) {
		 NotificationEntity entity = new ObjectMapper().convertValue(notificationDTO, NotificationEntity.class );
		NotificationEntity notification = notificationRepository.save(entity);
		 NotificationDTO dto = new ObjectMapper().convertValue(notification, NotificationDTO.class );
		 return dto;
	}
	
	
	@Override
	public ResBody<List<NotificationToCustomer>> notificationToCustomer( String customerName,
			 String customerMobileNumber){
		ResBody<List<NotificationToCustomer>> res = new ResBody<List<NotificationToCustomer>>();
		List<NotificationToCustomer> eligibleNotifications = new ArrayList<>();
		try {
		List<NotificationEntity> entity = notificationRepository.findByDataNameAndDataMobileNumber(customerName,customerMobileNumber );
		List<NotificationDTO> dto = new ObjectMapper().convertValue(entity, new TypeReference<List<NotificationDTO>>() {});
		if(dto != null) {
		for(NotificationDTO n : dto) {					
			if(n.getData().getStatus().equalsIgnoreCase("Accepted")) {
				NotificationToCustomer notification = new NotificationToCustomer();
				notification.setMessage("Appointment Accepted For "+n.getData().getSubServiceName());
				Response response = adminFeign.getClinicById(n.getData().getClinicId());
				Object object =  response.getData();
				ClinicDTO clinicdto = new ObjectMapper().convertValue(object, ClinicDTO.class);
				Response respnse =  cllinicFeign.getDoctorById(n.getData().getDoctorId()).getBody();
				Object obj = respnse.getData();
				DoctorsDTO dctrDto = new ObjectMapper().convertValue(obj,DoctorsDTO.class );
				notification.setHospitalName(clinicdto.getName());
				notification.setDoctorName(dctrDto.getDoctorName());
				notification.setServiceName(n.getData().getSubServiceName());
				notification.setServiceDate(n.getData().getServiceDate());
				notification.setServiceTime(n.getData().getServicetime());
				notification.setServiceFee(n.getData().getTotalFee());
				notification.setConsultationType(n.getData().getConsultationType());
				notification.setConsultationFee(n.getData().getConsultationFee());
				eligibleNotifications.add(notification);}}}
		if(eligibleNotifications!=null || !eligibleNotifications.isEmpty() ) {
		res = new ResBody<List<NotificationToCustomer>>("Notification sent Successfully",200,eligibleNotifications);
		}else {
			res = new ResBody<List<NotificationToCustomer>>("NotificationInfo Not Found",404,null);
			}}catch(FeignException e) {
		res = new ResBody<List<NotificationToCustomer>>(ExtractFeignMessage.clearMessage(e),500,null);
	}
		return res;
		}	
}

