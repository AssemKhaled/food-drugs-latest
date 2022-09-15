package com.example.food_drugs.helpers.Impl;

import com.example.examplequerydslspringdatajpamaven.entity.CustomMapData;
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository;
import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class LiveDataMapping {
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<CustomMapData> getList(List<String> positionIds,List<Device> deviceList ,String status){

        List<CustomMapData> positions = new ArrayList<>();

        List<ObjectId> ids = new ArrayList<>();

        for(String id:positionIds) {
            if(id != null) {
                ids.add(new ObjectId(id));
            }
        }

        Aggregation aggregation = newAggregation(
                match(Criteria.where("_id").in(ids)),
                project("deviceid","deviceName","devicetime",
                        "valid","attributes.ignition","attributes.power",
                        "attributes.operator","latitude","longitude","speed","address").and("devicetime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("devicetime"),
                sort(Sort.Direction.DESC, "devicetime")


        ).withOptions(newAggregationOptions().allowDiskUse(true).build());




        AggregationResults<BasicDBObject> groupResults
                = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);


        if(groupResults.getMappedResults().size() > 0) {

            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
            while (iterator.hasNext()) {
                BasicDBObject object = (BasicDBObject) iterator.next();
                CustomMapData position = new CustomMapData();

                if(object.containsField("deviceid") && object.get("deviceid") != null) {
                    position.setId(object.getLong("deviceid"));
//                    Long deviceId= deviceList.stream().filter(device -> device.getId().equals(position.getId())).findFirst().get().getId();
//                    Double lastTemp = deviceList.stream().filter(device -> device.getId().equals(position.getId())).findFirst().get().getLastTemp();
//                    Double lasthum = deviceList.stream().filter(device -> device.getId().equals(position.getId())).findFirst().get().getLastHum();
                    Double roundTemp = null;
                    Double roundHum = null;
                    String uniqueId = null;
                    String lastUpdate = null;
                    Double lastTemp ;
                    Double lastHum ;
                    try{
                        Device res;
                        List<Device> data= deviceList.stream()
                                .filter(device -> device.getId().equals(position.getId()))
                                .collect(Collectors.toList());
                        if (!data.isEmpty()) {
                            res = data.get(0);
                             uniqueId = res.getUniqueid();
                             lastUpdate = res.getLastupdate();
                             lastTemp = res.getLastTemp();
                             lastHum = res.getLastHum();
                            roundTemp = Math.round(lastTemp * 100.0) / 100.0;
                            roundHum = Math.round(lastHum * 100.0) / 100.0;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                        position.setTemperature(roundTemp);
                        position.setHumidity(roundHum);
                        position.setUniqueid(uniqueId);
                        position.setLastUpdate(lastUpdate);

//                    Device device = deviceRepository.findOne(position.getId());
//                    if(device != null) {
//
//                        Double roundTemp = 0.0;
//                        Double roundHum = 0.0;
//
//                        roundTemp = Math.round(device.getLastTemp() * 100.0) / 100.0;
//                        roundHum = Math.round(device.getLastHum() * 100.0) / 100.0;
//
//                        position.setTemperature(roundTemp);
//                        position.setHumidity(roundHum);
//
//                    }
                }

                if(object.containsField("address") && object.get("address") != null) {
                    position.setAddress(object.getString("address"));

                }

                if(object.containsField("deviceName") && object.get("deviceName") != null) {
                    position.setDeviceName(object.getString("deviceName"));

                }
                if(object.containsField("devicetime") && object.get("devicetime") != null) {

                    position.setLastUpdateApp(object.getString("devicetime"));

                    Date dateTime = null;
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

                    try {
                        dateTime = inputFormat.parse(object.getString("devicetime"));

                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    Calendar calendarTime = Calendar.getInstance();
                    calendarTime.setTime(dateTime);
                    calendarTime.add(Calendar.HOUR_OF_DAY, 3);
                    dateTime = calendarTime.getTime();


                    position.setDeviceTime(outputFormat.format(dateTime));

                }
                if(object.containsField("_id") && object.get("_id") != null) {
                    position.setPositionId(object.getObjectId("_id").toString());

                }

                if (status == "offline"){
                    position.setStatus(4);
                    position.setVehicleStatus(3);
                    if(object.containsField("speed") && object.get("speed") != null) {
                        position.setSpeed(object.getDouble("speed") * (1.852) );
                    }
                    if(object.containsField("ignition")  && object.get("ignition") != null) {
                        if(object.getBoolean("ignition") == true) {
                            position.setIgnition(1);
                        }
                        else {
                            position.setIgnition(0);
                        }
                    }
                }
                else if(status == "outOfNetwork") {
                    position.setStatus(6);
                    position.setVehicleStatus(2);
                    if(object.containsField("speed") && object.get("speed") != null) {
                        position.setSpeed(object.getDouble("speed") * (1.852) );
                    }
                    if(object.containsField("ignition") && object.get("ignition") != null) {
                        if(object.getBoolean("ignition") == true) {
                            position.setIgnition(1);

                        }
                        else {
                            position.setIgnition(0);

                        }

                    }
                }
                else if(status == "online"){
                    position.setVehicleStatus(1);
                    if(object.containsField("speed") && object.get("speed") != null) {
                        position.setSpeed(object.getDouble("speed") * (1.852) );
                        if(object.getDouble("speed") >0 ) {
                            position.setStatus(2);
                        }
                        if(object.getDouble("speed") == 0 ) {
                            position.setStatus(1);
                        }
                    }

                    if(object.containsField("ignition") && object.get("ignition") != null) {
                        if(object.getBoolean("ignition") == true) {
                            position.setIgnition(1);

                            if(object.getDouble("speed") >0) {
                                position.setStatus(2);
                            }
                            if(object.getDouble("speed") == 0) {
                                position.setStatus(1);
                            }

                        }
                        else {
                            position.setIgnition(0);
                            position.setStatus(3);


                        }

                    }
                }

                if(object.containsField("valid") && object.get("valid") != null) {

                    if(object.getBoolean("valid") == true) {
                        position.setValid(1);
                    }
                    else {
                        position.setValid(0);
                    }
                }

                if(object.containsField("power") && object.get("power") != null) {
                    position.setPower(object.getDouble("power"));
                }

                if(object.containsField("operator") && object.get("operator") != null) {
                    position.setOperator(object.getDouble("operator"));
                }

                if(object.containsField("latitude") && object.get("latitude") != null) {
                    position.setLatitude(object.getDouble("latitude"));
                }

                if(object.containsField("longitude") && object.get("longitude") != null) {
                    position.setLongitude(object.getDouble("longitude"));
                }

                positions.add(position);
            }
        }

        return positions;
    }
}
