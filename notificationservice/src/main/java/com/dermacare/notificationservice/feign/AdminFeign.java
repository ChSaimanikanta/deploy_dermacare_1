package com.dermacare.notificationservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.dermacare.notificationservice.util.Response;

@FeignClient(name = "adminservice")
public interface AdminFeign {
	 @GetMapping("/admin/getClinicById/{clinicId}")
	    public Response getClinicById(@PathVariable String clinicId) ;

}
