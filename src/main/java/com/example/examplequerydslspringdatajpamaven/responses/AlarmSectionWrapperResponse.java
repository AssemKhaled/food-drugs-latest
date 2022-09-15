package com.example.examplequerydslspringdatajpamaven.responses;

import lombok.*;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class AlarmSectionWrapperResponse {
    String alarmCondition;
    String firstAlarmTime;
    int numOfAlarms;
}
