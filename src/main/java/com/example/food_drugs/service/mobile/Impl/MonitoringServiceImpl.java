package com.example.food_drugs.service.mobile.Impl;

import com.example.examplequerydslspringdatajpamaven.entity.Device;

import com.example.food_drugs.dto.AttributesWrapper;

import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.MongoPositions;
import com.example.examplequerydslspringdatajpamaven.repository.DriverRepository;
import com.example.examplequerydslspringdatajpamaven.repository.MongoPositionsRepository;

import com.example.food_drugs.dto.StoringCategoryCondition;
import com.example.food_drugs.dto.responses.mobile.InventoryDataResponse;
import com.example.food_drugs.entity.Inventory;

import com.example.food_drugs.entity.MonogoInventoryLastData;
import com.example.food_drugs.entity.Position;
import com.example.food_drugs.entity.Warehouse;
import com.example.food_drugs.helpers.Impl.*;
import com.example.food_drugs.helpers.Impl.Dictionary;
import com.example.food_drugs.repository.*;

import com.example.food_drugs.dto.responses.InventorySummaryDataWrapper;
import com.example.food_drugs.dto.responses.MongoInventoryWrapper;
import com.example.food_drugs.dto.responses.ResponseWrapper;
import com.example.food_drugs.dto.responses.mobile.DeviceMonitoringResponse;
import com.example.food_drugs.dto.responses.mobile.MonitoringDevicePositionResponse;
import com.example.food_drugs.service.mobile.MonitoringService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
public class MonitoringServiceImpl implements MonitoringService {



    private final InventoryRepository inventoryRepository;
    private final MongoInventoryLastDataRepository mongoInventoryLastDataRepository;
    private final WarehousesRepository warehousesRepository;

    private  final MongoPositionsRepository mongoPositionsRepository;
    private  final DriverRepository driverRepository;
    private final Utilities utilities;

    @Autowired
    private  Dictionary dictionary;

    private final UserHelper userHelper;
    private final PositionMongoSFDARepository positionMongoSFDARepository;
    private final DeviceHelper deviceHelper;
    private final DeviceRepositorySFDA deviceRepositorySFDA;
    private static final Log logger = LogFactory.getLog(MonitoringServiceImpl.class);

    public MonitoringServiceImpl(InventoryRepository inventoryRepository, MongoInventoryLastDataRepository mongoInventoryLastDataRepository, WarehousesRepository warehousesRepository, MongoPositionsRepository mongoPositionsRepository, DriverRepository driverRepository, Utilities utilities, UserHelper userHelper, PositionMongoSFDARepository positionMongoSFDARepository,
                                 DeviceHelper deviceHelper, DeviceRepositorySFDA deviceRepositorySFDA){
        this.inventoryRepository = inventoryRepository;
        this.mongoInventoryLastDataRepository = mongoInventoryLastDataRepository;
        this.warehousesRepository = warehousesRepository;
        this.mongoPositionsRepository = mongoPositionsRepository;
        this.driverRepository = driverRepository;
        this.utilities = utilities;
        this.userHelper = userHelper;
        this.positionMongoSFDARepository = positionMongoSFDARepository;
        this.deviceHelper = deviceHelper;
        this.deviceRepositorySFDA = deviceRepositorySFDA;
    }


