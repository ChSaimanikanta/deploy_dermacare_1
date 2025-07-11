package com.dermacare.notificationservice.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClinicDTO {

	    private String hospitalId;
	    
	 
	    private String name;

	    private String address;

	  
	    private String city;

	    
	    private String contactNumber;

	  
	    private String hospitalRegistrations;

	  
	    private String openingTime;

	 
	    private String closingTime;

	    private String hospitalLogo;

	   
	    private String emailAddress;

	  
	    private String website;

	   
	    private String licenseNumber;

	  
	    private String issuingAuthority; 
	    
	 
	    private List<String> contractorDocuments;
	    
       
	    private List<String> hospitalDocuments;
        
        private boolean recommended;

	    // Getters and setters
	}

	   
	    
