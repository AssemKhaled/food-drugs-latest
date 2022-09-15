package com.example.food_drugs.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface DriverServiceSFDA {

	public ResponseEntity<?> activeDriver(String Token,Long driverId, Long userId);
	public ResponseEntity<?> getAllDriversSFDA(String TOKEN,Long id,int offset,String search,int active,String exportData);

}