    @Override
    public ResponseWrapper<List<DeviceMonitoringResponse>> monitoringDeviceList(String TOKEN,Long userId , int offset , int size , String search) {
        logger.info("******************** monitoringDeviceList Service Started ********************");
        ResponseHandler<List<DeviceMonitoringResponse>> responseHandler = new ResponseHandler<>();
        Date dateLast = new Date();
        ResponseWrapper<List<DeviceMonitoringResponse>> userResponseWrapper = userHelper.userErrorsChecker(TOKEN,userId);
        if(!userResponseWrapper.getSuccess()){
            return userResponseWrapper;
        }

        try {
            List<Long> userIds = userHelper.getUserChildrenId(userId) ;
            List<Object[]> deviceMonitoringList = deviceRepositorySFDA.getDeviceByUserIdsWithLimitAndSize(userIds ,offset ,size);
            List<DeviceMonitoringResponse> deviceMonitoringResponses =
                    new ArrayList<>();
            int dataSize = deviceRepositorySFDA.countDeviceByUserIds(userIds);
            if(deviceMonitoringList.size()<1){
                return responseHandler.reportError("Error");
            }
            for(Object[] device : deviceMonitoringList){
             try {
                 DecimalFormat decimalFormat = new DecimalFormat(".###");
                 AttributesWrapper attributes = new ObjectMapper().readValue((String) device[5],AttributesWrapper.class);
//                 String storingCategory;
                 StoringCategoryCondition storingCategoryCondition = new StoringCategoryCondition();
                 if (attributes.getStoringCategory()==null||attributes.getActivity()==null){
                     storingCategoryCondition.setTemperatureCondition("");
                     storingCategoryCondition.setMaxHumidity(0.0);
                     storingCategoryCondition.setMinTemperature(0.0);
                     storingCategoryCondition.setMaxTemperature(0.0);
                     storingCategoryCondition.setMinHumidity(0.0);
                     storingCategoryCondition.setHumidityContition("");
                     

                 }else {
                     storingCategoryCondition=deviceHelper.StoringCategoryConditionDetector(attributes.getStoringCategory());
                 }

                    Position position = positionMongoSFDARepository.findOne((String) device[6]);
                 if(device[2].toString() != null) {
                     SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                     Date now = new Date();
                     String strDate = formatter.format(now);
                     try {
                         dateLast = formatter.parse(device[2].toString());
                         Date dateNow = formatter.parse(strDate);

                     } catch (ParseException e) {
                         e.printStackTrace();
                     }
                 }else {
                     dateLast = null;
                 }
                    if(position==null){
                        deviceMonitoringResponses.add(DeviceMonitoringResponse
                                .builder()
                                .deviceName((String) device[0])
                                .id((Integer) device[1])
                                .lastUpdate(utilities.timeZoneConverter(dateLast,"%2B0300"))
                                .lastTemp(device[3]!=null?Double.parseDouble(decimalFormat.format(device[3])):300)
                                .lastHum(device[4]!=null?Double.parseDouble(decimalFormat.format(device[4])):300)
                                .storingCategory(storingCategoryCondition)
                                .speed(0.0)
                                .ignition(false)
                                .power(0.0)
                                .cooler(Long.valueOf(0))
                                .status("offline")
                                .gpsStatus(false)
                                .build());
                    }else {

                        deviceMonitoringResponses.add(DeviceMonitoringResponse
                                .builder()
                                .deviceName((String) device[0])
                                .id((Integer) device[1])
                                .lastUpdate(utilities.timeZoneConverter(dateLast,"%2B0300"))
                                .lastTemp(device[3] != null ? Double.parseDouble(decimalFormat.format(device[3])) : 300)
                                .lastHum(device[4] != null ? Double.parseDouble(decimalFormat.format(device[4])) : 300)
                                .storingCategory(storingCategoryCondition)
                                .speed(position.getSpeed() == null ? 0 : Double.parseDouble(decimalFormat.format(position.getSpeed())))
                                .ignition(position.getAttributes().get("ignition") != null ? (Boolean) position.getAttributes().get("ignition") : false)
                                .power(position.getAttributes().get("power") != null ? Double.parseDouble(decimalFormat.format(position.getAttributes().get("power"))) : -1)
                                .cooler(position.getAttributes().get("AC") != null ? (Long) position.getAttributes().get("AC") : 300)
                                .status(deviceHelper.deviceStatuesDetector(position.getServertime()))
                                .gpsStatus(deviceHelper.deviceGPSDetector(position))
                                .build());
                    }
                 }catch (Error | Exception e){
                    responseHandler.errorLogger("Fail To Map SQL Data To DeviceMonitoringResponse In case :  " + Arrays.toString(device)
                            +" \n Position : " + device[6].toString());
             }
            }
            if(deviceMonitoringResponses.size()>0 && Pattern.matches(".*\\S.*" , search)){
                deviceMonitoringResponses = deviceMonitoringResponses.stream().filter(deviceMonitoringResponse ->
                        deviceMonitoringResponse.getDeviceName().contains(search)).collect(Collectors.toList());
            }
            logger.info("******************** monitoringDeviceList Service Ended With Success ********************");
            return responseHandler.reportSuccess("Success", deviceMonitoringResponses,dataSize);
        } catch (Error | Exception e) {
            logger.info("******************** monitoringDeviceList Service Started With Error "+e.getMessage()+" ********************");
            return responseHandler.reportError(e.getMessage());
        }
    }

