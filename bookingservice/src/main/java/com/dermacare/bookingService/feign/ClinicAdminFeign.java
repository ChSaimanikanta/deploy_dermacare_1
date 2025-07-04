package com.dermacare.bookingService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import com.dermacare.bookingService.util.Response;


@FeignClient(name = "clinicadmin")
public interface ClinicAdminFeign {
	
	 @GetMapping("/clinic-admin/getallreports")
     public ResponseEntity<Response> getAllReports();     

}
