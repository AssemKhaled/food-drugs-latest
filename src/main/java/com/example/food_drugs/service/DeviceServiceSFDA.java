package com.example.food_drugs.service;

import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.food_drugs.dto.ApiResponse;
import com.example.food_drugs.dto.responses.GraphDataWrapper;
import com.example.food_drugs.dto.responses.VehicleListDashBoardResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Service
public interface DeviceServiceSFDA {

	public  ResponseEntity<?> activeDeviceSFDA(String TOKEN,Long userId, Long deviceId);
	public ResponseEntity<?>  getAllUserDevicesSFDA(String TOKEN,Long userId , int offset, String search, int active,String exportData);
	ResponseEntity<?> getDeviceGraphData(String TOKEN,Long userId) throws IOException;
	ResponseEntity<?> getDeviceGraphDataDashboard(String TOKEN,Long userId,int offset,int size);
	ResponseEntity<GetObjectResponse<GraphDataWrapper>> getDataForGraphByDeviceID(int deviceID);
	void startAndEndDate() throws ParseException;
	ApiResponse<List<VehicleListDashBoardResponse>> vehicleListDashBoard (String TOKEN , Long userId);

}
