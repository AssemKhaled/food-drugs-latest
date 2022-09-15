package com.example.food_drugs.dto.responses;


import java.sql.Date;

public interface DeviceDataGraph {
    int getName() ;
    int getId() ;
    Date getLastupdate();
    double getLastTemp();
    double getLastHum();
    String getStoringCategory();
}
