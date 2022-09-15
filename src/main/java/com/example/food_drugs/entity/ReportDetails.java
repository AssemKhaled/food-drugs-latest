package com.example.food_drugs.entity;

import java.util.Date;

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
public class ReportDetails {
	
	private String date ;
	private String time;
	private Double temperature;
	private Double humidity;
	 

}
