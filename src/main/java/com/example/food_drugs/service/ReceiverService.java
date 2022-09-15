package com.example.food_drugs.service;

import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.food_drugs.dto.responses.ReceiverDataWrapper;
import org.springframework.http.ResponseEntity;

public interface ReceiverService {
    ResponseEntity<GetObjectResponse<ReceiverDataWrapper>> createReceiver(String TOKEN , Long userId);
    ResponseEntity<GetObjectResponse<ReceiverDataWrapper>> deleteReceiver(String TOKEN , Long userId,Long receiverId);
    ResponseEntity<GetObjectResponse<ReceiverDataWrapper>> updateReceiver(String TOKEN , Long userId,Long receiverId);
    ResponseEntity<GetObjectResponse<ReceiverDataWrapper>> listReceivers(String TOKEN , Long userId);

}
