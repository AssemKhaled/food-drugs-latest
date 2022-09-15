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


import com.example.food_drugs.dto.responses.NewInventoryNotificationResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;

import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import com.example.food_drugs.entity.InventoryNotification;

import com.mongodb.BasicDBObject;

@Repository
public class MongoInventoryNotificationRepo {
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	public Integer getNotificationsTodaySize(List<Long> allInventories){

		Integer size = 0;

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 

		String currentDate=formatter.format(date);
		
		String from = currentDate +" 00:00:01";
		String to = currentDate +" 23:59:59";
		
		Date dateFrom = null;
		Date dateTo = null;
		try {
			dateFrom = output.parse(from);
			dateTo = output.parse(to);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(dateFrom);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		dateFrom = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(dateTo);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		dateTo = calendarTo.getTime();
		
	    Aggregation aggregation = newAggregation(
	    		 match(Criteria.where("inventory_id").in(allInventories).and("create_date").gte(dateFrom).lte(dateTo)),
		            project("type","attributes","inventory_id").and("create_date").dateAsFormattedString("%Y-%m-%d %H:%M:%S.%LZ").as("create_date"),
		            sort(Sort.Direction.DESC, "create_date"),
	                count().as("size")
	            
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());


	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_inventory_notifications", BasicDBObject.class);
	        

	        if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	size = object.getInt("size");
	            }

	        }
	        
