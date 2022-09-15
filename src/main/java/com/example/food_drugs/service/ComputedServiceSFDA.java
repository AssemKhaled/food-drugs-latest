package com.example.food_drugs.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ComputedServiceSFDA {

	public ResponseEntity<?> activeComputedSFDA(String TOKEN,Long attributeId,Long userId);
	public ResponseEntity<?> getAllComputedSFDA(String TOKEN,Long id,int offset,String search,int active,String exportData);

}