    @Override
    public ResponseWrapper<MonitoringDevicePositionResponse> monitoringGetDevicePosition(String TOKEN, Long deviceId) {

        ResponseHandler<MonitoringDevicePositionResponse> responseHandler = new ResponseHandler<>();
        logger.info("******************** monitoringGetDevicePosition Service Started ********************");
        ResponseWrapper<MonitoringDevicePositionResponse> userResponseWrapper = userHelper.userTokenErrorsChecker(TOKEN);
        if(!userResponseWrapper.getSuccess()){
            return userResponseWrapper;
        }
        MongoPositions mongoPositions ;
        try {
            DecimalFormat decimalFormat = new DecimalFormat(".###");
//            Optional<Position> lastPositionOptional = positionMongoSFDARepository.findFirstByDeviceidOrderByServertimeDesc(deviceId);
            Device device = deviceRepositorySFDA.findOne(deviceId);
            Optional<MongoPositions> optionalMongoPositions=mongoPositionsRepository.findAllByDeviceid(deviceId);



            if(device == null){
                return responseHandler.reportError("Device Not Found With Id : "+deviceId);
            }

//            if(!lastPositionOptional.isPresent()){
//                return responseHandler.reportError("Position Not Found With Id : "+deviceId);
//            }
//            Position lastPosition = lastPositionOptional.get();
            Position lastPosition = positionMongoSFDARepository.findOne(device.getPositionid());;
            if (optionalMongoPositions.isPresent()) {
                mongoPositions = optionalMongoPositions.get();
//                System.out.println(mongoPositions.getDeviceName());
                Driver driver = driverRepository.findById(mongoPositions.getDriverid());

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                return responseHandler.reportSuccess("Success",
                        MonitoringDevicePositionResponse
                                .builder()
                                .deviceName(device.getName())
                                .driverName(mongoPositions.getDriverName())
                                .sequence_num(device.getSequence_number())
                                .trackerimei(device.getUniqueid())
                                .mobile_num(driver.getMobile_num())
                                .lastupdate(device.getLastupdate())
                                .lasttemp(device.getLastTemp())
                                .lasthum(device.getLastHum())
                                .speed(Double.parseDouble(decimalFormat.format(lastPosition.getSpeed())))
                                .latitude(lastPosition.getLatitude())
                                .longitude(lastPosition.getLongitude())
                                .humidity(deviceHelper.findValueFromMap(lastPosition.getAttributes(), "hum"))
                                .temperature(deviceHelper.findValueFromMap(lastPosition.getAttributes(), "temp"))
                                .serverTime(simpleDateFormat.format(lastPosition.getServertime()))
                                .status(deviceHelper.deviceStatuesDetector(lastPosition.getServertime()))
                                .ignition(lastPosition.getAttributes().get("ignition") != null ? (Boolean) lastPosition.getAttributes().get("ignition") : false)
                                .power(lastPosition.getAttributes().get("power") != null ? Double.parseDouble(decimalFormat.format(lastPosition.getAttributes().get("power"))) : -1)
                                .cooler(lastPosition.getAttributes().get("AC") != null ? (Long) lastPosition.getAttributes().get("AC") : 300)
                                .gpsStatus(deviceHelper.deviceGPSDetector(lastPosition))
                                .build());
            }else {
                return null;
            }
        }catch (Error | Exception e){
            logger.info("******************** monitoringGetDevicePosition Service Started With Error "+e.getMessage()+" ********************");
            return responseHandler.reportError(e.getMessage());
        }
    }




