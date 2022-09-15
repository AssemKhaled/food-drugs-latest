package com.example.food_drugs.service.mobile.Impl;

import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;
import com.example.food_drugs.repository.MongoInventoryLastDataRepository;
import com.example.food_drugs.repository.WarehousesRepository;
import com.example.food_drugs.dto.responses.InventoryWarehouseDataByUserIdsDataWrapper;
import com.example.food_drugs.dto.responses.WarehouseWrapperGraphData;
import com.example.food_drugs.service.impl.WarehouseServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DashboardService extends RestServiceController implements com.example.food_drugs.service.mobile.DashboardService {

    private static final Log logger = LogFactory.getLog(WarehouseServiceImpl.class);

    private GetObjectResponse getObjectResponse;

    private final UserServiceImpl userServiceImpl;

    private final WarehousesRepository warehousesRepository;

    private final MongoInventoryLastDataRepository mongoInventoryLastDataRepository;

    public DashboardService(UserServiceImpl userServiceImpl, WarehousesRepository warehousesRepository,
                            MongoInventoryLastDataRepository mongoInventoryLastDataRepository) {
        this.userServiceImpl = userServiceImpl;
        this.warehousesRepository = warehousesRepository;
        this.mongoInventoryLastDataRepository = mongoInventoryLastDataRepository;
    }

    @Override
    public ResponseEntity<?> getListWarehousesInventories(String TOKEN, Long userId, int offset) {
        logger.info("************************ getListWarehousesInventories STARTED ***************************");
        List<Object[]> listOfWarehousesAndInventoriesData = new ArrayList<>();

        if(TOKEN.equals("")) {
            getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",listOfWarehousesAndInventoriesData);
            return  ResponseEntity.badRequest().body(getObjectResponse);
        }

        if(super.checkActive(TOKEN)!= null) {
            return super.checkActive(TOKEN);
        }

        if(userId == 0) {
            getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",listOfWarehousesAndInventoriesData);
            return  ResponseEntity.badRequest().body(getObjectResponse);
        }

        User loggedUser = userServiceImpl.findById(userId);

        if(loggedUser == null ) {
            getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",listOfWarehousesAndInventoriesData);
            return  ResponseEntity.status(404).body(getObjectResponse);
        }
        if(loggedUser.getDeleteDate() != null){
            getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(),
                    "This User Was Deleted " ,listOfWarehousesAndInventoriesData);
            return  ResponseEntity.status(404).body(getObjectResponse);
        }

        List<Long>usersIds= new ArrayList<>();

        userServiceImpl.resetChildernArray();
        if(loggedUser.getAccountType().equals(4)) {
            usersIds.add(userId);
//            listOfWarehousesAndInventoriesData = warehousesRepository.getInventoryWarehouseDataByUserIdsOffset(usersIds, offset);
            listOfWarehousesAndInventoriesData = warehousesRepository.getInventoryWarehouseDataByUserIds(usersIds);
        }
        else {
            List<User>childernUsers = userServiceImpl.getActiveAndInactiveChildern(userId);
            if(childernUsers.isEmpty()) {
                usersIds.add(userId);
            }
            else {
                usersIds.add(userId);
                for(User object : childernUsers) {
                    usersIds.add(object.getId());
                }
            }
//            listOfWarehousesAndInventoriesData = warehousesRepository.getInventoryWarehouseDataByUserIdsOffset(usersIds, offset);
            listOfWarehousesAndInventoriesData = warehousesRepository.getInventoryWarehouseDataByUserIds(usersIds);
        }

        List<InventoryWarehouseDataByUserIdsDataWrapper> mappedSQLData = new ArrayList<>();

        for( Object[] data : listOfWarehousesAndInventoriesData){

            mappedSQLData.add(InventoryWarehouseDataByUserIdsDataWrapper
                    .builder()
                    .userId((Integer) data[0])
                    .inventoryId((Integer) data[1])
                    .wareHouseName((String) data[2])
                    .inventoryName((String) data[3])
                    .storingCategory((String) data[4])
                    .lastUpdate((String) data[5])
                    .lastDataId((String) data[6])
                    .warehouseId((Integer) data[7])
                    .lastData(mongoInventoryLastDataRepository.findById((String) data[6]))
                    .build());
        }

        List<WarehouseWrapperGraphData> warehouseWrapperGraphData = new ArrayList<>();
        List<Object[]> warehouses = warehousesRepository.getWarehouseForUserOffset(usersIds, offset);
        for (Object[] warehouseId : warehouses){
            if(!warehouseId[1].equals("")){
                List<InventoryWarehouseDataByUserIdsDataWrapper> inventories = mappedSQLData
                        .stream().filter(c -> Objects.equals(c.getWarehouseId(), (Integer) warehouseId[0]))
                        .collect(Collectors.toList());
                warehouseWrapperGraphData.add(
                        WarehouseWrapperGraphData.builder()
                                .inventories(inventories)
                                .warehouseName((String) warehouseId[1])
                                .build());
            }
        }

        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",warehouseWrapperGraphData, warehouseWrapperGraphData.size());
        logger.info("************************ getListWarehousesInventories ENDED ***************************");
        return  ResponseEntity.ok().body(getObjectResponse);
    }
}
