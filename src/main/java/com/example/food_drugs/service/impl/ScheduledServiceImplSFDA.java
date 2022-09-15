package com.example.food_drugs.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.example.food_drugs.service.ScheduledServiceSFDA;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.entity.Schedule;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.ScheduledRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.example.examplequerydslspringdatajpamaven.service.ScheduledServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.UserRoleService;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;
import com.example.food_drugs.dto.DeviceTempHum;
import com.example.food_drugs.entity.InventoryLastData;
import com.example.food_drugs.entity.InventoryNotification;
import com.example.food_drugs.repository.ScheduledRepositorySFDA;

/**
 * services functionality related to schedules
 * @author fuinco
 *
 */
@Component
@Service
public class ScheduledServiceImplSFDA extends RestServiceController implements ScheduledServiceSFDA {

	private static final Log logger = LogFactory.getLog(ScheduledServiceImpl.class);

	private GetObjectResponse getObjectResponse;
	
	@Autowired
	private UserServiceImpl userServiceImpl;
	
	@Autowired
	private UserRoleService userRoleService;
	
	@Autowired
	private ScheduledRepository scheduledRepository;

	@Autowired
	private ScheduledRepositorySFDA scheduledRepositorySFDA;
	
	@Autowired
	private UserRepository userRepository;

     @Autowired
     private JavaMailSender emailSender;
	
	 @Value("${stopsUrl}")
	 private String stopsUrl;
	 
	 @Value("${tripsUrl}")
	 private String tripsUrl;
	 
	 @Value("${eventsUrl}")
	 private String eventsUrl;
	 
	 @Value("${summaryUrl}")
	 private String summaryUrl;
	
	 @Value("${spring.mail.username}")
	 private String emaiFrom;
	
	 @Autowired
	 private ReportServiceImplSFDA reportServiceImplSFDA;
		
