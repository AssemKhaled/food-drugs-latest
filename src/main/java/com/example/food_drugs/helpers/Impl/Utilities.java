package com.example.food_drugs.helpers.Impl;

import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Repository
public class Utilities {

    public double speedConverter(Double speed){
        double convertedSpeed = Math.abs(speed * (1.852));
        return Math.round(convertedSpeed*100.0)/100.0;
    }

    public String monitoringTimeZoneConverter(Date date, String timeOffset){
        if(timeOffset.contains("%2B")){
            timeOffset = "+" + timeOffset.substring(3);
        }
        if(date != null){
            ZoneOffset zo = ZoneOffset.of(timeOffset);
            OffsetDateTime odt = OffsetDateTime.ofInstant(date.toInstant(), zo);
          return String.valueOf(odt).substring(0,19).replace("T", " ");
        }
        return null;
    }

    public String timeZoneConverter(Date date, String timeOffset){
//        if(timeOffset.contains("%2B")){
//            timeOffset = "+" + timeOffset.substring(3);
//        }
//        if(date != null){
//            ZoneOffset zo = ZoneOffset.of(timeOffset);
//            OffsetDateTime odt = OffsetDateTime.ofInstant(date.toInstant(), zo);
//            return String.valueOf(odt).substring(0,19).replace("T", " ");
//        }
//        return null;
//        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");
//        if(timeOffset.contains("%2B")){
//            timeOffset = "+" + timeOffset.substring(3);
//        }
//        if(date != null){
//            ZoneOffset zo = ZoneOffset.of(timeOffset);
//            String odt = OffsetDateTime.ofInstant(date.toInstant(), zo).format(DateTimeFormatter.ISO_DATE_TIME);
//            String sDate = odt.substring(0,19).replace("T", " ");
//
//            return sDate;
//        }
//        return null;
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");
        if(timeOffset.contains("%2B")){
            timeOffset = "+" + timeOffset.substring(3);
        }
        if(date != null){
            ZoneOffset zo = ZoneOffset.of(timeOffset);
            String odt = OffsetDateTime.ofInstant(date.toInstant(), zo).format(DateTimeFormatter.ISO_DATE_TIME);
            String sDate = odt.substring(0,19).replace("T", " ");

            return sDate;
        }
        return null;
    }

    public Double humidityCalculations(Map<String,Object> obj){

        int countHum = 0;
        double Hum = 0.0;
        double avgHum = 0.0;

        if(obj.containsKey("hum1")) {
            if(Double.parseDouble(obj.get("hum1").toString()) != 0) {
                Hum = Hum + Double.parseDouble(obj.get("hum1").toString());
                countHum = countHum + 1;
            }
        }
        if(obj.containsKey("hum2")) {
            if(Double.parseDouble(obj.get("hum2").toString()) != 0) {
                Hum = Hum + Double.parseDouble(obj.get("hum2").toString());
                countHum = countHum + 1;
            }
        }
        if(obj.containsKey("hum3")) {
            if(Double.parseDouble(obj.get("hum3").toString()) != 0) {
                Hum = Hum + Double.parseDouble(obj.get("hum3").toString());
                countHum = countHum + 1;
            }
        }
        if(obj.containsKey("hum4")) {
            if(Double.parseDouble(obj.get("hum4").toString()) != 0) {
                Hum = Hum + Double.parseDouble(obj.get("hum4").toString());
                countHum = countHum + 1;
            }
        }
        if(obj.containsKey("wirehum1")) {
            if(Double.parseDouble(obj.get("wirehum1").toString()) != 0) {
                Hum = Hum + Double.parseDouble(obj.get("wirehum1").toString());
                countHum = countHum + 1;
            }
        }
        if(obj.containsKey("wirehum2")) {
            if(Double.parseDouble(obj.get("wirehum2").toString()) != 0) {
                Hum = Hum + Double.parseDouble(obj.get("wirehum2").toString());
                countHum = countHum + 1;
            }
        }
        if(obj.containsKey("wirehum3")) {
            if(Double.parseDouble(obj.get("wirehum3").toString()) != 0) {
                Hum = Hum + Double.parseDouble(obj.get("wirehum3").toString());
                countHum = countHum + 1;
            }
        }
        if(obj.containsKey("wirehum4")) {
            if(Double.parseDouble(obj.get("wirehum4").toString()) != 0) {
                Hum = Hum + Double.parseDouble(obj.get("wirehum4").toString());
                countHum = countHum + 1;
            }
        }
        if(countHum != 0) {
            avgHum = Hum / countHum;
        }
        return Math.round(avgHum * 100.0) / 100.0;
    }

    public Double temperatureCalculations(Map<String,Object> obj){
        int countTemp = 0;
        double Temp =  0.0;
        double avgTemp = 0.0;

        if(obj.containsKey("temp1")) {
            if(Double.parseDouble(obj.get("temp1").toString()) != 0) {
                Temp = Temp + Double.parseDouble(obj.get("temp1").toString());
                countTemp = countTemp + 1;
            }
        }
        if(obj.containsKey("temp2")) {
            if(Double.parseDouble(obj.get("temp2").toString()) != 0) {
                Temp = Temp + Double.parseDouble(obj.get("temp2").toString());
                countTemp = countTemp + 1;
            }
        }
        if(obj.containsKey("temp3")) {
            if(Double.parseDouble(obj.get("temp3").toString()) != 0) {
                Temp = Temp + Double.parseDouble(obj.get("temp3").toString());
                countTemp = countTemp + 1;
            }
        }
        if(obj.containsKey("temp4")) {
            if(Double.parseDouble(obj.get("temp4").toString()) != 0) {
                Temp = Temp + Double.parseDouble(obj.get("temp4").toString());
                countTemp = countTemp + 1;
            }
        }
        if(obj.containsKey("wiretemp1")) {
            if(Double.parseDouble(obj.get("wiretemp1").toString()) != 0) {
                Temp = Temp + Double.parseDouble(obj.get("wiretemp1").toString());
                countTemp = countTemp + 1;
            }
        }
        if(obj.containsKey("wiretemp2")) {
            if(Double.parseDouble(obj.get("wiretemp2").toString()) != 0) {
                Temp = Temp + Double.parseDouble(obj.get("wiretemp2").toString());
                countTemp = countTemp + 1;
            }
        }
        if(obj.containsKey("wiretemp3")) {
            if(Double.parseDouble(obj.get("wiretemp3").toString()) != 0) {
                Temp = Temp + Double.parseDouble(obj.get("wiretemp3").toString());
                countTemp = countTemp + 1;
            }
        }
        if(obj.containsKey("wiretemp4")) {
            if(Double.parseDouble(obj.get("wiretemp4").toString()) != 0) {
                Temp = Temp + Double.parseDouble(obj.get("wiretemp4").toString());
                countTemp = countTemp + 1;
            }
        }
        if(countTemp != 0) {
            avgTemp = Temp / countTemp;
        }

        return Math.round(avgTemp * 100.0) / 100.0;
    }

    public String durationCalculation(String duration){
        long timeDuration = 0;
        String totalDuration = "00:00:00";

        timeDuration = Math.abs(Long.parseLong(duration));

        Long hoursDuration =   TimeUnit.MILLISECONDS.toHours(timeDuration) ;
        Long minutesDuration = TimeUnit.MILLISECONDS.toMinutes(timeDuration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDuration));
        Long secondsDuration = TimeUnit.MILLISECONDS.toSeconds(timeDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDuration));

        totalDuration = String.valueOf(hoursDuration)+":"+String.valueOf(minutesDuration)+":"+String.valueOf(secondsDuration);
        return totalDuration;
    }

}
