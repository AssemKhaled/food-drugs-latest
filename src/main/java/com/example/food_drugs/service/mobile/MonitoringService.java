package com.example.food_drugs.service.mobile;

import java.util.*;

import com.example.food_drugs.dto.responses.MongoInventoryWrapper;
import com.example.food_drugs.dto.responses.ResponseWrapper;
import com.example.food_drugs.dto.responses.mobile.DeviceMonitoringResponse;
import com.example.food_drugs.dto.responses.mobile.InventoryDataResponse;
import com.example.food_drugs.dto.responses.mobile.MonitoringDevicePositionResponse;


public interface MonitoringService {
    ResponseWrapper<List<DeviceMonitoringResponse>> monitoringDeviceList(String TOKEN , Long userId, int offset , int size,String search);
    ResponseWrapper<MonitoringDevicePositionResponse> monitoringGetDevicePosition(String TOKEN , Long deviceId);
    ResponseWrapper<List<MongoInventoryWrapper>> monitoringGetAllInventoriesLastInfo(String TOKEN, Long userId, int offset,String search);

    ResponseWrapper<InventoryDataResponse> monitoringGetDetailsInventory(String TOKEN, Long inventoryId);



}