	@Override
	public ResponseEntity<?> activeScheduled(String TOKEN, Long scheduledId, Long userId) {
		// TODO Auto-generated method stub
		if(TOKEN.equals("")) {
			 List<Schedule> schedules= null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",schedules);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		User loggedUser = userServiceImpl.findById(userId);
		 if(loggedUser == null) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged User is not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse); 
		 }
		 if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "SCHEDULED", "delete")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to delete schedule",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(scheduledId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No scheduledId  to delete ",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		Schedule schedule= scheduledRepository.findOne(scheduledId);
		if(schedule == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this scheduledId not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		


		Long createdBy=schedule.getUserId();
		Boolean isParent=false;

		if(createdBy.toString().equals(userId.toString())) {
			isParent=true;
		}
		List<User>childs = new ArrayList<User>();
		if(loggedUser.getAccountType().equals(4)) {
			if(isParent) {
				
                schedule.setDelete_date(null);
			    scheduledRepository.save(schedule);
			    
				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",null);
				return  ResponseEntity.ok().body(getObjectResponse);
				
				
			}
			 
		}
		else {
			childs = userServiceImpl.getAllChildernOfUser(userId);
		}
		
		
 		
		User parentChilds = new User();
		if(!childs.isEmpty()) {
			for(User object : childs) {
				parentChilds = object;
				if(parentChilds.getId().toString().equals(createdBy.toString())) {
					isParent=true;
					break;
				}
			}
		}
		if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get schedule",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		schedule.setDelete_date(null);  
	    scheduledRepository.save(schedule);
	   
	    
	    
	    
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",null);
		return  ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> getScheduledListSFDA(String TOKEN, Long id, int offset, String search, int active,String exportData) {
	   logger.info("************************ getScheduledList STARTED ***************************");
		
		List<Schedule> schedules = new ArrayList<Schedule>();
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",schedules);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {
			
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",schedules);
				return  ResponseEntity.status(404).body(getObjectResponse);
	
			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(id, "SCHEDULED", "list")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get SCHEDULED list",null);
						 logger.info("************************ getScheduledList ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(user.getDeleteDate() == null) {
					
					userServiceImpl.resetChildernArray();
				    if(user.getAccountType().equals(4)) {
						
							 List<Long>usersIds= new ArrayList<>();
							 usersIds.add(user.getId());
							
							 Integer size = 0;
							 List<Map> data = new ArrayList<>();
							
							 if(active == 0) {
								if(exportData.equals("exportData")) {
									schedules = scheduledRepositorySFDA.getAllScheduledDeactiveExport(usersIds,search);

								}
								else {
									schedules = scheduledRepositorySFDA.getAllScheduledDeactive(usersIds,offset,search);
									size = scheduledRepositorySFDA.getAllScheduledSizeDeactive(usersIds);
								}

							 }
							 
				             if(active == 2) {
								if(exportData.equals("exportData")) {
					            	 schedules = scheduledRepositorySFDA.getAllScheduledAllExport(usersIds,search);

								}
								else {
					            	 schedules = scheduledRepositorySFDA.getAllScheduledAll(usersIds,offset,search);
									 size = scheduledRepositorySFDA.getAllScheduledSizeAll(usersIds);
								}
	
							 }
				             
				             if(active == 1) {
								if(exportData.equals("exportData")) {
					            	 schedules = scheduledRepository.getAllScheduledExport(usersIds,search);

								}
								else {
					            	 schedules = scheduledRepository.getAllScheduled(usersIds,offset,search);
									 size = scheduledRepository.getAllScheduledSize(usersIds);	
								}
									
							 }
							 for(Schedule schedule:schedules) {
							     Map scheduleList= new HashMap();

								 scheduleList.put("date", schedule.getDate());
								 scheduleList.put("date_type", schedule.getDate_type());
								 scheduleList.put("delete_date", schedule.getDelete_date());
								 scheduleList.put("expression", schedule.getExpression());
								 scheduleList.put("id", schedule.getId());
								 scheduleList.put("task", schedule.getTask());
								 scheduleList.put("userId", schedule.getUserId());
								 scheduleList.put("userName", null);

								 User us = userRepository.findOne(schedule.getUserId());
								 if(us != null) {
									 scheduleList.put("userName", us.getName());

								 }
								 data.add(scheduleList);

							 }
							 
							getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",data,size);
							logger.info("************************ getScheduledList ENDED ***************************");
							return  ResponseEntity.ok().body(getObjectResponse);
						 
					}
				    List<User>childernUsers = userServiceImpl.getActiveAndInactiveChildern(id);
					 List<Long>usersIds= new ArrayList<>();
					 if(childernUsers.isEmpty()) {
						 usersIds.add(id);
					 }
					 else {
						 usersIds.add(id);
						 for(User object : childernUsers) {
							 usersIds.add(object.getId());
						 }
					 }
	
					
					 List<Map> data = new ArrayList<>();
					 Integer size = 0;

					 

					 if(active == 0) {
						if(exportData.equals("exportData")) {
							schedules = scheduledRepositorySFDA.getAllScheduledDeactiveExport(usersIds,search);

						}
						else {
							schedules = scheduledRepositorySFDA.getAllScheduledDeactive(usersIds,offset,search);
							size = scheduledRepositorySFDA.getAllScheduledSizeDeactive(usersIds);
						}

					 }
					 
		             if(active == 2) {
						if(exportData.equals("exportData")) {
			            	 schedules = scheduledRepositorySFDA.getAllScheduledAllExport(usersIds,search);

						}
						else {
			            	 schedules = scheduledRepositorySFDA.getAllScheduledAll(usersIds,offset,search);
							 size = scheduledRepositorySFDA.getAllScheduledSizeAll(usersIds);
						}

					 }
		             
		             if(active == 1) {
						if(exportData.equals("exportData")) {
			            	 schedules = scheduledRepository.getAllScheduledExport(usersIds,search);

						}
						else {
			            	 schedules = scheduledRepository.getAllScheduled(usersIds,offset,search);
							 size = scheduledRepository.getAllScheduledSize(usersIds);	
						}
							
					 }
					 for(Schedule schedule:schedules) {
					     Map scheduleList= new HashMap();

						 scheduleList.put("date", schedule.getDate());
						 scheduleList.put("date_type", schedule.getDate_type());
						 scheduleList.put("delete_date", schedule.getDelete_date());
						 scheduleList.put("expression", schedule.getExpression());
						 scheduleList.put("id", schedule.getId());
						 scheduleList.put("task", schedule.getTask());
						 scheduleList.put("userId", schedule.getUserId());
						 scheduleList.put("userName", null);

						 User us = userRepository.findOne(schedule.getUserId());
						 if(us != null) {
							 scheduleList.put("userName", us.getName());

						 }
						 data.add(scheduleList);

					 }
						
	
					 
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",data,size);
					logger.info("************************ getScheduledList ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);
	
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",schedules);
					return  ResponseEntity.status(404).body(getObjectResponse);
	
				}
				
			}
	
		}
		else{
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",schedules);
			return  ResponseEntity.badRequest().body(getObjectResponse);
	
		}
	}

	@Override
	public void accessExpressionSFDA(String expression) {
		// TODO Auto-generated method stub
		logger.info("************************ Start Access Expression SFDA STARTED ***************************");

		doReportsSFDA(expression);
	    
		logger.info("************************ Start Access Expression SFDA ENDED ***************************");

	}

	@Override
	public void doReportsSFDA(String Expression) {
		// TODO Auto-generated method stub
		
		List<Schedule> scheduleCheckBeforeRemove = scheduledRepository.getAllScheduledHaveExpression(Expression);

	    List<Map> data = new ArrayList<>();
		Map resp = new HashMap();
		JSONArray reports = new JSONArray();
	    JSONArray deviceIds = new JSONArray();
	    JSONArray warehouseIds = new JSONArray();
	    JSONArray inventoryIds = new JSONArray();
	    JSONArray groupIds = new JSONArray();

	    String custom ="";
	    String value ="";

	    for(Schedule object : scheduleCheckBeforeRemove) {
	    	
	    	String email="";
	    	Long userId = object.getUserId();
	    	email = object.getEmail();

	    	String dateType = object.getDate_type();
	    	String from = "";
	    	String to = "";
			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
			String currentDate=formatter.format(date);
			
	    	if(dateType.equals("everyMonth")) {
	    		
	    		Date referenceDate = new Date();
	    		Calendar c = Calendar.getInstance(); 
	    		c.setTime(referenceDate); 
	    		c.add(Calendar.MONTH, -1);	    		
	    		String fromDate=formatter.format(c.getTime());

	    		from = fromDate;
	    		to = currentDate;
	    		
	    	}
            if(dateType.equals("everyWeek")) {
	    		
	    		Date referenceDate = new Date();
	    		Calendar c = Calendar.getInstance(); 
	    		c.setTime(referenceDate); 
	    		c.add(Calendar.DATE, -7);	    		
	    		String fromDate=formatter.format(c.getTime());

	    		from = fromDate;
	    		to = currentDate;
	    		
	    	}
            if(dateType.equals("everyDay")) {
	    		
	    		Date referenceDate = new Date();
	    		Calendar c = Calendar.getInstance(); 
	    		c.setTime(referenceDate); 
	    		c.add(Calendar.DATE, -1);	    		
	    		String fromDate=formatter.format(c.getTime());

	    		from = fromDate;
	    		to = currentDate;
	    		
	    	}
	    	
	    	
		    if(object.getTask().toString().startsWith("{")) {

			   JSONObject obj = new JSONObject(object.getTask().toString());

			   
			   if(obj.has("reports")) {
				   reports = obj.getJSONArray("reports");
				   
			   }
			   if(obj.has("custom")) {
				   custom = (String) obj.get("custom");
				   
			   }
			   if(obj.has("value")) {
				   value = (String) obj.get("value");
				   
			   }
			   if(obj.has("warehouseIds")) {
				   warehouseIds = obj.getJSONArray("warehouseIds");
				   
			   }
			   if(obj.has("deviceIds")) {
				   deviceIds = obj.getJSONArray("deviceIds");
				   
			   }
			   if(obj.has("inventoryIds")) {
				   inventoryIds = obj.getJSONArray("inventoryIds");
				   
			   }
			   if(obj.has("groupIds")) {
				   groupIds = obj.getJSONArray("groupIds");
				   
			   }

		    }
		    
		
			Long [] deviIds= new Long[deviceIds.length()];
			Long [] wareIds=new Long[warehouseIds.length()];
			Long [] invIds=new Long[inventoryIds.length()];
			Long [] grouIds=new Long[groupIds.length()];



		    for (int i = 0 ; i < deviceIds.length(); i++) {
		    	deviIds[i]=deviceIds.getLong(i);		    
		    }
		    
		    for (int i = 0 ; i < warehouseIds.length(); i++) {
		    	wareIds[i]=warehouseIds.getLong(i);		    
		    }
		    
		    for (int i = 0 ; i < inventoryIds.length(); i++) {
		    	invIds[i]=inventoryIds.getLong(i);		    
		    }
		    
		    for (int i = 0 ; i < groupIds.length(); i++) {
		    	grouIds[i]=groupIds.getLong(i);		    
		    }
		    
		    
		    for (int i = 0 ; i < reports.length(); i++) {
		    	
		    	if(reports.get(i).equals("vehicleTempHum")) {
		    		
		    		ResponseEntity<?> response = reportServiceImplSFDA.getVehicleTempHum("Schedule", deviIds, grouIds, 0, from, to, "", userId,"", "+0300");


//					ResponseEntity<?> response = reportServiceImplSFDA.getVehicleTempHumNew("Schedule", deviIds, grouIds, 0, from, to, "", userId,"");


					getObjectResponse = (GetObjectResponse) response.getBody();
		    		List<DeviceTempHum> sensorReports = (List<DeviceTempHum>) getObjectResponse.getEntity();

					Random rand = new Random();
					int n = rand.nextInt(999999);
		    		String[] columns = {"Vehicle Name", "Time", "Temperature", "Humidity",
		    				"Weight (kg)","Last Speed (Km/h)"};
				    createExcelSFDA("vehicleTempHum",sensorReports, "vehicleTempHum"+n+".xlsx",columns);
				    sendMailSFDA("vehicleTempHum"+n+".xlsx",email);
		    		
		    	}
		    	
		    	if(reports.get(i).equals("inventoryTempHum")) {
		    		ResponseEntity<?> response = reportServiceImplSFDA.getInventoriesReport("Schedule", invIds, 0, from, to, "", userId, "");
		    	
		    		getObjectResponse = (GetObjectResponse) response.getBody();
		    		List<InventoryLastData> sensorReports = (List<InventoryLastData>) getObjectResponse.getEntity();

					Random rand = new Random();
					int n = rand.nextInt(999999);
		    		String[] columns = {"Inventory Name", "Time", "Temperature", "Humidity"};
				    createExcelSFDA("inventoryTempHum",sensorReports, "inventoryTempHum"+n+".xlsx",columns);
				    sendMailSFDA("inventoryTempHum"+n+".xlsx",email);
		    	}
		    	if(reports.get(i).equals("warehouseTempHum")) {
		    		ResponseEntity<?> response = reportServiceImplSFDA.getWarehousesReport("Schedule", wareIds, 0, from, to, "", userId, "");
			    	
		    		getObjectResponse = (GetObjectResponse) response.getBody();
		    		List<InventoryLastData> sensorReports = (List<InventoryLastData>) getObjectResponse.getEntity();

					Random rand = new Random();
					int n = rand.nextInt(999999);
		    		String[] columns = {"Warehouse Name","Inventory Name", "Time", "Temperature", "Humidity"};
				    createExcelSFDA("warehouseTempHum",sensorReports, "warehouseTempHum"+n+".xlsx",columns);
				    sendMailSFDA("warehouseTempHum"+n+".xlsx",email);
		    		
		    	}
		    	if(reports.get(i).equals("notificationTempHum")) {
                    ResponseEntity<?> response = reportServiceImplSFDA.getNotificationReport("Schedule", invIds, wareIds, 0, from, to, "", userId, "");
			    	
		    		getObjectResponse = (GetObjectResponse) response.getBody();
		    		List<InventoryNotification> sensorReports = (List<InventoryNotification>) getObjectResponse.getEntity();

					Random rand = new Random();
					int n = rand.nextInt(999999);
		    		String[] columns = {"Warehouse Name","Inventory Name", "Type","Time", "Temperature", "Humidity"};
				    createExcelSFDA("notificationTempHum",sensorReports, "notificationTempHum"+n+".xlsx",columns);
				    sendMailSFDA("notificationTempHum"+n+".xlsx",email);
		    	}
                
		    	
		    }
		    

		    
		   
	    }
	    
	}

	@Override
	public boolean sendMailSFDA(String excelName, String email) {
		// TODO Auto-generated method stub
		logger.info("************************ sendMail SFDA STARTED ***************************");

        MimeMessage message = emailSender.createMimeMessage();
        
        MimeMessageHelper helper = null;
		

        try {
			helper = new MimeMessageHelper(message, true);
			helper.setFrom(emaiFrom);
			helper.setTo(email);
	        helper.setSubject("Report Schedule");
	        helper.setText("Check Your Report");
		} catch (MessagingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        String path ="/var/www/html/sareb_sheets/";
        
        FileSystemResource file 
          = new FileSystemResource(new File(path+excelName));
        try {
			helper.addAttachment(file.getFilename(), file);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     
        emailSender.send(message);
        File filee = new File(path+excelName);
		if (filee.isFile()) {
			filee.delete();
		}
		logger.info("************************ sendMail SFDA ENDED ***************************");

		return true;
	}

	@Override
	public Boolean createExcelSFDA(String reportType, List<?> entity, String excelName, String[] columns) {
		// TODO Auto-generated method stub
		List<DeviceTempHum> deviceTempHumReport = new ArrayList<DeviceTempHum>();
		List<InventoryLastData> inventoryTempHumReport = new ArrayList<InventoryLastData>();
		List<InventoryLastData> warehouseTempHumReport = new ArrayList<InventoryLastData>();
		List<InventoryNotification> notificationTempHumReport = new ArrayList<InventoryNotification>();

		if(reportType.equals("vehicleTempHum")) {
			deviceTempHumReport = (List<DeviceTempHum>) entity;

		}
		if(reportType.equals("inventoryTempHum")) {
			inventoryTempHumReport = (List<InventoryLastData>) entity;

		}
		if(reportType.equals("warehouseTempHum")) {
			warehouseTempHumReport = (List<InventoryLastData>) entity;

		}
		if(reportType.equals("notificationTempHum")) {
			notificationTempHumReport = (List<InventoryNotification>) entity;

		}
		
		Workbook workbook = new XSSFWorkbook();
		
		
	    Sheet sheet = workbook.createSheet("sheet#1");

        CreationHelper createHelper = workbook.getCreationHelper();

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.BLUE_GREY.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);

        
        for(int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }
        


        int rowNum = 1;
        
        if(reportType.equals("notificationTempHum")) {
        	for(InventoryNotification notificationTemp: notificationTempHumReport) {


                Row row = sheet.createRow(rowNum++);
                
                if(notificationTemp.getWarehouseName() == null){
                	row.createCell(0)
                    .setCellValue("");
                }
                else {
                	row.createCell(0)
                    .setCellValue(notificationTemp.getWarehouseName());
                }
                
                if(notificationTemp.getInventoryName() == null){
                	row.createCell(1)
                    .setCellValue("");
                }
                else {
                	row.createCell(1)
                    .setCellValue(notificationTemp.getInventoryName());
                }
                
                if(notificationTemp.getCreate_date() == null){
                	row.createCell(2)
                    .setCellValue("");
                }
                else {
                	row.createCell(2)
                    .setCellValue(notificationTemp.getCreate_date());
                }
                
                if(notificationTemp.getType() == null){
                	row.createCell(3)
                    .setCellValue("");
                }
                else {
                	row.createCell(3)
                    .setCellValue(notificationTemp.getType());
                }
                
                if(notificationTemp.getTemperature() == null){
                	row.createCell(4)
                    .setCellValue("");
                }
                else {
                	row.createCell(4)
                    .setCellValue(notificationTemp.getTemperature());
                }
                
                if(notificationTemp.getHumidity() == null){
                	row.createCell(5)
                    .setCellValue("");
                }
                else {
                	row.createCell(5)
                    .setCellValue(notificationTemp.getHumidity());
                }

              

            }
        }
        
        if(reportType.equals("warehouseTempHum")) {
        	for(InventoryLastData warehouseTemp: warehouseTempHumReport) {


                Row row = sheet.createRow(rowNum++);
                
                if(warehouseTemp.getWarehouseName() == null){
                	row.createCell(0)
                    .setCellValue("");
                }
                else {
                	row.createCell(0)
                    .setCellValue(warehouseTemp.getWarehouseName());
                }
                
                if(warehouseTemp.getInventoryName() == null){
                	row.createCell(1)
                    .setCellValue("");
                }
                else {
                	row.createCell(1)
                    .setCellValue(warehouseTemp.getInventoryName());
                }
                
                if(warehouseTemp.getCreate_date() == null){
                	row.createCell(2)
                    .setCellValue("");
                }
                else {
                	row.createCell(2)
                    .setCellValue(warehouseTemp.getCreate_date());
                }
                
                if(warehouseTemp.getTemperature() == null){
                	row.createCell(3)
                    .setCellValue("");
                }
                else {
                	row.createCell(3)
                    .setCellValue(warehouseTemp.getTemperature());
                }
                
                if(warehouseTemp.getHumidity() == null){
                	row.createCell(4)
                    .setCellValue("");
                }
                else {
                	row.createCell(4)
                    .setCellValue(warehouseTemp.getHumidity());
                }

              

            }
        }
        
        if(reportType.equals("inventoryTempHum")) {
        	for(InventoryLastData inventoryTemp: inventoryTempHumReport) {


                Row row = sheet.createRow(rowNum++);
                
               
                if(inventoryTemp.getInventoryName() == null){
                	row.createCell(0)
                    .setCellValue("");
                }
                else {
                	row.createCell(0)
                    .setCellValue(inventoryTemp.getInventoryName());
                }
                
                if(inventoryTemp.getCreate_date() == null){
                	row.createCell(1)
                    .setCellValue("");
                }
                else {
                	row.createCell(1)
                    .setCellValue(inventoryTemp.getCreate_date());
                }
                
                if(inventoryTemp.getTemperature() == null){
                	row.createCell(2)
                    .setCellValue("");
                }
                else {
                	row.createCell(2)
                    .setCellValue(inventoryTemp.getTemperature());
                }
                
                if(inventoryTemp.getHumidity() == null){
                	row.createCell(3)
                    .setCellValue("");
                }
                else {
                	row.createCell(3)
                    .setCellValue(inventoryTemp.getHumidity());
                }

              

            }
        }
        if(reportType.equals("vehicleTempHum")) {
        	for(DeviceTempHum deviceTempHum: deviceTempHumReport) {


                Row row = sheet.createRow(rowNum++);
                
               
                if(deviceTempHum.getDeviceName() == null){
                	row.createCell(0)
                    .setCellValue("");
                }
                else {
                	row.createCell(0)
                    .setCellValue(deviceTempHum.getDeviceName());
                }
                
                if(deviceTempHum.getServertime() == null){
                	row.createCell(1)
                    .setCellValue("");
                }
                else {
                	row.createCell(1)
                    .setCellValue(deviceTempHum.getServertime());
                }
                
                if(deviceTempHum.getTemperature() == null){
                	row.createCell(2)
                    .setCellValue("");
                }
                else {
                	row.createCell(2)
                    .setCellValue(deviceTempHum.getTemperature());
                }
                
                if(deviceTempHum.getHumidity() == null){
                	row.createCell(3)
                    .setCellValue("");
                }
                else {
                	row.createCell(3)
                    .setCellValue(deviceTempHum.getHumidity());
                }
                
                if(deviceTempHum.getWeight() == null){
                	row.createCell(4)
                    .setCellValue("");
                }
                else {
                	row.createCell(4)
                    .setCellValue(deviceTempHum.getWeight());
                }
                
                
                if(deviceTempHum.getSpeed() == null){
                	row.createCell(5)
                    .setCellValue("");
                }
                else {
                	row.createCell(5)
                    .setCellValue(deviceTempHum.getSpeed());
                }
                
              

            }
        }


        for(int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }


        FileOutputStream fileOut = null;
        String path ="/var/www/html/sareb_sheets/";
        
		try {
			fileOut = new FileOutputStream(path+excelName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			workbook.write(fileOut);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			fileOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


        try {
			workbook.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
		
		return true;
	}

}
