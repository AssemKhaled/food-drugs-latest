package com.example.examplequerydslspringdatajpamaven.rest;

import java.util.List;
import java.util.Map;

import com.example.food_drugs.dto.ApiResponse;
import com.example.food_drugs.dto.ApiResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.tokens.TokenSecurity;

/**
 * Service check authorized
 * @author fuinco
 *
 */
public class RestServiceController {
 
	GetObjectResponse getObjectResponse;
	
	@Autowired
	private TokenSecurity tokenSecurity;
	ApiResponseBuilder<Object> builder = new ApiResponseBuilder<>();

	public RestServiceController(){
		
	}
	
	public ResponseEntity<?> checkActive(String token) {
		// TODO Auto-generated method stub
		if(token == null || token == "") {
			return this.ActiveReponse(false);
		}
		// Boolean updated = TokenSecurity.getInstance().checkToken(token);
		 Boolean updated = tokenSecurity.checkToken(token);

		 return this.ActiveReponse(updated);
	}
	public ApiResponse checkActiveByApi(String token) {
		// TODO Auto-generated method stub
		if(token == null || token == "") {
			return this.ActiveResponseByApi(false);
		}
		// Boolean updated = TokenSecurity.getInstance().checkToken(token);
		Boolean updated = tokenSecurity.checkToken(token);

		return this.ActiveResponseByApi(updated);
	}

	
	private ResponseEntity<?> ActiveReponse(Boolean updated){
		if (!updated) {
			List<Map> loggedUser = null;
			
			getObjectResponse = new GetObjectResponse(HttpStatus.UNAUTHORIZED.value(), "UNAUTORIZED",loggedUser);
			
			return  ResponseEntity.status(401).body(getObjectResponse);
		}
		return null;
	}
	private ApiResponse ActiveResponseByApi(Boolean updated){
		if (!updated) {
			List<Map> loggedUser = null;

			builder.setMessage("TOKEN id is required");
			builder.setStatusCode(HttpStatus.UNAUTHORIZED.value());
			builder.setEntity(loggedUser);
			builder.setSize(0);
			builder.setSuccess(false);

			return builder.build();
		}
		return null;
	}
}
