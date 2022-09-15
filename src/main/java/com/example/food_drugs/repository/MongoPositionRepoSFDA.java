package com.example.food_drugs.repository;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.count;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregationOptions;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;


import com.example.food_drugs.dto.DeviceTempHum;
import com.example.food_drugs.entity.MonitorStaticstics;
import com.example.food_drugs.entity.Position;
import com.example.food_drugs.entity.Series;
import com.mongodb.BasicDBObject;

@Repository
public class MongoPositionRepoSFDA {

	@Autowired
	MongoTemplate mongoTemplate;

	public List<MonitorStaticstics> getVehicleTempHumListDigram(List<Long> allDevices,Date start,Date end){

		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
		
		List<Series> seriesTemp = new  ArrayList<Series>();
//		List<Series> seriesHum = new  ArrayList<Series>();
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)),
	            project("deviceid","attributes","speed","deviceName","weight").and("devicetime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("devicetime"),
	            sort(Sort.Direction.DESC, "devicetime")
	            
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());

	    
	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);

	        
            if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();


    	            Series objTemp = new Series(); 
    	            Series objHum = new Series(); 

	            	
	            	if(object.containsField("attributes") && object.get("attributes").toString() != null) {
	
                       JSONObject obj = new JSONObject(object.get("attributes").toString());
	                	
                       Integer countTemp = 0;
	                   Integer countHum = 0;
	                   	
	                   	Double Temp = (double) 0;
	                   	Double Hum = (double) 0;
	                   	

                    	if(obj.has("temp1")) {
                    		if(obj.getDouble("temp1") != 0) {
	                    		Temp = Temp + obj.getDouble("temp1");
	                    		countTemp = countTemp + 1;
                    		}
						}
                    	if(obj.has("temp2")) {
                    		if(obj.getDouble("temp2") != 0) {
	                    		Temp = Temp + obj.getDouble("temp2");
	                    		countTemp = countTemp + 1;
                    		}
						}
                    	if(obj.has("temp3")) {
                    		if(obj.getDouble("temp3") != 0) {
	                    		Temp = Temp + obj.getDouble("temp3");
	                    		countTemp = countTemp + 1;
                    		}
						}
                    	if(obj.has("temp4")) {
                    		if(obj.getDouble("temp4") != 0) {
                    			Temp = Temp + obj.getDouble("temp4");
	                    		countTemp = countTemp + 1;
                    		}
						}
                    	
                    	if(obj.has("hum1")) {
                    		if(obj.getDouble("hum1") != 0) {
	                    		Hum = Hum + obj.getDouble("hum1");
	                    		countHum = countHum + 1;
                    		}

						}
                    	if(obj.has("hum2")) {
                    		if(obj.getDouble("hum2") != 0) {
	                    		Hum = Hum + obj.getDouble("hum2");
	                    		countHum = countHum + 1;
                    		}

						}
                    	if(obj.has("hum3")) {
                    		if(obj.getDouble("hum3") != 0) {
	                    		Hum = Hum + obj.getDouble("hum3");
	                    		countHum = countHum + 1;
                    		}

						}
                    	if(obj.has("hum4")) {
                    		if(obj.getDouble("hum4") != 0) {
	                    		Hum = Hum + obj.getDouble("hum4");
	                    		countHum = countHum + 1;
                    		}

						}
                    	
                    	if(obj.has("wiretemp1")) {
                    		if(obj.getDouble("wiretemp1") != 0) {
	                    		Temp = Temp + obj.getDouble("wiretemp1");
	                    		countTemp = countTemp + 1;
                    		}
						}
                    	if(obj.has("wiretemp2")) {
                    		if(obj.getDouble("wiretemp2") != 0) {
	                    		Temp = Temp + obj.getDouble("wiretemp2");
	                    		countTemp = countTemp + 1;
                    		}

						}
                    	if(obj.has("wiretemp3")) {
                    		if(obj.getDouble("wiretemp3") != 0) {
	                    		Temp = Temp + obj.getDouble("wiretemp3");
	                    		countTemp = countTemp + 1;
                    		}

						}
                    	if(obj.has("wiretemp4")) {
                    		if(obj.getDouble("wiretemp4") != 0) {
	                    		Temp = Temp + obj.getDouble("wiretemp4");
	                    		countTemp = countTemp + 1;
                    		}

						}
                    	
                    	if(obj.has("wirehum1")) {
                    		if(obj.getDouble("wirehum1") != 0) {
	                    		Hum = Hum + obj.getDouble("wirehum1");
	                    		countHum = countHum + 1;
                    		}
						}
                    	if(obj.has("wirehum2")) {
                    		if(obj.getDouble("wirehum2") != 0) {
	                    		Hum = Hum + obj.getDouble("wirehum2");
	                    		countHum = countHum + 1;
                    		}

						}
                    	if(obj.has("wirehum3")) {
                    		if(obj.getDouble("wirehum3") != 0) {
	                    		Hum = Hum + obj.getDouble("wirehum3");
	                    		countHum = countHum + 1;
                    		}

						}
                    	if(obj.has("wirehum4")) {
                    		if(obj.getDouble("wirehum4") != 0) {
	                    		Hum = Hum + obj.getDouble("wirehum4");
	                    		countHum = countHum + 1;
                    		}

						}
                    	
	                   	Double avgTemp = (double) 0;
	                   	Double avgHum = (double) 0;
	                   	if(countTemp != 0) {
		                    	avgTemp = Temp / countTemp;
	
	                   	}
	                   	if(countHum != 0) {
	                   		avgHum = Hum / countHum;
	
	                   	}
	                   	avgHum = Math.round(avgHum * 100.0) / 100.0;
                    	avgTemp = Math.round(avgTemp * 100.0) / 100.0;
                    	
        	            objTemp.setValue(avgTemp);
//        	            objHum.setValue(avgHum);

	            	}
	            	
					if(object.containsField("devicetime") && object.get("devicetime") != null) {
						
						Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss aa");

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

						
	    	            objTemp.setName(outputFormat.format(dateTime));
	    	            objHum.setName(outputFormat.format(dateTime));
						
						
	                }
					
					
					seriesTemp.add(objTemp);