		return size;
	}
	public List<InventoryNotification> getNotificationsToday(List<Long> allInventories,int offset){
		
		
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 

		String currentDate=formatter.format(date);
		
		String from = currentDate +" 00:00:01";
		String to = currentDate +" 23:59:59";
		
		Date dateFrom = null;
		Date dateTo = null;
		try {
			dateFrom = output.parse(from);
			dateTo = output.parse(to);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(dateFrom);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		dateFrom = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(dateTo);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		dateTo = calendarTo.getTime();
		
		List<InventoryNotification> notifications = new ArrayList<InventoryNotification>();
		
	    Aggregation aggregation = newAggregation(
	    		

	            match(Criteria.where("inventory_id").in(allInventories).and("create_date").gte(dateFrom).lte(dateTo)),
	            project("type","attributes","inventory_id").and("create_date").dateAsFormattedString("%Y-%m-%d %H:%M:%S.%LZ").as("create_date"),
	            sort(Sort.Direction.DESC, "create_date"),
	            skip(offset),
	            limit(10)
	            
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());


	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_inventory_notifications", BasicDBObject.class);



	        if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();

	            	InventoryNotification notification = new InventoryNotification();
                    if(object.containsField("inventory_id") && object.get("inventory_id") != null) {
                    	
                    	notification.setInventory_id(object.getLong("inventory_id"));

	            	}
					if(object.containsField("create_date") && object.get("create_date") != null) {
					
						Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(object.getString("create_date"));

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();
						
						notification.setCreate_date(outputFormat.format(dateTime));
						
	                }
					if(object.containsField("attributes") && object.get("attributes") != null) {
						notification.setAttributes(object.get("attributes").toString());
	                	
	            	}
					
					if(object.containsField("type") && object.get("type") != null) {
						notification.setType(object.get("type").toString());
	                	JSONObject attr = new JSONObject(notification.getAttributes().toString());

						if(notification.getType().equals("temperature alarm")) {
							if(attr.has("value")) {
								
								Double roundTemp = 0.0;
								roundTemp = Math.round(attr.getDouble("value") * 100.0) / 100.0;

								notification.setTemperature(roundTemp);
								
							}
						}
                        if(notification.getType().equals("humidity alarm")) {
							if(attr.has("value")) {
								
								Double roundHum = 0.0;
								roundHum = Math.round(attr.getDouble("value") * 100.0) / 100.0;
			            		
								notification.setHumidity(roundHum);

							}
						}
	                		                	

	            	}
					if(object.containsField("_id") && object.get("_id") != null) {
						notification.set_id(object.getObjectId("_id").toString());

					}
					notifications.add(notification);
                     
	            }
	        }
	        
		return notifications;
	}
	
    public List<InventoryNotification> getNotificationsReport(List<Long> allInventories,int offset,Date start,Date end){
		
		
		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
		List<InventoryNotification> notifications = new ArrayList<InventoryNotification>();
		
	    Aggregation aggregation = newAggregation(
	    		

	            match(Criteria.where("inventory_id").in(allInventories).and("create_date").gte(start).lte(end)),
	            project("type","attributes","inventory_id").and("create_date").dateAsFormattedString("%Y-%m-%d %H:%M:%S.%LZ").as("create_date"),
	            sort(Sort.Direction.DESC, "create_date"),
	            skip(offset),
	            limit(10)
	            
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());


	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_inventory_notifications", BasicDBObject.class);



	        if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();

	            	InventoryNotification notification = new InventoryNotification();
                    if(object.containsField("inventory_id") && object.get("inventory_id") != null) {
                    	
                    	notification.setInventory_id(object.getLong("inventory_id"));

	            	}
					if(object.containsField("create_date") && object.get("create_date") != null) {
						

						Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(object.getString("create_date"));

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();
						
						notification.setCreate_date(outputFormat.format(dateTime));						
	                }
					if(object.containsField("attributes") && object.get("attributes") != null) {
						notification.setAttributes(object.get("attributes").toString());
	                	
	            	}
					
					if(object.containsField("type") && object.get("type") != null) {
						notification.setType(object.get("type").toString());
	                	JSONObject attr = new JSONObject(notification.getAttributes().toString());

	                	if(notification.getType().equals("temperature alarm")) {
							if(attr.has("value")) {
								
								Double roundTemp = 0.0;
								roundTemp = Math.round(attr.getDouble("value") * 100.0) / 100.0;

								notification.setTemperature(roundTemp);


							}
						}
                        if(notification.getType().equals("humidity alarm")) {
							if(attr.has("value")) {

								Double roundHum = 0.0;
								roundHum = Math.round(attr.getDouble("value") * 100.0) / 100.0;
			            		
								notification.setHumidity(roundHum);
							}
						}
	                		                	

	            	}
					if(object.containsField("_id") && object.get("_id") != null) {
						notification.set_id(object.getObjectId("_id").toString());

					}
					notifications.add(notification);
                     
	            }
	        }
	        
		return notifications;
	}
    public List<NewInventoryNotificationResponse> newGetNotificationsReport(List<Long> allInventories,int offset,Date start,Date end){


		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();

		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();

		List<NewInventoryNotificationResponse> notifications = new ArrayList<>();

	    Aggregation aggregation = newAggregation(


	            match(Criteria.where("inventory_id").in(allInventories).and("create_date").gte(start).lte(end)),
	            project("type","attributes","inventory_id").and("create_date").dateAsFormattedString("%Y-%m-%d %H:%M:%S.%LZ").as("create_date"),
	            sort(Sort.Direction.DESC, "create_date"),
	            skip(offset),
	            limit(10)

	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());


	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_inventory_notifications", BasicDBObject.class);



	        if(groupResults.getMappedResults().size() > 0) {

	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();

					NewInventoryNotificationResponse notification = new NewInventoryNotificationResponse();
                    if(object.containsField("inventory_id") && object.get("inventory_id") != null) {

                    	notification.setInventory_id(object.getLong("inventory_id"));

	            	}
					if(object.containsField("create_date") && object.get("create_date") != null) {


						Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(object.getString("create_date"));

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}


						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();

						notification.setCreate_date(outputFormat.format(dateTime));
	                }
					if(object.containsField("attributes") && object.get("attributes") != null) {
						notification.setAttributes(object.get("attributes").toString());

	            	}

					if(object.containsField("type") && object.get("type") != null) {
						notification.setType(object.get("type").toString());
	                	JSONObject attr = new JSONObject(notification.getAttributes().toString());

	                	if(notification.getType().equals("temperature alarm")) {
							if(attr.has("value")) {

								Double roundTemp = 0.0;
								roundTemp = Math.round(attr.getDouble("value") * 100.0) / 100.0;

								notification.setValue(roundTemp);


							}
						}
                        if(notification.getType().equals("humidity alarm")) {
							if(attr.has("value")) {

								Double roundHum = 0.0;
								roundHum = Math.round(attr.getDouble("value") * 100.0) / 100.0;

								notification.setValue(roundHum);
							}
						}


	            	}
					if(object.containsField("_id") && object.get("_id") != null) {
						notification.set_id(object.getObjectId("_id").toString());

					}
					notifications.add(notification);

	            }
	        }

		return notifications;
	}

    public List<InventoryNotification> getNotificationsReportSchedule(List<Long> allInventories,Date start,Date end){
		
		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
		List<InventoryNotification> notifications = new ArrayList<InventoryNotification>();
		
	    Aggregation aggregation = newAggregation(
	    		

	            match(Criteria.where("inventory_id").in(allInventories).and("create_date").gte(start).lte(end)),
	            project("type","attributes","inventory_id").and("create_date").dateAsFormattedString("%Y-%m-%d %H:%M:%S.%LZ").as("create_date"),
	            sort(Sort.Direction.DESC, "create_date")
	            
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());


	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_inventory_notifications", BasicDBObject.class);



	        if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();

	            	InventoryNotification notification = new InventoryNotification();
                    if(object.containsField("inventory_id") && object.get("inventory_id") != null) {
                    	
                    	notification.setInventory_id(object.getLong("inventory_id"));

	            	}
					if(object.containsField("create_date") && object.get("create_date") != null) {
						
						Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(object.getString("create_date"));

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();
						
						notification.setCreate_date(outputFormat.format(dateTime));
						
	                }
					if(object.containsField("attributes") && object.get("attributes") != null) {
						notification.setAttributes(object.get("attributes").toString());
	                	
	            	}
					
					if(object.containsField("type") && object.get("type") != null) {
						notification.setType(object.get("type").toString());
	                	JSONObject attr = new JSONObject(notification.getAttributes().toString());

	                	if(notification.getType().equals("temperature alarm")) {
							if(attr.has("value")) {

								Double roundTemp = 0.0;
								roundTemp = Math.round(attr.getDouble("value") * 100.0) / 100.0;

								notification.setTemperature(roundTemp);
							}
						}
                        if(notification.getType().equals("humidity alarm")) {
							if(attr.has("value")) {

								Double roundHum = 0.0;
								roundHum = Math.round(attr.getDouble("value") * 100.0) / 100.0;
			            		
								notification.setHumidity(roundHum);
							}
						}
	                		                	

	            	}
					if(object.containsField("_id") && object.get("_id") != null) {
						notification.set_id(object.getObjectId("_id").toString());

					}
					notifications.add(notification);
                     
	            }
	        }
	        
		return notifications;
	}
    public List<NewInventoryNotificationResponse> newGetNotificationsReportSchedule(List<Long> allInventories,Date start,Date end){

		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();

		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();

		List<NewInventoryNotificationResponse> notifications = new ArrayList<>();

	    Aggregation aggregation = newAggregation(


	            match(Criteria.where("inventory_id").in(allInventories).and("create_date").gte(start).lte(end)),
	            project("type","attributes","inventory_id").and("create_date").dateAsFormattedString("%Y-%m-%d %H:%M:%S.%LZ").as("create_date"),
	            sort(Sort.Direction.DESC, "create_date")

	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());


	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_inventory_notifications", BasicDBObject.class);



	        if(groupResults.getMappedResults().size() > 0) {

	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();

					NewInventoryNotificationResponse notification = new NewInventoryNotificationResponse();
                    if(object.containsField("inventory_id") && object.get("inventory_id") != null) {

                    	notification.setInventory_id(object.getLong("inventory_id"));

	            	}
					if(object.containsField("create_date") && object.get("create_date") != null) {

						Date dateTime = null;
						SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");

						try {
							dateTime = inputFormat.parse(object.getString("create_date"));

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}


						Calendar calendarTime = Calendar.getInstance();
						calendarTime.setTime(dateTime);
						calendarTime.add(Calendar.HOUR_OF_DAY, 3);
						dateTime = calendarTime.getTime();

						notification.setCreate_date(outputFormat.format(dateTime));

	                }
					if(object.containsField("attributes") && object.get("attributes") != null) {
						notification.setAttributes(object.get("attributes").toString());

	            	}

					if(object.containsField("type") && object.get("type") != null) {
						notification.setType(object.get("type").toString());
	                	JSONObject attr = new JSONObject(notification.getAttributes().toString());

	                	if(notification.getType().equals("temperature alarm")) {
							if(attr.has("value")) {

								Double roundTemp = 0.0;
								roundTemp = Math.round(attr.getDouble("value") * 100.0) / 100.0;

								notification.setValue(roundTemp);
							}
						}
                        if(notification.getType().equals("humidity alarm")) {
							if(attr.has("value")) {

								Double roundHum = 0.0;
								roundHum = Math.round(attr.getDouble("value") * 100.0) / 100.0;

								notification.setValue(roundHum);
							}
						}


	            	}
					if(object.containsField("_id") && object.get("_id") != null) {
						notification.set_id(object.getObjectId("_id").toString());

					}
					notifications.add(notification);

	            }
	        }

		return notifications;
	}

    public Integer getNotificationsReportSize(List<Long> allInventories,Date start,Date end){
		
		
		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(start);
		calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
		start = calendarFrom.getTime();
	    
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(end);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		end = calendarTo.getTime();
		
    	Integer size =0;
		
	    Aggregation aggregation = newAggregation(
	    		

	            match(Criteria.where("inventory_id").in(allInventories).and("create_date").gte(start).lte(end)),
	            project("type","attributes","inventory_id").and("create_date").dateAsFormattedString("%Y-%m-%d %H:%M:%S.%LZ").as("create_date"),
	            sort(Sort.Direction.ASC, "create_date"),
	            count().as("size")
	            
	    		).withOptions(newAggregationOptions().allowDiskUse(true).build());


	        AggregationResults<BasicDBObject> groupResults
	            = mongoTemplate.aggregate(aggregation,"tc_inventory_notifications", BasicDBObject.class);


           if(groupResults.getMappedResults().size() > 0) {
	        	
	            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
	            while (iterator.hasNext()) {
	            	BasicDBObject object = (BasicDBObject) iterator.next();
	            	size = object.getInt("size");
	            }

	        }
	        
		return size;
	}
}
