package com.example.food_drugs.service;

import com.example.food_drugs.dto.ApiResponse;
import com.example.food_drugs.dto.Request.NotificationSettingRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface NotificationServiceSFDA {

	public ResponseEntity<?> activeNotification(String TOKEN,Long notificationId,Long userId);
	public ResponseEntity<?> getAllNotificationsSFDA(String TOKEN,Long id,int offset,String search,int active,String exportData);
	ApiResponse<NotificationSettingRequest> notificationSetting(String TOKEN,Long userId, NotificationSettingRequest notificationSettingRequest);
	ApiResponse<NotificationSettingRequest> getNotificationSetting(String TOKEN,Long userId);

	ApiResponse sendEmail();
}
