package com.example.food_drugs.service;

import com.example.examplequerydslspringdatajpamaven.entity.UserSelect;
import com.example.food_drugs.dto.ApiResponse;
import com.example.food_drugs.dto.Request.UserAuditRequest;
import com.example.food_drugs.entity.UserAuditLogger;

import java.util.List;


public interface UserServiceSFDA {
    ApiResponse<List<UserSelect>> getUsersChildren(Long userId);
}
