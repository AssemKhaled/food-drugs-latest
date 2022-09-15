package com.example.food_drugs.mappers;

import com.example.food_drugs.entity.Position;
import com.example.food_drugs.repository.DeviceRepositorySFDA;
import com.example.food_drugs.dto.responses.PositionResponse;


import java.util.Map;



public class PositionMapper {


    private final DeviceRepositorySFDA deviceRepositorySFDA;

    public PositionMapper(DeviceRepositorySFDA deviceRepositorySFDA) {
        this.deviceRepositorySFDA = deviceRepositorySFDA;
    }


    public PositionResponse convertToResponse(Position position){
        return PositionResponse.builder()
                ._id(position.get_id())
                .deviceName(deviceRepositorySFDA.findOne(position.getDeviceid()).getName())
                .serverTime(position.getServertime().toString())
                .speed(position.getSpeed())
                .temperature(findTemperature(position.getAttributes()))
                .humidity(findHumidity(position.getAttributes()))
                .ac((Long) position.getAttributes().get("AC"))
                .build();
    }
    double findTemperature(Map<String, Object> attributes){
        double temparature  = 0.0;
        for (Map.Entry<String, Object> set:attributes.entrySet()) {
            if(set.getKey().contains("temp")){
                if(Double.parseDouble(set.getValue().toString())>0.0){
                    temparature=Double.parseDouble(set.getValue().toString());
                    break;
                }
            }
        }
        return temparature;
    }
    double findHumidity(Map<String, Object> attributes){
        double humidity  = 0.0;
        for (Map.Entry<String, Object> set:attributes.entrySet()) {
            if(set.getKey().contains("hum")){
                if(Double.parseDouble(set.getValue().toString())>0.0){
                    humidity=Double.parseDouble(set.getValue().toString());
                    break;
                }
            }
        }
        return humidity;
    }
}
