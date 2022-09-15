package com.example.food_drugs.helpers.Impl;

import com.example.examplequerydslspringdatajpamaven.entity.*;
import com.example.examplequerydslspringdatajpamaven.repository.MongoPositionsRepository;
import com.example.examplequerydslspringdatajpamaven.service.DeviceServiceImpl;
import com.example.food_drugs.dto.StopReport;
import com.example.food_drugs.dto.SummaryReport;
import com.example.food_drugs.dto.TripReport;
import com.example.food_drugs.dto.DeviceTempHum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class ReportsHelper {

    private final DeviceServiceImpl deviceServiceImpl;
    private final Utilities utilities;
    private final MongoPositionsRepository mongoPositionsRepository;

    public ReportsHelper(@Lazy DeviceServiceImpl deviceServiceImpl, Utilities utilities, MongoPositionsRepository mongoPositionsRepository) {
        this.deviceServiceImpl = deviceServiceImpl;
        this.utilities = utilities;
        this.mongoPositionsRepository = mongoPositionsRepository;
    }

    public List<TripReport> tripReportProcessHandler(List<TripReport> tripReports, String timeOffset){

        for(TripReport tripReport : tripReports ) {

            double totalDistance = 0.0 ;
            double roundOffDistance = 0.0;
            double roundOffFuel = 0.0;
            double litres=10.0;
            double Fuel =0.0;
            double distance=0.0;

            Device device= deviceServiceImpl.findById(tripReport.getDeviceId());
            Set<User> companies = device.getUser();
            User user = new User();
            for(User company : companies) {
                user = company;
                break;
            }
            tripReport.setHum(device.getLastHum());
            tripReport.setTemp(device.getLastTemp());
//            String companyName = user.getName();
            tripReport.setCompanyName(user.getName());

            Set<Driver>  drivers = device.getDriver();
            for(Driver driver : drivers ) {
                tripReport.setDriverName(driver.getName());
                tripReport.setDriverUniqueId(driver.getUniqueid());
            }

            if(tripReport.getDistance() != null && tripReport.getDistance() != "") {
                totalDistance = Math.abs(  Double.parseDouble(tripReport.getDistance())/1000  );
                roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
                tripReport.setDistance(Double.toString(roundOffDistance));
            }
            if(device.getFuel() != null) {
                if(device.getFuel() != null && device.getFuel() != "" && device.getFuel().startsWith("{")) {
                    JSONObject obj = new JSONObject(device.getFuel());
                    if(obj.has("fuelPerKM")) {
                        litres=obj.getDouble("fuelPerKM");

                    }
                }
            }
            distance = Double.parseDouble(tripReport.getDistance().toString());
            if(distance > 0) {
                Fuel = (distance*litres)/100;
            }

            roundOffFuel = Math.round(Fuel * 100.0)/ 100.0;
            tripReport.setSpentFuel(Double.toString(roundOffFuel));

            if(tripReport.getDuration() != null && tripReport.getDuration() != "") {
                tripReport.setDuration(utilities.durationCalculation(tripReport.getDuration()));
            }

            if(tripReport.getAverageSpeed() != null && tripReport.getAverageSpeed() != "") {
                tripReport.setAverageSpeed(
                        String.valueOf(
                                utilities.speedConverter(Double.parseDouble(tripReport.getAverageSpeed()))
                        ));
            }
            if(tripReport.getMaxSpeed() != null && tripReport.getMaxSpeed() != "") {
                tripReport.setMaxSpeed(String.valueOf(
                        utilities.speedConverter(Double.parseDouble(tripReport.getMaxSpeed()))
                ));
            }

            if(tripReport.getStartTime() != null && tripReport.getStartTime() != "") {

//                if(timeOffset.equals("%2B0200")){
                    Date dateTime = new Date();

//                    TimeZone zt2 = TimeZone.getTimeZone(ZoneId.of("Africa/Cairo"));
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+SSSS");
//                    SimpleDateFormat inputFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+SSSS");
//                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    inputFormat.setTimeZone(TimeZone.getTimeZone("Africa/Cairo"));
                    SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

                    try {
//                        TimeZone etTimeZone = TimeZone.getTimeZone("UTC");
//                        inputFormat.setTimeZone(etTimeZone);
//                        if(dateTime != null){
////                            tripReport.setStartTime(utilities.timeZoneConverter(dateTime, timeOffset));
//                            tripReport.setStartTime(String.valueOf(asiaZonedDateTime));
//                        }
//                        else {
//                            tripReport.setStartTime(outputFormat.format(dateTime));
//                        }
//                    } catch (ParseException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//
//                }else if (timeOffset.equals("%2B0300")){
//                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+SSSS");
//                    inputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Riyadh"));
////                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//                    SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");
//
//                    try {
//                        Date  dateTime = inputFormat.parse(tripReport.getStartTime());
//                        if(dateTime != null){
//                            tripReport.setStartTime(utilities.timeZoneConverter(dateTime, timeOffset));
//                        }
//                        else {
//                            tripReport.setStartTime(outputFormat.format(dateTime));
//                        }
//                    } catch (ParseException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
                        dateTime = inputFormat.parse(tripReport.getStartTime());
                        if (dateTime !=null){
                            tripReport.setStartTime(utilities.timeZoneConverter(dateTime,timeOffset));
                        }
                        else {
                            tripReport.setStartTime(outputFormat.format(dateTime));
                        }
                    }catch (ParseException e){
                        e.printStackTrace();
                    }

            }
            if(tripReport.getEndTime() != null && tripReport.getEndTime() != "") {
                Date dateTime = null;
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+SSSS");
//                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

                try {

                    dateTime = inputFormat.parse(tripReport.getEndTime());
                    if(dateTime != null){
                        tripReport.setEndTime(utilities.timeZoneConverter(dateTime, timeOffset));
                    }
                    else {
                        tripReport.setEndTime(outputFormat.format(null));
                    }
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }
        }
        return tripReports;
    }

    public List<StopReport> stopReportProcessHandler(List<StopReport> stopReports, String timeOffset){
        long timeEngine= 0;
        String totalEngineHours = "00:00:00";

        for(StopReport stopReportOne : stopReports ) {
            Device device= deviceServiceImpl.findById(stopReportOne.getDeviceId());
            Set<Driver>  drivers = device.getDriver();

            for(Driver driver : drivers ) {

                stopReportOne.setDriverName(driver.getName());
                stopReportOne.setDriverUniqueId(driver.getUniqueid());
            }

            if(stopReportOne.getDuration() != null && stopReportOne.getDuration() != "") {
                stopReportOne.setDuration(utilities.durationCalculation(stopReportOne.getDuration()));
            }

            if(stopReportOne.getEngineHours() != null && stopReportOne.getEngineHours() != "") {
                stopReportOne.setEngineHours(utilities.durationCalculation(stopReportOne.getEngineHours()));
            }

            if(stopReportOne.getStartTime() != null && stopReportOne.getStartTime() != "") {
                Date dateTime = null;
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+SSSS");
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

                try {
                    TimeZone etTimeZone = TimeZone.getTimeZone("UTC");
                    inputFormat.setTimeZone(etTimeZone);
                    dateTime = inputFormat.parse(stopReportOne.getStartTime());

                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if(dateTime != null){
                    stopReportOne.setStartTime(utilities.timeZoneConverter(dateTime, timeOffset));
                }
                else {
                    stopReportOne.setStartTime(outputFormat.format(null));
                }
            }
            if(stopReportOne.getEndTime() != null && stopReportOne.getEndTime() != "") {
                Date dateTime = null;
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+SSSS");
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

                try {
                    TimeZone etTimeZone = TimeZone.getTimeZone("UTC");
                    inputFormat.setTimeZone(etTimeZone);
                    dateTime = inputFormat.parse(stopReportOne.getEndTime());

                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if(dateTime != null){
                    stopReportOne.setEndTime(utilities.timeZoneConverter(dateTime, timeOffset));
                }
                else {
                    stopReportOne.setEndTime(outputFormat.format(null));
                }
            }
        }

        return stopReports;
    }

    public List<SummaryReport> summaryReportProcessHandler(List<SummaryReport> summaryReports, String timeOffset){
        Double totalDistance = 0.0 ;
        double roundOffDistance = 0.0;
        double roundOffFuel = 0.0;
        Double litres=10.0;
        Double Fuel =0.0;
        Double distance=0.0;

        for(SummaryReport summaryReportOne : summaryReports ) {
            Device device= deviceServiceImpl.findById(summaryReportOne.getDeviceId());
            if(device != null) {
                Set<Driver> drivers = device.getDriver();
                for (Driver driver : drivers) {

                    summaryReportOne.setDriverName(driver.getName());
                }
                if (summaryReportOne.getDistance() != null && summaryReportOne.getDistance() != "") {
                    totalDistance = Math.abs(Double.parseDouble(summaryReportOne.getDistance()) / 1000);
                    roundOffDistance = Math.round(totalDistance * 100.0) / 100.0;
                    summaryReportOne.setDistance(Double.toString(roundOffDistance));
                }
                if(device.getFuel() != null) {
                    if(device.getFuel() != null && device.getFuel() != "" && device.getFuel().startsWith("{")) {
                        JSONObject obj = new JSONObject(device.getFuel());
                        if(obj.has("fuelPerKM")) {
                            litres=obj.getDouble("fuelPerKM");

                        }
                    }
                }

                distance = Double.parseDouble(summaryReportOne.getDistance().toString());
                if(distance > 0) {
                    Fuel = (distance*litres)/100;
                }

                roundOffFuel = Math.round(Fuel * 100.0 )/ 100.0;
                summaryReportOne.setSpentFuel(Double.toString(roundOffFuel));
            }

            if(summaryReportOne.getEngineHours() != null && summaryReportOne.getEngineHours() != "") {

                summaryReportOne.setEngineHours(utilities.durationCalculation(summaryReportOne.getEngineHours()));
            }

            if(summaryReportOne.getAverageSpeed() != null && summaryReportOne.getAverageSpeed() != "") {
                summaryReportOne.setAverageSpeed(String.valueOf(
                        utilities.speedConverter(Double.parseDouble(String.valueOf(roundOffDistance)))
                ));
            }

            if(summaryReportOne.getMaxSpeed() != null && summaryReportOne.getMaxSpeed() != "") {
                summaryReportOne.setMaxSpeed(String.valueOf(
                        utilities.speedConverter(Double.parseDouble(summaryReportOne.getMaxSpeed()))
                ));
            }
        }
        return summaryReports;
    }

    public List<DeviceTempHum>  deviceTempAndHumProcessHandler(List<MongoPositions> mongoPositions,String timeOffset){
        List<DeviceTempHum> positions = new ArrayList<>();
        ObjectMapper oMapper = new ObjectMapper();

        for(MongoPositions mongoPosition: mongoPositions){
            DeviceTempHum deviceTempHum = DeviceTempHum.builder()
                    .id(mongoPosition.get_id().toString())
                    .deviceId(mongoPosition.getDeviceid())
                    .deviceName(mongoPosition.getDeviceName())
                    .driverId(mongoPosition.getDriverid())
                    .driverName(mongoPosition.getDriverName())
                    .speed(utilities.speedConverter(mongoPosition.getSpeed()))
                    .deviceTime(utilities.timeZoneConverter(mongoPosition.getDevicetime(), timeOffset))
                    .temperature(utilities.temperatureCalculations(oMapper.convertValue(mongoPosition.getAttributes(), Map.class)))
                    .humidity(utilities.humidityCalculations(oMapper.convertValue(mongoPosition.getAttributes(), Map.class)))
                    .latitude(mongoPosition.getLatitude())
                    .longitude(mongoPosition.getLongitude())
                    .attributes(mongoPosition.getAttributes().toString())
                    .address(mongoPosition.getAddress())
                    .build();


            positions.add(deviceTempHum);
        }
        return positions;
    }

    public List<EventReport> eventsReportProcessHandler(List<MongoEvents> mongoEventsList, String timeOffset){
        List<EventReport> eventReportList = new ArrayList<>();
        MongoPositions position = null;

        for(MongoEvents mongoEvent: mongoEventsList){
            if(mongoEvent.getPositionid() != null){
                position = mongoPositionsRepository.findById(mongoEvent.getPositionid());
            }
            EventReport eventReport = EventReport.builder()
                    .deviceId(mongoEvent.getDeviceid())
                    .deviceName(mongoEvent.getDeviceName())
                    .driverId(mongoEvent.getDriverid())
                    .driverName(mongoEvent.getDriverName())
                    .eventType(mongoEvent.getType())
                    .geofenceId(mongoEvent.getGeofenceid())
                    .attributes(mongoEvent.getAttributes())
                    .positionId(mongoEvent.getPositionid())
                    .serverTime(utilities.timeZoneConverter(mongoEvent.getServertime(), timeOffset))
                    .eventId(mongoEvent.get_id().toString())
                    .latitude(position != null? position.getLatitude() : null)
                    .longitude(position != null? position.getLongitude() : null)
                    .build();

            eventReportList.add(eventReport);
        }
        return eventReportList;
    }
}
