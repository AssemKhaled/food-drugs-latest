package com.example.food_drugs.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface PointsServiceSFDA {

	public ResponseEntity<?> activePoints(String TOKEN,Long PointId,Long userId);
	public ResponseEntity<?> getPointsListSFDA(String TOKEN,Long id,int offset,String search,int active,String exportData);

}
