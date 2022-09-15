package com.example.food_drugs.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PdfSummaryData {
	
	private Double avgTemp;
	
	private Double maxTemp;
	
	private Double minTemp;
	
	private Integer totalLength;
	
	private Double mkt;

	private Double averageHum;

	private Double maxHum;

	private Double minHum;

	private String deviceName;
	private String driverName;
	private String companyName;
	private String duration;



}
