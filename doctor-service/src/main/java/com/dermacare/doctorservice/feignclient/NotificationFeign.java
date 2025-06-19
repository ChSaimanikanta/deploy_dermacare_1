package com.dermacare.doctorservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.dermacare.doctorservice.dto.NotificationDTO;

@FeignClient(value = "notificationservice")
public interface NotificationFeign {
	
	@GetMapping("/api/notificationservice/getNotificationByBookingId/{id}")
	public NotificationDTO getNotificationByBookingId(@PathVariable String id);
	
	@PutMapping("/api/notificationservice/updateNotification")
	public NotificationDTO updateNotification(@RequestBody NotificationDTO notificationDTO );

}
