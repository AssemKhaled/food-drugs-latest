package com.example.food_drugs.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface GroupServiceSFDA {

	public ResponseEntity<?> activeGroup(String TOKEN,Long groupId,Long userId);
	public ResponseEntity<?> getAllGroupsSFDA(String TOKEN,Long id,int offset,String search,int active,String exportData);

}
