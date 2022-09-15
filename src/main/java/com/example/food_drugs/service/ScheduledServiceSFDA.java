package com.example.food_drugs.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ScheduledServiceSFDA {

	public ResponseEntity<?> activeScheduled(String TOKEN,Long scheduledId,Long userId);
	public ResponseEntity<?> getScheduledListSFDA(String TOKEN,Long id,int offset,String search,int active,String exportData);

	public void accessExpressionSFDA(String expression);
	public void doReportsSFDA(String Expression);
	public boolean sendMailSFDA(String excelName,String email);
	public Boolean createExcelSFDA(String reportType,List<?> entity,String excelName,String[] columns);
}
