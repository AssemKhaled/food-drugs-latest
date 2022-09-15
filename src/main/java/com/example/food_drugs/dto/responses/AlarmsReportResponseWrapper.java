package com.example.food_drugs.dto.responses;

import com.example.examplequerydslspringdatajpamaven.responses.AlarmSectionWrapperResponse;
import com.example.food_drugs.entity.PdfSummaryData;
import com.example.food_drugs.entity.ReportDetails;

import lombok.*;
import java.util.*;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class AlarmsReportResponseWrapper {
    List<AlarmSectionWrapperResponse> alarmsSection ;
    GraphDataWrapper temperatureDataGraph ;
    GraphDataWrapper humidityDataGraph ;
    List<List<ReportDetails>> reportDetailsData;
    PdfSummaryData summaryData;
}
