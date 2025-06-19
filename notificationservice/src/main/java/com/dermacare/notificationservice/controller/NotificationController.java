package com.dermacare.notificationservice.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dermacare.notificationservice.dto.BookingResponse;
import com.dermacare.notificationservice.dto.NotificationDTO;
import com.dermacare.notificationservice.dto.NotificationResponse;
import com.dermacare.notificationservice.dto.NotificationToCustomer;
import com.dermacare.notificationservice.service.NotificationService;
import com.dermacare.notificationservice.util.ResBody;

@RestController
@RequestMapping("/notificationservice")
public class NotificationController {
	
	@Autowired
	private NotificationService notificationService;
	
	@PostMapping("/createnotification")
	public ResponseEntity<ResBody<String>> notification(@RequestBody BookingResponse bookingRequset){
		NotificationDTO notification = new NotificationDTO();
	    notification.setMessage("You have a new service appointment: " + bookingRequset.getSubServiceName());
	    notification.setData(bookingRequset);
	    notification.setActions(new String[]{"Accept", "Reject"});
	    ResBody<String> res = notificationService.notification(notification);
	    if(res != null) {
	    	return ResponseEntity.status(res.getStatus()).body(res);}
	    return null;
	}
	
		
	@GetMapping("/notificationtodoctorandclinic/{hospitalId}/{doctorId}")
	public ResponseEntity<ResBody<List<NotificationDTO>>> notificationtodoctorandclinic(@PathVariable String hospitalId,
			@PathVariable String doctorId){
		ResBody<List<NotificationDTO>> res = notificationService.notificationtodoctorandclinic(hospitalId, doctorId);
		 if(res != null) {
		    	return ResponseEntity.status(res.getStatus()).body(res);}
		    return null;	
	}
	
	
	@PostMapping("/response")
	public ResponseEntity<?> response(@RequestBody NotificationResponse notificationResponse){
		ResBody<NotificationDTO> res = notificationService.notificationResponse(notificationResponse);
		 if(res != null) {
		    	return ResponseEntity.status(res.getStatus()).body(res);}
		    return null;
		 
	}	
	
	@GetMapping("/notificationtoadmin")
	public ResponseEntity<?> notificationToAdmin(){
		ResBody<List<NotificationDTO>> res = notificationService.sendNotificationToAdmin();
		 if(res != null) {
		    	return ResponseEntity.status(res.getStatus()).body(res);}
		    return null;
		 
	}	
	
	
	@GetMapping("/getNotificationByBookingId/{id}")
	public NotificationDTO getNotificationByBookingId(@PathVariable String id){
	NotificationDTO res = notificationService.getNotificationByBookingId(id);
		 if(res != null) {
		    return res;
		 }
		 return null;
	}	
	
	
	@PutMapping("/updateNotification")
	public NotificationDTO updateNotification(@RequestBody NotificationDTO notificationDTO ){
	NotificationDTO res = notificationService.updateNotification(notificationDTO);
		 if(res != null) {
		    return res;
		 }
		 return null;
	}	
	
	
	@GetMapping("/notificationToCustomer/{customerName}/{customerMobileNumber}")
	public ResponseEntity<ResBody<List<NotificationToCustomer>>> notificationToCustomer(@PathVariable String customerName,
			@PathVariable String customerMobileNumber){
		ResBody<List<NotificationToCustomer>> res = notificationService.notificationToCustomer( customerName, customerMobileNumber);
		 if(res != null) {
		    	return ResponseEntity.status(res.getStatus()).body(res);}
		    return null;	
	}
	
}
