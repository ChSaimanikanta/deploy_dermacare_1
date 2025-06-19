package com.dermaCare.customerService.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.dermaCare.customerService.dto.BookingResponse;
import com.dermaCare.customerService.util.ResBody;
import com.dermaCare.customerService.util.Response;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;


@FeignClient(value = "notificationservice")
@CircuitBreaker(name = "circuitBreaker", fallbackMethod = "notificationAdminServiceFallBack")
public interface NotificationFeign {

	@PostMapping("/api/notificationservice/createnotification")
	public ResponseEntity<ResBody<String>> notification(@RequestBody BookingResponse bookingRequset);
	
	
	//FALLBACK METHODS
//	
	default ResponseEntity<?> notificationAdminServiceFallBack(Exception e){		 
	return ResponseEntity.status(503).body(new Response("NOTIFICATION SERVICE NOT AVAILABLE",503,false,null));}
}
