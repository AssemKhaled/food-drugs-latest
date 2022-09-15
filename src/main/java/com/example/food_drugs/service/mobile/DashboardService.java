package com.example.food_drugs.service.mobile;

import org.springframework.http.ResponseEntity;

public interface DashboardService {

    ResponseEntity<?> getListWarehousesInventories(String TOKEN, Long userId, int offset);
}