    public ResponseWrapper<InventoryDataResponse> monitoringGetDetailsInventory(String TOKEN, Long inventoryId){

        ResponseHandler<InventoryDataResponse> responseHandler = new ResponseHandler<>();
//        logger.info("******************** monitoringGetDevicePosition Service Started ********************");
        ResponseWrapper<MonitoringDevicePositionResponse> userResponseWrapper = userHelper.userTokenErrorsChecker(TOKEN);
        if(!userResponseWrapper.getSuccess()){
//            return userResponseWrapper;
            return null;
        }
        MonogoInventoryLastData monogoInventoryLastData;
        Warehouse warehouse;
        try{
            Inventory inventory=inventoryRepository.findById(inventoryId);

            if (inventory==null){


                return responseHandler.reportError("Inventory Not Found with Id"+inventoryId);
            }
            Optional<MonogoInventoryLastData> optionalMonogoInventoryLastData=mongoInventoryLastDataRepository.findBy_id(inventory.getLastDataId());
            Optional<Warehouse>optionalWarehouse=warehousesRepository.findById(inventory.getWarehouseId());

            if (optionalMonogoInventoryLastData.isPresent()){
//

                monogoInventoryLastData=optionalMonogoInventoryLastData.get();
                warehouse=optionalWarehouse.get();

//                String type=inventory.getStoringCategory();
//              type=dictionary.Type(type);

                return responseHandler.reportSuccess("Success",
                        InventoryDataResponse.builder().
                                inventoryName(inventory.getName()).
                                inventoryNumber(inventory.getInventoryNumber()).
                                inventoryStoringCategory(dictionary.Type(inventory.getStoringCategory())).
                                lastTemp(monogoInventoryLastData.getTemperature()).lastHum(monogoInventoryLastData.getHumidity()).lastUpDate(inventory.getLastUpdate())
                                .assignWarehouseName(warehouse.getName())
                                .build()
                );
            }else {
                return responseHandler.reportError("error");
            }



        }catch (Error|Exception e){
            logger.info("******************** monitoringGetDevicePosition Service Started With Error "+e.getMessage()+" ********************");
            return responseHandler.reportError(e.getMessage());
        }

    }



    @Override
    public ResponseWrapper<List<MongoInventoryWrapper>> monitoringGetAllInventoriesLastInfo(String TOKEN, Long userId, int offset, String search) {
        logger.info("************************ monitoringGetAllInventoriesLastInfo STARTED ***************************");
        ResponseHandler<List<MongoInventoryWrapper>>  responseHandler = new ResponseHandler<>();
        ResponseWrapper<List<MongoInventoryWrapper>> userResponseWrapper = userHelper.userErrorsChecker(TOKEN,userId);
        if(!userResponseWrapper.getSuccess()){
            return userResponseWrapper;
        }
        try {
            List<MongoInventoryWrapper> inventoryLastData =  new ArrayList<>();
            List<Long>usersIds= userHelper.getUserChildrenId(userId);
            List<InventorySummaryDataWrapper> allInventoriesSumDataFromMySQL = inventoryRepository.getAllInventoriesSummaryData(usersIds,offset);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            DecimalFormat df = new DecimalFormat("#.##");
            for (InventorySummaryDataWrapper inventorySummaryWrapper : allInventoriesSumDataFromMySQL){
                if(inventorySummaryWrapper.getLastDataId()!=null){
                    MonogoInventoryLastData mongoInv = mongoInventoryLastDataRepository.findById(inventorySummaryWrapper.getName());
                    if(mongoInv!=null){
                        inventoryLastData.add(
                                MongoInventoryWrapper
                                        .builder()
                                        ._id(mongoInv.get_id())
                                        .temperature(Double.valueOf(df.format(mongoInv.getTemperature())))
                                        .inventoryId(mongoInv.getInventoryId())
                                        .inventoryName(inventorySummaryWrapper.getLastDataId())
                                        .createDate(simpleDateFormat.format(mongoInv.getCreateDate()))
                                        .humidity(Double.valueOf(df.format(mongoInv.getHumidity())))
                                        .build());
                    }
                }
            }

            Integer size=inventoryRepository.getInventoriesSize(usersIds);
            if(inventoryLastData.size()>0 && Pattern.matches(".*\\S.*" , search)){
                inventoryLastData = inventoryLastData.stream().filter(inventoryLastDates ->
                        inventoryLastDates.getInventoryName().contains(search)).collect(Collectors.toList());
            }
            logger.info("************************ monitoringGetAllInventoriesLastInfo ENDED SUCCESS ***************************");
            return responseHandler.reportSuccess("Success",inventoryLastData,size);
        }catch (Exception | Error e){
            logger.info("************************ monitoringGetAllInventoriesLastInfo ENDED With ERROR ***************************");
            return responseHandler.reportError(e.getMessage());
        }


    }


}