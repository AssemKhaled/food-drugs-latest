package com.example.food_drugs.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface GeofenceServiceSFDA {

	public ResponseEntity<?> activeGeofence(String TOKEN,Long geofenceId,Long userId);
	public ResponseEntity<?> getAllGeofencesSFDA(String TOKEN,Long id,int offset,String search,int active,String exportData);

}
