package com.example.food_drugs.rest.mobileRestSFDA;

import com.example.food_drugs.dto.responses.MongoInventoryWrapper;
import com.example.food_drugs.dto.responses.ResponseWrapper;
import com.example.food_drugs.dto.responses.mobile.DeviceMonitoringResponse;
import com.example.food_drugs.dto.responses.mobile.InventoryDataResponse;
import com.example.food_drugs.dto.responses.mobile.MonitoringDevicePositionResponse;
import com.example.food_drugs.service.mobile.Impl.MonitoringServiceImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@Component
@RestController
@RequestMapping(path = "/mobile/monitoring")
public class MobileMonitoringControllerSFDA {
    private final MonitoringServiceImpl monitoringService;
    public MobileMonitoringControllerSFDA(MonitoringServiceImpl monitoringService) {
        this.monitoringService = monitoringService;
    }

    @GetMapping(path = "/devices/list")
    public ResponseWrapper<List<DeviceMonitoringResponse>> listDevices(@RequestHeader(value = "TOKEN") String TOKEN ,
                                                                       @RequestParam(value = "userId") Long userId,
                                                                       @RequestParam(value = "offset") int offset,
                                                                       @RequestParam(value = "size") int size ,
                                                                       @RequestParam(value = "search",defaultValue = "") String search){
        return monitoringService.monitoringDeviceList(TOKEN ,userId , offset , size ,search);
    }

    @GetMapping(path = "/inventories/list")
    public ResponseWrapper<List<MongoInventoryWrapper>> listInventoriesLastInfo(@RequestHeader(value = "TOKEN") String TOKEN , @RequestParam(value = "userId") Long userId, @RequestParam(value = "offset") int offset, @RequestParam(value = "search") String search){
        return monitoringService.monitoringGetAllInventoriesLastInfo(TOKEN ,userId , offset ,search);
    }

    @GetMapping(path = "/devices/position")
    public ResponseWrapper<MonitoringDevicePositionResponse> getDevicePosition(@RequestHeader(value = "TOKEN") String TOKEN ,
                                                                               @RequestParam(value = "deviceId") Long deviceId){
        return monitoringService.monitoringGetDevicePosition(TOKEN ,deviceId);
    }
    @GetMapping(path = "/inventories/details")
    public ResponseWrapper<InventoryDataResponse>getDetailsInventory(@RequestHeader(value = "TOKEN") String TOKEN ,
                                                                     @RequestParam(value = "inventoryId") Long inventoryId){
        return monitoringService.monitoringGetDetailsInventory(TOKEN,inventoryId);
    }

}
