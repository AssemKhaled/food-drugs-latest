package com.example.food_drugs.service.impl;

import com.example.food_drugs.dto.ApiResponse;
import com.example.food_drugs.dto.ApiResponseBuilder;
import com.example.food_drugs.dto.Request.UserAuditRequest;
import com.example.food_drugs.entity.UserAuditLogger;
import com.example.food_drugs.helpers.Impl.Utilities;
import com.example.food_drugs.repository.UserAuditLoggerRepository;
import com.example.food_drugs.service.UserAuditServiceSFDA;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Assem
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserAuditServiceImplSFDA implements UserAuditServiceSFDA {

    private final UserAuditLoggerRepository userAuditLoggerRepository;
    private final Utilities utilities;
    @Override
    public ApiResponse<List<UserAuditLogger>> getUserLogs(Long userId, String from, String to, List<String> search, String timeOffset,Integer size, UserAuditRequest userAuditRequest) {
        log.info("************** Get User Logs STARTED**************");
        ApiResponseBuilder<List<UserAuditLogger>> builder = new ApiResponseBuilder<>();
        List<UserAuditLogger> result = new ArrayList<>(); long resultSize = 0L;
        List<String> deviceIds;List<String> driversIds;List<String> invIds;List<String> wareIds;
        Page<UserAuditLogger> optionalUserAuditLogger;List<String> allEntityStrings;List<String> allEntityIds = new ArrayList<>();

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date fromDate = new Date() ;Date toDate = new Date() ;
        try{
            fromDate = inputFormat.parse(from);
            toDate = inputFormat.parse(to);
        }catch (ParseException ignored){
        }

        if (search.isEmpty()) {
            optionalUserAuditLogger = userAuditLoggerRepository.findAllByUserIdAndDateBetweenOrderByDateDesc(userId, fromDate, toDate,new PageRequest(size,10));
           if(!optionalUserAuditLogger.getContent().isEmpty()){
               result.addAll(optionalUserAuditLogger.getContent());
           }
            resultSize = optionalUserAuditLogger.getTotalElements();
        }
        else {
            if (search.contains("DeviceSFDA")) {
                deviceIds = userAuditRequest.getDeviceIds()
                        .stream()
                        .map(Functions.toStringFunction()::apply)
                        .collect(Collectors.toList());
                for (String string:deviceIds) {
//                    string.concat("-DeviceSFDA");
                    allEntityIds.add(string+"-DeviceSFDA");
                }
                log.info(allEntityIds.toString());
            }
                if (search.contains("DriverSFDA")) {
                driversIds = userAuditRequest.getDriverIds()
                        .stream()
                        .map(Functions.toStringFunction()::apply)
                        .collect(Collectors.toList());
                for (String string:driversIds) {
//                    string.concat("-DriverSFDA");
                    allEntityIds.add(string+"-DriverSFDA");
                }
                log.info(allEntityIds.toString());
            }if (search.contains("Inventory")) {
                invIds = userAuditRequest.getInventoryIds()
                        .stream()
                        .map(Functions.toStringFunction()::apply)
                        .collect(Collectors.toList());
                for (String string:invIds) {
//                    string.concat("-Inventory");
                    allEntityIds.add(string+"-Inventory");
                }
            }if (search.contains("Warehouse")) {
                wareIds = userAuditRequest.getWareHouseIds()
                        .stream()
                        .map(Functions.toStringFunction()::apply)
                        .collect(Collectors.toList());
                for (String string:wareIds) {
//                    string.concat("-Warehouse");
                    allEntityIds.add(string+"-Warehouse");
                }
            }
            optionalUserAuditLogger = userAuditLoggerRepository
                        .findAllByUserIdAndEntityInAndGenericIdInAndDateBetweenOrderByDateDesc
                        (userId,search,allEntityIds,fromDate,toDate,new PageRequest(size,10));
                result.addAll(optionalUserAuditLogger.getContent());
                resultSize = optionalUserAuditLogger.getTotalElements();
//            if (!userAuditRequest.getDeviceIds().isEmpty()) {
//                deviceIds= userAuditRequest.getDeviceIds()
//                        .stream()
//                        .map(Functions.toStringFunction()::apply)
//                        .collect(Collectors.toList());
//                List<String> deviceStringList = new ArrayList<>();
//                for (String string:deviceIds) {
//                    deviceStringList.add(string+"-DeviceSFDA");
//                }
//                optionalUserAuditLogger = userAuditLoggerRepository
//                        .findAllByUserIdAndEntityInAndGenericIdInAndDateBetweenOrderByDateDesc
//                        (userId,search,deviceStringList,fromDate,toDate,new PageRequest(size,10));
//                result.addAll(optionalUserAuditLogger.getContent());
//                resultSize += optionalUserAuditLogger.getTotalElements();
//            }if (!userAuditRequest.getDriverIds().isEmpty()) {
//                drivesIds=userAuditRequest.getDriverIds()
//                        .stream()
//                        .map(Functions.toStringFunction()::apply)
//                        .collect(Collectors.toList());
//                List<String> driverStringList = new ArrayList<>();
//                for (String string:drivesIds) {
//                    driverStringList.add(string+"-DriverSFDA");
//                }
//                optionalUserAuditLogger = userAuditLoggerRepository
//                        .findAllByUserIdAndEntityInAndGenericIdInAndDateBetweenOrderByDateDesc
//                                (userId,search,driverStringList,fromDate,toDate,new PageRequest(size,10));
//                result.addAll(optionalUserAuditLogger.getContent());
//                resultSize += optionalUserAuditLogger.getTotalElements();
//            }if (!userAuditRequest.getInventoryIds().isEmpty()) {
//                invIds=userAuditRequest.getInventoryIds().stream()
//                        .map(Functions.toStringFunction()::apply)
//                        .collect(Collectors.toList());
//                List<String> invStringList = new ArrayList<>();
//                for (String string:invIds) {
//                    invStringList.add(string+"-Inventory");
//                }
//                optionalUserAuditLogger = userAuditLoggerRepository
//                        .findAllByUserIdAndEntityInAndGenericIdInAndDateBetweenOrderByDateDesc
//                                (userId,invIds,invStringList,fromDate,toDate,new PageRequest(size,10));
//                result.addAll(optionalUserAuditLogger.getContent());
//                resultSize += optionalUserAuditLogger.getTotalElements();
//            }if (!userAuditRequest.getWareHouseIds().isEmpty()) {
//                wareIds=userAuditRequest.getWareHouseIds().stream()
//                        .map(Functions.toStringFunction()::apply)
//                        .collect(Collectors.toList());
//                List<String> wareStringList = new ArrayList<>();
//                for (String string:wareIds) {
//                    wareStringList.add(string+"-Warehouse");
//                }
//                optionalUserAuditLogger = userAuditLoggerRepository
//                        .findAllByUserIdAndEntityInAndGenericIdInAndDateBetweenOrderByDateDesc
//                                (userId,search,wareStringList,fromDate,toDate,new PageRequest(size,10));
//                result.addAll(optionalUserAuditLogger.getContent());
//                resultSize += optionalUserAuditLogger.getTotalElements();
//            }
        }
        if (!result.isEmpty()) {
            for (UserAuditLogger userAuditLogger:result) {
                Date dateTime = new Date();Date dateOutTime = new Date(); String formatDate;
                String formatOutDate;Date dateFinalTime = new Date();
                SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+SSSS");
                SimpleDateFormat outputFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");
                String sDate;
                try {
                    formatDate = inputFormat1.format(userAuditLogger.getDate());
                    dateTime = inputFormat1.parse(formatDate);
                    formatOutDate = outputFormat.format(userAuditLogger.getDate());
                    dateOutTime = outputFormat.parse(formatOutDate);
                    sDate = utilities.timeZoneConverter(dateTime,timeOffset);
                    dateFinalTime = outputFormat1.parse(sDate);
                }catch (ParseException e){
                    e.printStackTrace();
                }
                if (dateTime !=null){
                    userAuditLogger.setDate(dateFinalTime);
                }
                else {
                    userAuditLogger.setDate(dateOutTime);
                }
            }
            builder.setSuccess(true);
            builder.setStatusCode(200);
            builder.setEntity(result);
            builder.setSize(Math.toIntExact(resultSize));
            builder.setMessage("Success");
            log.info("************** Get User Logs ENDED**************");
            return builder.build();
        }else {
            builder.setSuccess(true);
            builder.setStatusCode(200);
            builder.setEntity(result);
            builder.setSize(0);
            builder.setMessage("NO DATA FOUND");
            log.info("************** Get User Logs ENDED**************");
            return builder.build();
        }
    }

}
