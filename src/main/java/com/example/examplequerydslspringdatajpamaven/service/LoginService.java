package com.example.examplequerydslspringdatajpamaven.service;

import com.example.food_drugs.dto.ApiResponse;
import com.example.food_drugs.dto.responses.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface LoginService {

//	ApiResponse<LoginResponse> login(String authorization);
	ResponseEntity<?> login(String authorization);
	ResponseEntity<?> logout(String token);
		
}
