package com.example.food_drugs.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserRoleServiceSFDA {

	public ResponseEntity<?>activeRole(String TOKEN,Long roleId,Long userId);
	public ResponseEntity<?>getAllRolesCreatedByUserSFDA(String TOKEN,Long userId,int offset,String search,int active,String exportData);

}