//					seriesHum.add(objHum);
	            }
	        }
    	MonitorStaticstics dataTemp = new MonitorStaticstics();
    	dataTemp.setName("Temperature");
    	dataTemp.setSeries(seriesTemp);
    	
//    	MonitorStaticstics dataHum = new MonitorStaticstics();
//    	dataHum.setName("Humidity");
//    	dataHum.setSeries(seriesHum);
    	
		List<MonitorStaticstics> sensors = new ArrayList<MonitorStaticstics>();
		sensors.add(dataTemp);
//		sensors.add(dataHum);

		
		
		return sensors;
	}
	
	public List<DeviceTempHum> getVehicleTempHumListScheduled(List<Long> allDevices,Date start,Date end){

		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
		
		List<DeviceTempHum> positions = new ArrayList<DeviceTempHum>();

						
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)),
	            project("deviceid","attributes","speed","deviceName","weight").and("devicetime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("devicetime"),
	            sort(Sort.Direction.DESC, "devicetime")
	            
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());

	    
	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);

	        
            if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	DeviceTempHum device = new DeviceTempHum();
	            	
	            	if(object.containsField("attributes") && object.get("attributes").toString() != null) {
	                	device.setAttributes(object.get("attributes").toString());
	
                       JSONObject obj = new JSONObject(device.getAttributes().toString());
	                	
                       Integer countTemp = 0;
	                   Integer countHum = 0;
	                   	
	                   	Double Temp = (double) 0;
	                   	Double Hum = (double) 0;
	                   	

                    	if(obj.has("temp1")) {
                    		if(obj.getDouble("temp1") != 0) {
	                    		Temp = Temp + obj.getDouble("temp1");
	                    		countTemp = countTemp + 1;
                    		}
						}
                    	if(obj.has("temp2")) {
                    		if(obj.getDouble("temp2") != 0) {
	                    		Temp = Temp + obj.getDouble("temp2");
	                    		countTemp = countTemp + 1;
                    		}
						}
                    	if(obj.has("temp3")) {
                    		if(obj.getDouble("temp3") != 0) {
	                    		Temp = Temp + obj.getDouble("temp3");
	                    		countTemp = countTemp + 1;
                    		}
						}
                    	if(obj.has("temp4")) {
                    		if(obj.getDouble("temp4") != 0) {
                    			Temp = Temp + obj.getDouble("temp4");
	                    		countTemp = countTemp + 1;
                    		}
						}
                    	
                    	if(obj.has("hum1")) {
                    		if(obj.getDouble("hum1") != 0) {
	                    		Hum = Hum + obj.getDouble("hum1");
	                    		countHum = countHum + 1;
                    		}

						}
                    	if(obj.has("hum2")) {
                    		if(obj.getDouble("hum2") != 0) {
	                    		Hum = Hum + obj.getDouble("hum2");
	                    		countHum = countHum + 1;
                    		}

						}
                    	if(obj.has("hum3")) {
                    		if(obj.getDouble("hum3") != 0) {
	                    		Hum = Hum + obj.getDouble("hum3");
	                    		countHum = countHum + 1;
                    		}

						}
                    	if(obj.has("hum4")) {
                    		if(obj.getDouble("hum4") != 0) {
	                    		Hum = Hum + obj.getDouble("hum4");
	                    		countHum = countHum + 1;
                    		}

						}
                    	
                    	if(obj.has("wiretemp1")) {
                    		if(obj.getDouble("wiretemp1") != 0) {
	                    		Temp = Temp + obj.getDouble("wiretemp1");
	                    		countTemp = countTemp + 1;
                    		}
						}
                    	if(obj.has("wiretemp2")) {
                    		if(obj.getDouble("wiretemp2") != 0) {
	                    		Temp = Temp + obj.getDouble("wiretemp2");
	                    		countTemp = countTemp + 1;
                    		}

						}
                    	if(obj.has("wiretemp3")) {
                    		if(obj.getDouble("wiretemp3") != 0) {
	                    		Temp = Temp + obj.getDouble("wiretemp3");
	                    		countTemp = countTemp + 1;
                    		}

						}
                    	if(obj.has("wiretemp4")) {
                    		if(obj.getDouble("wiretemp4") != 0) {
	                    		Temp = Temp + obj.getDouble("wiretemp4");
	                    		countTemp = countTemp + 1;
                    		}

						}
                    	
                    	if(obj.has("wirehum1")) {
                    		if(obj.getDouble("wirehum1") != 0) {
	                    		Hum = Hum + obj.getDouble("wirehum1");
	                    		countHum = countHum + 1;
                    		}
						}
                    	if(obj.has("wirehum2")) {
                    		if(obj.getDouble("wirehum2") != 0) {
	                    		Hum = Hum + obj.getDouble("wirehum2");
	                    		countHum = countHum + 1;
                    		}

						}
                    	if(obj.has("wirehum3")) {
                    		if(obj.getDouble("wirehum3") != 0) {
	                    		Hum = Hum + obj.getDouble("wirehum3");
	                    		countHum = countHum + 1;
                    		}

						}
                    	if(obj.has("wirehum4")) {
                    		if(obj.getDouble("wirehum4") != 0) {
	                    		Hum = Hum + obj.getDouble("wirehum4");
	                    		countHum = countHum + 1;
                    		}

						}
                    	
	                   	Double avgTemp = (double) 0;
	                   	Double avgHum = (double) 0;
	                   	if(countTemp != 0) {
		                    	avgTemp = Temp / countTemp;
	
	                   	}
	                   	if(countHum != 0) {
	                   		avgHum = Hum / countHum;
	
	                   	}
	                   	avgHum = Math.round(avgHum * 100.0) / 100.0;
                    	        avgTemp = Math.round(avgTemp * 100.0) / 100.0;
	                   	device.setTemperature(avgTemp);
	                   	device.setHumidity(avgHum);
	            	}
	            	if(object.containsField("deviceid") && object.get("deviceid") != null) {
	                	device.setDeviceId(object.getLong("deviceid"));
	
	            	}
	            	if(object.containsField("speed") && object.get("speed") != null) {
		            	device.setSpeed(object.getDouble("speed") * (1.852) );    		
	                }
					if(object.containsField("devicetime") && object.get("devicetime") != null) {
						
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

						
						device.setServertime(outputFormat.format(dateTime));
						
						
	                }
					
					if(object.containsField("_id") && object.get("_id") != null) {
						device.setId(object.getObjectId("_id").toString());

					}
					if(object.containsField("deviceName") && object.get("deviceName") != null) {
		            	device.setDeviceName(object.getString("deviceName"));    		
	                }
					if(object.containsField("weight") && object.get("weight") != null) {
		            	device.setWeight(object.getDouble("weight"));    		
	                }
					
	            	
	            	
					positions.add(device);
	            }
	        }
        
		return positions;
	}
	
	public List<DeviceTempHum> getVehicleTempHumList(List<Long> allDevices,int offset,Date start,Date end){

		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
//		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
//		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
		List<DeviceTempHum> positions = new ArrayList<DeviceTempHum>();

				
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)),
	            project("deviceid","attributes","speed","deviceName","weight").and("devicetime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("devicetime"),
	            sort(Sort.Direction.DESC, "devicetime"),
	            skip(offset),
	            limit(10)
	            
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());

	    
	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);

	        
            if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	DeviceTempHum device = new DeviceTempHum();
	            	
	            	
	            	
	            	if(object.containsField("attributes") && object.get("attributes") != null) {
	                	device.setAttributes(object.get("attributes").toString());
	
	                	JSONObject obj = new JSONObject(device.getAttributes().toString());
	                	
	                       Integer countTemp = 0;
		                   Integer countHum = 0;
		                   	
		                   	Double Temp = (double) 0;
		                   	Double Hum = (double) 0;

	                    	if(obj.has("temp1")) {
	                    		if(obj.getDouble("temp1") != 0) {
		                    		Temp = Temp + obj.getDouble("temp1");
		                    		countTemp = countTemp + 1;
	                    		}
							}
	                    	if(obj.has("temp2")) {
	                    		if(obj.getDouble("temp2") != 0) {
		                    		Temp = Temp + obj.getDouble("temp2");
		                    		countTemp = countTemp + 1;
	                    		}
							}
	                    	if(obj.has("temp3")) {
	                    		if(obj.getDouble("temp3") != 0) {
		                    		Temp = Temp + obj.getDouble("temp3");
		                    		countTemp = countTemp + 1;
	                    		}
							}
	                    	if(obj.has("temp4")) {
	                    		if(obj.getDouble("temp4") != 0) {
	                    			Temp = Temp + obj.getDouble("temp4");
		                    		countTemp = countTemp + 1;
	                    		}
							}
	                    	
	                    	if(obj.has("hum1")) {
	                    		if(obj.getDouble("hum1") != 0) {
		                    		Hum = Hum + obj.getDouble("hum1");
		                    		countHum = countHum + 1;
	                    		}

							}
	                    	if(obj.has("hum2")) {
	                    		if(obj.getDouble("hum2") != 0) {
		                    		Hum = Hum + obj.getDouble("hum2");
		                    		countHum = countHum + 1;
	                    		}
	
							}
	                    	if(obj.has("hum3")) {
	                    		if(obj.getDouble("hum3") != 0) {
		                    		Hum = Hum + obj.getDouble("hum3");
		                    		countHum = countHum + 1;
	                    		}
	
							}
	                    	if(obj.has("hum4")) {
	                    		if(obj.getDouble("hum4") != 0) {
		                    		Hum = Hum + obj.getDouble("hum4");
		                    		countHum = countHum + 1;
	                    		}
	
							}
	                    	
	                    	if(obj.has("wiretemp1")) {
	                    		if(obj.getDouble("wiretemp1") != 0) {
		                    		Temp = Temp + obj.getDouble("wiretemp1");
		                    		countTemp = countTemp + 1;
	                    		}
							}
	                    	if(obj.has("wiretemp2")) {
	                    		if(obj.getDouble("wiretemp2") != 0) {
		                    		Temp = Temp + obj.getDouble("wiretemp2");
		                    		countTemp = countTemp + 1;
	                    		}
	
							}
	                    	if(obj.has("wiretemp3")) {
	                    		if(obj.getDouble("wiretemp3") != 0) {
		                    		Temp = Temp + obj.getDouble("wiretemp3");
		                    		countTemp = countTemp + 1;
	                    		}
	
							}
	                    	if(obj.has("wiretemp4")) {
	                    		if(obj.getDouble("wiretemp4") != 0) {
		                    		Temp = Temp + obj.getDouble("wiretemp4");
		                    		countTemp = countTemp + 1;
	                    		}
	
							}
	                    	
	                    	if(obj.has("wirehum1")) {
	                    		if(obj.getDouble("wirehum1") != 0) {
		                    		Hum = Hum + obj.getDouble("wirehum1");
		                    		countHum = countHum + 1;
	                    		}
							}
	                    	if(obj.has("wirehum2")) {
	                    		if(obj.getDouble("wirehum2") != 0) {
		                    		Hum = Hum + obj.getDouble("wirehum2");
		                    		countHum = countHum + 1;
	                    		}
	
							}
	                    	if(obj.has("wirehum3")) {
	                    		if(obj.getDouble("wirehum3") != 0) {
		                    		Hum = Hum + obj.getDouble("wirehum3");
		                    		countHum = countHum + 1;
	                    		}
	
							}
	                    	if(obj.has("wirehum4")) {
	                    		if(obj.getDouble("wirehum4") != 0) {
		                    		Hum = Hum + obj.getDouble("wirehum4");
		                    		countHum = countHum + 1;
	                    		}
	
							}
	                    	
		                   	Double avgTemp = (double) 0;
		                   	Double avgHum = (double) 0;
		                   	if(countTemp != 0) {
			                    	avgTemp = Temp / countTemp;
		
		                   	}
		                   	if(countHum != 0) {
		                   		avgHum = Hum / countHum;
		
		                   	}
		                   	
	                   	        avgHum = Math.round(avgHum * 100.0) / 100.0;
                    	                avgTemp = Math.round(avgTemp * 100.0) / 100.0;
		                   	device.setTemperature(avgTemp);
		                   	device.setHumidity(avgHum);
	            	}
	            	if(object.containsField("deviceid") && object.get("deviceid") != null) {
	                	device.setDeviceId(object.getLong("deviceid"));
	
	            	}
	            	if(object.containsField("speed") && object.get("speed") != null) {
		            	device.setSpeed(object.getDouble("speed") * (1.852) );
						device.setSpeed(Math.round(device.getSpeed()*100.0)/100.0);
	                }
					if(object.containsField("devicetime") && object.get("devicetime") != null) {
						
						Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss ");

						try {
							dateTime = inputFormat.parse(object.getString("devicetime"));

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 2);
						dateTime = calendarTime.getTime();

						
						device.setServertime(outputFormat.format(dateTime));
						
						
	                }
					if(object.containsField("_id") && object.get("_id") != null) {
						device.setId(object.getObjectId("_id").toString());

					}
					if(object.containsField("deviceName") && object.get("deviceName") != null) {
		            	device.setDeviceName(object.getString("deviceName"));    		
	                }
					if(object.containsField("weight") && object.get("weight") != null) {
		            	device.setWeight(object.getDouble("weight"));    		
	                }
	            	
					
					positions.add(device);
	            }
	        }
        
		return positions;
	}
	
	public Integer getVehicleTempHumListSize(List<Long> allDevices,Date start,Date end){

		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
//		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
//		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
		Integer size = 0;

		
		
	    Aggregation aggregation = newAggregation(
	    		match(Criteria.where("deviceid").in(allDevices).and("devicetime").gte(start).lte(end)),
	            project("deviceid","attributes","speed","deviceName","weight").and("devicetime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("devicetime"),
	            sort(Sort.Direction.DESC, "devicetime"),
	            count().as("size")
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());


	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);


	        if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	size = object.getInt("size");
	            }

	        }
		return size;
	}
	
	

	public List<DeviceTempHum> getTripPositionsDetails(Long deviceId,Date start,Date end,int offset){

		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
//		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
//		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
		
        List<DeviceTempHum> positions = new ArrayList<DeviceTempHum>();
				
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(deviceId).and("devicetime").gte(start).lte(end)),
	            project("deviceid","latitude","longitude","deviceName","driverName","driverid","attributes","speed","weight","address")
	            .and("devicetime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("devicetime")
	            .and("servertime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("servertime")
	            .and("fixtime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("fixtime"),
	            sort(Sort.Direction.DESC, "devicetime"),
	            skip(offset),
	            limit(10)
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());

	    
	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);


            if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	
	            	DeviceTempHum device = new DeviceTempHum();
	            	
	            	

	            	if(object.containsField("latitude") && object.get("latitude") != null) {
	            		
	            		device.setLatitude(object.getDouble("latitude"));

	            	}
                    if(object.containsField("longitude") && object.get("longitude") != null) {
	            		
                    	device.setLongitude(object.getDouble("longitude"));

	            	}
	            	
	            	if(object.containsField("attributes") && object.get("attributes") != null) {
	                	device.setAttributes(object.get("attributes").toString());
	
	                	JSONObject obj = new JSONObject(device.getAttributes().toString());
	                	
	                       Integer countTemp = 0;
		                   Integer countHum = 0;
		                   	
		                   	Double Temp = (double) 0;
		                   	Double Hum = (double) 0;

	                    	if(obj.has("temp1")) {
	                    		if(obj.getDouble("temp1") != 0) {
		                    		Temp = Temp + obj.getDouble("temp1");
		                    		countTemp = countTemp + 1;
	                    		}
							}
	                    	if(obj.has("temp2")) {
	                    		if(obj.getDouble("temp2") != 0) {
		                    		Temp = Temp + obj.getDouble("temp2");
		                    		countTemp = countTemp + 1;
	                    		}
							}
	                    	if(obj.has("temp3")) {
	                    		if(obj.getDouble("temp3") != 0) {
		                    		Temp = Temp + obj.getDouble("temp3");
		                    		countTemp = countTemp + 1;
	                    		}
							}
	                    	if(obj.has("temp4")) {
	                    		if(obj.getDouble("temp4") != 0) {
	                    			Temp = Temp + obj.getDouble("temp4");
		                    		countTemp = countTemp + 1;
	                    		}
							}
	                    	
	                    	if(obj.has("hum1")) {
	                    		if(obj.getDouble("hum1") != 0) {
		                    		Hum = Hum + obj.getDouble("hum1");
		                    		countHum = countHum + 1;
	                    		}

							}
	                    	if(obj.has("hum2")) {
	                    		if(obj.getDouble("hum2") != 0) {
		                    		Hum = Hum + obj.getDouble("hum2");
		                    		countHum = countHum + 1;
	                    		}
	
							}
	                    	if(obj.has("hum3")) {
	                    		if(obj.getDouble("hum3") != 0) {
		                    		Hum = Hum + obj.getDouble("hum3");
		                    		countHum = countHum + 1;
	                    		}
	
							}
	                    	if(obj.has("hum4")) {
	                    		if(obj.getDouble("hum4") != 0) {
		                    		Hum = Hum + obj.getDouble("hum4");
		                    		countHum = countHum + 1;
	                    		}
	
							}
	                    	
	                    	if(obj.has("wiretemp1")) {
	                    		if(obj.getDouble("wiretemp1") != 0) {
		                    		Temp = Temp + obj.getDouble("wiretemp1");
		                    		countTemp = countTemp + 1;
	                    		}
							}
	                    	if(obj.has("wiretemp2")) {
	                    		if(obj.getDouble("wiretemp2") != 0) {
		                    		Temp = Temp + obj.getDouble("wiretemp2");
		                    		countTemp = countTemp + 1;
	                    		}
	
							}
	                    	if(obj.has("wiretemp3")) {
	                    		if(obj.getDouble("wiretemp3") != 0) {
		                    		Temp = Temp + obj.getDouble("wiretemp3");
		                    		countTemp = countTemp + 1;
	                    		}
	
							}
	                    	if(obj.has("wiretemp4")) {
	                    		if(obj.getDouble("wiretemp4") != 0) {
		                    		Temp = Temp + obj.getDouble("wiretemp4");
		                    		countTemp = countTemp + 1;
	                    		}
	
							}
	                    	
	                    	if(obj.has("wirehum1")) {
	                    		if(obj.getDouble("wirehum1") != 0) {
		                    		Hum = Hum + obj.getDouble("wirehum1");
		                    		countHum = countHum + 1;
	                    		}
							}
	                    	if(obj.has("wirehum2")) {
	                    		if(obj.getDouble("wirehum2") != 0) {
		                    		Hum = Hum + obj.getDouble("wirehum2");
		                    		countHum = countHum + 1;
	                    		}
	
							}
	                    	if(obj.has("wirehum3")) {
	                    		if(obj.getDouble("wirehum3") != 0) {
		                    		Hum = Hum + obj.getDouble("wirehum3");
		                    		countHum = countHum + 1;
	                    		}
	
							}
	                    	if(obj.has("wirehum4")) {
	                    		if(obj.getDouble("wirehum4") != 0) {
		                    		Hum = Hum + obj.getDouble("wirehum4");
		                    		countHum = countHum + 1;
	                    		}
	
							}
	                    	
		                   	Double avgTemp = (double) 0;
		                   	Double avgHum = (double) 0;
		                   	if(countTemp != 0) {
			                    	avgTemp = Temp / countTemp;
		
		                   	}
		                   	if(countHum != 0) {
		                   		avgHum = Hum / countHum;
		
		                   	}
		                   	
	                   	        avgHum = Math.round(avgHum * 100.0) / 100.0;
                    	                avgTemp = Math.round(avgTemp * 100.0) / 100.0;
		                   	device.setTemperature(avgTemp);
		                   	device.setHumidity(avgHum);
	            	}
	            	if(object.containsField("deviceid") && object.get("deviceid") != null) {
	                	device.setDeviceId(object.getLong("deviceid"));
	
	            	}
	            	if(object.containsField("driverid") && object.get("driverid") != null) {
	                	device.setDriverId(object.getLong("driverid"));
	
	            	}
	            	if(object.containsField("speed") && object.get("speed") != null) {
		            	device.setSpeed(object.getDouble("speed") * (1.852) );    		
	                }
	            	
	            	if(object.containsField("weight") && object.get("weight") != null) {
		            	device.setWeight(object.getDouble("weight"));    		
	                }
	            	
					if(object.containsField("devicetime") && object.get("devicetime") != null) {
						
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
//						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();

						
						device.setDeviceTime(outputFormat.format(dateTime));
						
						
	                }
					

					if(object.containsField("servertime") && object.get("servertime") != null) {
						
						Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(object.getString("servertime"));

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
//						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();

						
						device.setServertime(outputFormat.format(dateTime));
						
						
	                }
					

					if(object.containsField("fixtime") && object.get("fixtime") != null) {
						
						Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(object.getString("fixtime"));

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
//						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();

						
						device.setFixTime(outputFormat.format(dateTime));
						
						
	                }
					
					if(object.containsField("_id") && object.get("_id") != null) {
						device.setId(object.getObjectId("_id").toString());

					}
					if(object.containsField("deviceName") && object.get("deviceName") != null) {
		            	device.setDeviceName(object.getString("deviceName"));    		
	                }
					if(object.containsField("driverName") && object.get("driverName") != null) {
		            	device.setDriverName(object.getString("driverName"));    		
	                }
					if(object.containsField("address") && object.get("address") != null) {
		            	device.setAddress(object.getString("address"));    		
	                }
					
	            	
	            	
					positions.add(device);
					
	            	
	            	
	            }
	        }
        
		return positions;
	}	
	
	
	public Integer getTripPositionsDetailsSize(Long deviceId,Date start,Date end){

		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
		
		Integer size = 0;

		
		
		
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(deviceId).and("devicetime").gte(start).lte(end)),
	            project("deviceid","latitude","longitude","deviceName","driverName","driverid","attributes","speed","weight","address")
	            .and("devicetime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("devicetime")
	            .and("servertime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("servertime")
	            .and("fixtime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("fixtime"),
	            sort(Sort.Direction.DESC, "devicetime"),
	            count().as("size")
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());
		

	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);


	        if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	size = object.getInt("size");
	            }

	        }
		return size;
	}
	
	

	public List<DeviceTempHum> getTripPositionsDetailsExport(Long deviceId,Date start,Date end){
	
		
		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
		
        List<DeviceTempHum> positions = new ArrayList<DeviceTempHum>();
				
	    Aggregation aggregation = newAggregation(
	            match(Criteria.where("deviceid").in(deviceId).and("devicetime").gte(start).lte(end)),
	            project("deviceid","latitude","longitude","deviceName","driverName","driverid","attributes","speed","weight","address")
	            .and("devicetime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("devicetime")
	            .and("servertime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("servertime")
	            .and("fixtime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("fixtime"),
	            sort(Sort.Direction.DESC, "devicetime")
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());

	    
	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);


            if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	
	            	DeviceTempHum device = new DeviceTempHum();
	            	
	            	

	            	if(object.containsField("latitude") && object.get("latitude") != null) {
	            		
	            		device.setLatitude(object.getDouble("latitude"));

	            	}
                    if(object.containsField("longitude") && object.get("longitude") != null) {
	            		
                    	device.setLongitude(object.getDouble("longitude"));

	            	}
	            	
	            	if(object.containsField("attributes") && object.get("attributes") != null) {
	                	device.setAttributes(object.get("attributes").toString());
	
	                	JSONObject obj = new JSONObject(device.getAttributes().toString());
	                	
	                       Integer countTemp = 0;
		                   Integer countHum = 0;
		                   	
		                   	Double Temp = (double) 0;
		                   	Double Hum = (double) 0;

	                    	if(obj.has("temp1")) {
	                    		if(obj.getDouble("temp1") != 0) {
		                    		Temp = Temp + obj.getDouble("temp1");
		                    		countTemp = countTemp + 1;
	                    		}
							}
	                    	if(obj.has("temp2")) {
	                    		if(obj.getDouble("temp2") != 0) {
		                    		Temp = Temp + obj.getDouble("temp2");
		                    		countTemp = countTemp + 1;
	                    		}
							}
	                    	if(obj.has("temp3")) {
	                    		if(obj.getDouble("temp3") != 0) {
		                    		Temp = Temp + obj.getDouble("temp3");
		                    		countTemp = countTemp + 1;
	                    		}
							}
	                    	if(obj.has("temp4")) {
	                    		if(obj.getDouble("temp4") != 0) {
	                    			Temp = Temp + obj.getDouble("temp4");
		                    		countTemp = countTemp + 1;
	                    		}
							}
	                    	
	                    	if(obj.has("hum1")) {
	                    		if(obj.getDouble("hum1") != 0) {
		                    		Hum = Hum + obj.getDouble("hum1");
		                    		countHum = countHum + 1;
	                    		}

							}
	                    	if(obj.has("hum2")) {
	                    		if(obj.getDouble("hum2") != 0) {
		                    		Hum = Hum + obj.getDouble("hum2");
		                    		countHum = countHum + 1;
	                    		}
	
							}
	                    	if(obj.has("hum3")) {
	                    		if(obj.getDouble("hum3") != 0) {
		                    		Hum = Hum + obj.getDouble("hum3");
		                    		countHum = countHum + 1;
	                    		}
	
							}
	                    	if(obj.has("hum4")) {
	                    		if(obj.getDouble("hum4") != 0) {
		                    		Hum = Hum + obj.getDouble("hum4");
		                    		countHum = countHum + 1;
	                    		}
	
							}
	                    	
	                    	if(obj.has("wiretemp1")) {
	                    		if(obj.getDouble("wiretemp1") != 0) {
		                    		Temp = Temp + obj.getDouble("wiretemp1");
		                    		countTemp = countTemp + 1;
	                    		}
							}
	                    	if(obj.has("wiretemp2")) {
	                    		if(obj.getDouble("wiretemp2") != 0) {
		                    		Temp = Temp + obj.getDouble("wiretemp2");
		                    		countTemp = countTemp + 1;
	                    		}
	
							}
	                    	if(obj.has("wiretemp3")) {
	                    		if(obj.getDouble("wiretemp3") != 0) {
		                    		Temp = Temp + obj.getDouble("wiretemp3");
		                    		countTemp = countTemp + 1;
	                    		}
	
							}
	                    	if(obj.has("wiretemp4")) {
	                    		if(obj.getDouble("wiretemp4") != 0) {
		                    		Temp = Temp + obj.getDouble("wiretemp4");
		                    		countTemp = countTemp + 1;
	                    		}
	
							}
	                    	
	                    	if(obj.has("wirehum1")) {
	                    		if(obj.getDouble("wirehum1") != 0) {
		                    		Hum = Hum + obj.getDouble("wirehum1");
		                    		countHum = countHum + 1;
	                    		}
							}
	                    	if(obj.has("wirehum2")) {
	                    		if(obj.getDouble("wirehum2") != 0) {
		                    		Hum = Hum + obj.getDouble("wirehum2");
		                    		countHum = countHum + 1;
	                    		}
	
							}
	                    	if(obj.has("wirehum3")) {
	                    		if(obj.getDouble("wirehum3") != 0) {
		                    		Hum = Hum + obj.getDouble("wirehum3");
		                    		countHum = countHum + 1;
	                    		}
	
							}
	                    	if(obj.has("wirehum4")) {
	                    		if(obj.getDouble("wirehum4") != 0) {
		                    		Hum = Hum + obj.getDouble("wirehum4");
		                    		countHum = countHum + 1;
	                    		}
	
							}
	                    	
		                   	Double avgTemp = (double) 0;
		                   	Double avgHum = (double) 0;
		                   	if(countTemp != 0) {
			                    	avgTemp = Temp / countTemp;
		
		                   	}
		                   	if(countHum != 0) {
		                   		avgHum = Hum / countHum;
		
		                   	}
		                   	
	                   	        avgHum = Math.round(avgHum * 100.0) / 100.0;
                    	                avgTemp = Math.round(avgTemp * 100.0) / 100.0;
		                   	device.setTemperature(avgTemp);
		                   	device.setHumidity(avgHum);
	            	}
	            	if(object.containsField("deviceid") && object.get("deviceid") != null) {
	                	device.setDeviceId(object.getLong("deviceid"));
	
	            	}
	            	if(object.containsField("driverid") && object.get("driverid") != null) {
	                	device.setDriverId(object.getLong("driverid"));
	
	            	}
	            	if(object.containsField("speed") && object.get("speed") != null) {
		            	device.setSpeed(object.getDouble("speed") * (1.852) );    		
	                }
	            	
	            	if(object.containsField("weight") && object.get("weight") != null) {
		            	device.setWeight(object.getDouble("weight"));    		
	                }
	            	
					if(object.containsField("devicetime") && object.get("devicetime") != null) {
						
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

						
						device.setDeviceTime(outputFormat.format(dateTime));
						
						
	                }
					

					if(object.containsField("servertime") && object.get("servertime") != null) {
						
						Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(object.getString("servertime"));

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();

						
						device.setServertime(outputFormat.format(dateTime));
						
						
	                }
					

					if(object.containsField("fixtime") && object.get("fixtime") != null) {
						
						Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(object.getString("fixtime"));

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();

						
						device.setFixTime(outputFormat.format(dateTime));
						
						
	                }
					
					if(object.containsField("_id") && object.get("_id") != null) {
						device.setId(object.getObjectId("_id").toString());

					}
					if(object.containsField("deviceName") && object.get("deviceName") != null) {
		            	device.setDeviceName(object.getString("deviceName"));    		
	                }
					if(object.containsField("driverName") && object.get("driverName") != null) {
		            	device.setDriverName(object.getString("driverName"));    		
	                }
					if(object.containsField("address") && object.get("address") != null) {
		            	device.setAddress(object.getString("address"));    		
	                }
					
	            	
	            	
					positions.add(device);
					
	            	
	            	
	            }
	        }
        
		return positions;
	}	
	public void getPdfSummaryData() {
		/*
		 * Requires the MongoDB Java Driver.
		 * https://mongodb.github.io/mongo-java-driver
		 */

//		MongoClient mongoClient = new MongoClient(
//		    new MongoClientURI(
//		        "mongodb://myAdmin:Alevs99!!456!!@b3.sareb.co:27017/?authSource=admin&ssl=false"
//		    )
//		);
//		MongoDatabase database = mongoClient.getDatabase("food_drugs_db");
//		MongoCollection<Document> collection = database.getCollection("tc_positions");
//
//		AggregateIterable<Document> result =  collection.aggregate(Arrays.asList(new Document("$match", 
//		    new Document("deviceid", 12)), 
//		    new Document("$project", 
//		    new Document("deviceid", 1)
//		            .append("averageTemp", 
//		    new Document("$avg", Arrays.asList("$attributes.temp1", "$attributes.wrieTemp1", "$attributes.temp2", "$attributes.temp3", "$attributes.temp4", "$attributes.temp6", "$attributes.temp7", "$attributes.temp8", "$attributes.wrieTemp2")))), 
//		    new Document("$group", 
//		    new Document("_id", "$deviceid")
//		            .append("count", 
//		    new Document("$sum", 1))
//		            .append("max", 
//		    new Document("$max", "$averageTemp"))
//		            .append("min", 
//		    new Document("$min", "$averageTemp"))
//		            .append("avg", 
//		    new Document("$avg", "$averageTemp")))));
		
		
//	    Aggregation aggregation = newAggregation(
//	            match(Criteria.where("deviceid").in(12))
////	            project("deviceid","attributes").andExpression("attributes", "$avg:[$attributes.temp1,$attributes.wireTemp1,$attributes.temp2,$attributes.temp3,$attributes.temp4,$attributes.temp6, $attributes.temp7 ,$attributes.temp7 ,$attributes.temp8 ,$attributes.wireTemp2]").as("averageTemp")
////	            .and("devicetime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("devicetime")
////	            .and("servertime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("servertime")
////	            .and("fixtime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("fixtime"),
////	            sort(Sort.Direction.DESC, "devicetime")
//	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());
//
//	    
//	        AggregationResults<BasicDBObject> groupResults
//	            = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);
//		
//		System.out.println(groupResults);
		Query query = new Query();
		query.addCriteria(Criteria.where("deviceid").in(12));
		List<Position> users = mongoTemplate.find(query, Position.class);
		System.out.println(users);
		
	}
	
}
