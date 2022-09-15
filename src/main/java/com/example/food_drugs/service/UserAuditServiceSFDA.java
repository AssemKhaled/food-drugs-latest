package com.example.food_drugs.service;

import com.example.food_drugs.dto.ApiResponse;
import com.example.food_drugs.dto.Request.UserAuditRequest;
import com.example.food_drugs.entity.UserAuditLogger;

import java.util.List;

/**
 * @author Assem
 */
public interface UserAuditServiceSFDA {
    ApiResponse<List<UserAuditLogger>> getUserLogs(Long userId, String from, String to, List<String> search, String timeOffset,Integer size, UserAuditRequest userAuditRequest);

}
