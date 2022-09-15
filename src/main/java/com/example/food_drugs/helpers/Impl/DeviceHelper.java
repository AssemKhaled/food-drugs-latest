package com.example.food_drugs.helpers.Impl;

import com.example.food_drugs.dto.StoringCategoryCondition;
import com.example.food_drugs.entity.Position;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Repository
public class DeviceHelper {

    public StoringCategoryCondition StoringCategoryConditionDetector(String storingCategory){
        switch (storingCategory){
            case "SCD1":
            case "SCM1":
                return StoringCategoryCondition
                        .builder()
                            .temperatureCondition("Between -20°C and -10°C")
                            .minTemperature(-20)
                            .maxTemperature(-10)
                        .build();
            case "SCD2":
            case "SCM2":
                return StoringCategoryCondition
                        .builder()
                        .temperatureCondition("Between 2°C and 8°C")
                        .minTemperature(2)
                        .maxTemperature(8)
                        .build();
            case "SCD3":
            case "SCC1":
                return StoringCategoryCondition
                        .builder()
                        .temperatureCondition("Over 25°C")
                        .humidityContition("Over 60%")
                        .maxTemperature(25)
                        .maxHumidity(60)
                        .build();
            case "SCM3":
                return StoringCategoryCondition
                        .builder()
                        .temperatureCondition("Between 8°C and 15°C")
                        .humidityContition("Over 60%")
                        .minTemperature(8)
                        .maxTemperature(15)
                        .maxHumidity(60)
                        .build();
            case "SCM4":
                return StoringCategoryCondition
                        .builder()
                        .temperatureCondition("Between 15°C and 30°C")
                        .humidityContition("Over 60%")
                        .minTemperature(15)
                        .maxTemperature(30)
                        .maxHumidity(60)
                        .build();
            case "SCM5":
                return StoringCategoryCondition
                        .builder()
                        .temperatureCondition("Over 40°C")
                        .maxTemperature(40)
                        .build();
            case "SCF1":
                return StoringCategoryCondition
                        .builder()
                        .temperatureCondition("Temperature Over 25°C ")
                        .humidityContition("Over 60%")
                        .maxTemperature(25)
                        .maxHumidity(60)
                        .build();
            case "SCF2":
                return StoringCategoryCondition
                        .builder()
                        .temperatureCondition("Between -1.5°C and 10°C")
                        .humidityContition("Between 75% and 90%")
                        .minTemperature(-1.5)
                        .maxTemperature(10)
                        .minHumidity(75)
                        .maxHumidity(90)
                        .build();
            case "SCF3":
                return StoringCategoryCondition
                        .builder()
                        .temperatureCondition("Between -1.5°C and 21°C")
                        .humidityContition("Between 85% and 90%")
                        .minTemperature(-1.5)
                        .maxTemperature(21)
                        .minHumidity(85)
                        .maxHumidity(90)
                        .build();
            case "SCF4":
                return StoringCategoryCondition
                        .builder()
                        .temperatureCondition("Over -18°C")
                        .humidityContition("Between 75% and 99%")
                        .maxTemperature(-18)
                        .minHumidity(75)
                        .maxHumidity(99)
                        .build();
            case "SCA1":
                return StoringCategoryCondition
                        .builder()
                        .temperatureCondition("Over 30°C")
                        .humidityContition("Over 60%")
                        .maxTemperature(30)
                        .maxHumidity(60)
                        .build();
            case "SCP1":
                return StoringCategoryCondition
                        .builder()
                        .temperatureCondition("Over 35°C")
                        .maxTemperature(35)
                        .build();
            default:
                return StoringCategoryCondition
                        .builder()
                        .build();
        }
    }

    public double findValueFromMap(Map<String,Object> attributes , String founderKey){
        double returnedValue  = 0.0;
        for (Map.Entry<String, Object> set:attributes.entrySet()) {
            String key  = set.getKey() ;
            Object value = set.getValue() ;
            if(key.contains(founderKey)){
                if(Double.parseDouble(value.toString())>0.0
                        &&Double.parseDouble(value.toString())<300.0){
                    returnedValue=Double.parseDouble(value.toString());
                    return Math.round(returnedValue * 100.0) / 100.0;
                }
            }
        }
        return returnedValue;
    }

    public static long getDateDiffByMin(Date date1, Date date2, TimeUnit timeUnit) {
        return timeUnit.convert(date2.getTime() - date1.getTime(), TimeUnit.MILLISECONDS);
    }

    public String deviceStatuesDetector(Date lastUpdate){
        Date now = new Date();
        long minutes;
        minutes = getDateDiffByMin (lastUpdate, now, TimeUnit.MINUTES);
        if(minutes <= 3) {
            return "online";
        }
        else if(minutes >= 8) {
            return "offline";
        }
        return "unknown";
    }

    public Boolean deviceGPSDetector(Position position){
        return ! (position.getLatitude() == 0 && position.getLongitude() == 0) ;
    }

}
