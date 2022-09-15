package com.example.food_drugs.entity;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import com.example.examplequerydslspringdatajpamaven.entity.Device;



@SqlResultSetMappings({
	@SqlResultSetMapping(
        name="DevicesListSFDA",
        classes={
           @ConstructorResult(
                targetClass=CustomDeviceListSFDA.class,
                  columns={
                     @ColumnResult(name="id",type=int.class),
                     @ColumnResult(name="deviceName",type=String.class),
                     @ColumnResult(name="simcardNumber",type=String.class),
                     @ColumnResult(name="uniqueId",type=String.class),
                     @ColumnResult(name="sequenceNumber",type=String.class),
                     @ColumnResult(name="lastUpdate",type=String.class),
                     @ColumnResult(name="referenceKey",type=String.class),
                     @ColumnResult(name="expired",type=Boolean.class),
                     @ColumnResult(name="driverName",type=String.class),
                     @ColumnResult(name="companyName",type=String.class),
                     @ColumnResult(name="companyId",type=Long.class),
                     @ColumnResult(name="geofenceName",type=String.class),
                     @ColumnResult(name="delete_date",type=String.class),
                     @ColumnResult(name="create_date",type=String.class),
                     @ColumnResult(name="delete_date_elm",type=String.class),
                     @ColumnResult(name="update_date_elm",type=String.class),
                     @ColumnResult(name="leftDays",type=Long.class)

                     }
           )
        }
	)
})


@NamedNativeQueries({
	

	@NamedNativeQuery(name="getDevicesListAll", 
	     resultSetMapping="DevicesListSFDA", 
	     query=" SELECT tc_devices.id as id ,tc_devices.name as deviceName,tc_devices.simcardNumber as simcardNumber, tc_devices.uniqueid as uniqueId,"
	     		+ " tc_devices.sequence_number as sequenceNumber ,tc_devices.delete_date as delete_date ,tc_devices.lastupdate as lastUpdate "
	     		+ " ,tc_devices.reference_key as referenceKey, tc_devices.expired as expired, "
				+ " tc_devices.start_date as startDate ,tc_devices.end_date as endDate , "
	     		+ " tc_drivers.name as driverName,tc_users.name as companyName,tc_users.id as companyId ,GROUP_CONCAT(tc_geofences.name )AS geofenceName"
	     		+ " ,tc_devices.create_date as create_date ,tc_devices.delete_from_elm_date as delete_date_elm  ,tc_devices.update_date_in_elm as update_date_elm, DATEDIFF(DATE_ADD(tc_devices.update_date_in_elm, INTERVAL 275 DAY),CURDATE()) as leftDays FROM tc_devices LEFT JOIN  tc_device_driver ON tc_devices.id=tc_device_driver.deviceid"
	     		+ " LEFT JOIN  tc_drivers ON tc_drivers.id=tc_device_driver.driverid and tc_drivers.delete_date is null" 
	     		+ " LEFT JOIN  tc_device_geofence ON tc_devices.id=tc_device_geofence.deviceid" 
	     		+ " LEFT JOIN  tc_geofences ON tc_geofences.id=tc_device_geofence.geofenceid and tc_geofences.delete_date"
	     		+ " is null INNER JOIN tc_user_device ON tc_user_device.deviceid = tc_devices.id "
	     		+ " LEFT JOIN tc_users ON tc_user_device.userid = tc_users.id" 
	     		+ " where tc_user_device.userid IN(:userIds) AND ((TIMESTAMPDIFF(day ,CURDATE(),tc_devices.end_date) >=0)  OR :isAdmin) "
	     		+ " AND ( tc_devices.simcardNumber LIKE LOWER(CONCAT('%',:search, '%')) OR  tc_devices.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.uniqueid LIKE LOWER(CONCAT('%',:search, '%')) "
	     		+ " OR tc_devices.reference_key LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.sequence_number LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.lastupdate LIKE LOWER(CONCAT('%',:search, '%'))"
	     		+ " OR tc_drivers.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_geofences.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_users.name LIKE LOWER(CONCAT('%',:search, '%')) ) "
	     		+ " GROUP BY tc_devices.id,tc_drivers.id,tc_users.id LIMIT :offset,10"),
	
	@NamedNativeQuery(name="getDevicesListAllExport", 
    resultSetMapping="DevicesListSFDA", 
    query=" SELECT tc_devices.id as id ,tc_devices.name as deviceName,tc_devices.simcardNumber as simcardNumber, tc_devices.uniqueid as uniqueId,"
    		+ " tc_devices.sequence_number as sequenceNumber ,tc_devices.delete_date as delete_date ,tc_devices.lastupdate as lastUpdate "
    		+ " ,tc_devices.reference_key as referenceKey, tc_devices.expired as expired, "
			+ " tc_devices.start_date as startDate ,tc_devices.end_date as endDate , "
    		+ " tc_drivers.name as driverName,tc_users.name as companyName,tc_users.id as companyId ,GROUP_CONCAT(tc_geofences.name )AS geofenceName"
    		+ " ,tc_devices.create_date as create_date ,tc_devices.delete_from_elm_date as delete_date_elm   ,tc_devices.update_date_in_elm as update_date_elm , DATEDIFF(DATE_ADD(tc_devices.update_date_in_elm, INTERVAL 275 DAY),CURDATE()) as leftDays  FROM tc_devices LEFT JOIN  tc_device_driver ON tc_devices.id=tc_device_driver.deviceid"
    		+ " LEFT JOIN  tc_drivers ON tc_drivers.id=tc_device_driver.driverid and tc_drivers.delete_date is null" 
    		+ " LEFT JOIN  tc_device_geofence ON tc_devices.id=tc_device_geofence.deviceid" 
    		+ " LEFT JOIN  tc_geofences ON tc_geofences.id=tc_device_geofence.geofenceid and tc_geofences.delete_date"
    		+ " is null INNER JOIN tc_user_device ON tc_user_device.deviceid = tc_devices.id "
    		+ " LEFT JOIN tc_users ON tc_user_device.userid = tc_users.id" 
    		+ " where tc_user_device.userid IN(:userIds)  AND ((TIMESTAMPDIFF(day ,CURDATE(),tc_devices.end_date) >=0)  OR :isAdmin) "
    		+ " AND ( tc_devices.simcardNumber LIKE LOWER(CONCAT('%',:search, '%')) OR  tc_devices.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.uniqueid LIKE LOWER(CONCAT('%',:search, '%')) "
    		+ " OR tc_devices.reference_key LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.sequence_number LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.lastupdate LIKE LOWER(CONCAT('%',:search, '%'))"
    		+ " OR tc_drivers.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_geofences.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_users.name LIKE LOWER(CONCAT('%',:search, '%')) ) "
    		+ " GROUP BY tc_devices.id,tc_drivers.id,tc_users.id "),
	
	@NamedNativeQuery(name="getDevicesListByIdsAll", 
	resultSetMapping="DevicesListSFDA", 
	query=" SELECT tc_devices.id as id ,tc_devices.name as deviceName,tc_devices.simcardNumber as simcardNumber, tc_devices.uniqueid as uniqueId,"
			+ " tc_devices.sequence_number as sequenceNumber,tc_devices.delete_date as delete_date ,tc_devices.lastupdate as lastUpdate "
			+ " ,tc_devices.reference_key as referenceKey , tc_devices.expired as expired , "
			+ " tc_devices.start_date as startDate ,tc_devices.end_date as endDate , "
			+ " tc_drivers.name as driverName,tc_users.name as companyName,tc_users.id as companyId ,GROUP_CONCAT(tc_geofences.name )AS geofenceName"
			+ " ,tc_devices.create_date as create_date  ,tc_devices.delete_from_elm_date as delete_date_elm  ,tc_devices.update_date_in_elm as update_date_elm , DATEDIFF(DATE_ADD(tc_devices.update_date_in_elm, INTERVAL 275 DAY),CURDATE()) as leftDays  FROM tc_devices LEFT JOIN  tc_device_driver ON tc_devices.id=tc_device_driver.deviceid"
			+ " LEFT JOIN  tc_drivers ON tc_drivers.id=tc_device_driver.driverid and tc_drivers.delete_date is null" 
			+ " LEFT JOIN  tc_device_geofence ON tc_devices.id=tc_device_geofence.deviceid"
			+ " LEFT JOIN  tc_geofences ON tc_geofences.id=tc_device_geofence.geofenceid and tc_geofences.delete_date"
			+ " is null INNER JOIN tc_user_device ON tc_user_device.deviceid = tc_devices.id "
			+ " LEFT JOIN tc_users ON tc_user_device.userid = tc_users.id" 
			+ " where tc_devices.id IN(:deviceIds) AND ((TIMESTAMPDIFF(day ,CURDATE(),tc_devices.end_date) >=0)  OR :isAdmin) "
			+ " AND ( tc_devices.simcardNumber LIKE LOWER(CONCAT('%',:search, '%')) OR   tc_devices.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.uniqueid LIKE LOWER(CONCAT('%',:search, '%')) "
			+ " OR tc_devices.reference_key LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.sequence_number LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.lastupdate LIKE LOWER(CONCAT('%',:search, '%'))"
			+ " OR tc_drivers.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_geofences.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_users.name LIKE LOWER(CONCAT('%',:search, '%')) ) "
			+ " GROUP BY tc_devices.id,tc_drivers.id,tc_users.id LIMIT :offset,10"),
	
	@NamedNativeQuery(name="getDevicesListByIdsAllExport", 
	resultSetMapping="DevicesListSFDA", 
	query=" SELECT tc_devices.id as id ,tc_devices.name as deviceName,tc_devices.simcardNumber as simcardNumber, tc_devices.uniqueid as uniqueId,"
			+ " tc_devices.sequence_number as sequenceNumber,tc_devices.delete_date as delete_date ,tc_devices.lastupdate as lastUpdate "
			+ " ,tc_devices.reference_key as referenceKey , tc_devices.expired as expired , "
			+ " tc_devices.start_date as startDate ,tc_devices.end_date as endDate , "
			+ " tc_drivers.name as driverName,tc_users.name as companyName,tc_users.id as companyId ,GROUP_CONCAT(tc_geofences.name )AS geofenceName"
			+ " ,tc_devices.create_date as create_date ,tc_devices.delete_from_elm_date as delete_date_elm  ,tc_devices.update_date_in_elm as update_date_elm, DATEDIFF(DATE_ADD(tc_devices.update_date_in_elm, INTERVAL 275 DAY),CURDATE()) as leftDays  FROM tc_devices LEFT JOIN  tc_device_driver ON tc_devices.id=tc_device_driver.deviceid"
			+ " LEFT JOIN  tc_drivers ON tc_drivers.id=tc_device_driver.driverid and tc_drivers.delete_date is null" 
			+ " LEFT JOIN  tc_device_geofence ON tc_devices.id=tc_device_geofence.deviceid" 
			+ " LEFT JOIN  tc_geofences ON tc_geofences.id=tc_device_geofence.geofenceid and tc_geofences.delete_date"
			+ " is null INNER JOIN tc_user_device ON tc_user_device.deviceid = tc_devices.id "
			+ " LEFT JOIN tc_users ON tc_user_device.userid = tc_users.id" 
			+ " where tc_devices.id IN(:deviceIds) AND ((TIMESTAMPDIFF(day ,CURDATE(),tc_devices.end_date) >=0)  OR :isAdmin) "
			+ " AND ( tc_devices.simcardNumber LIKE LOWER(CONCAT('%',:search, '%')) OR   tc_devices.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.uniqueid LIKE LOWER(CONCAT('%',:search, '%')) "
			+ " OR tc_devices.reference_key LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.sequence_number LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.lastupdate LIKE LOWER(CONCAT('%',:search, '%'))"
			+ " OR tc_drivers.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_geofences.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_users.name LIKE LOWER(CONCAT('%',:search, '%')) ) "
			+ " GROUP BY tc_devices.id,tc_drivers.id,tc_users.id "),

	@NamedNativeQuery(name="getDevicesListDeactive", 
	     resultSetMapping="DevicesListSFDA", 
	     query=" SELECT tc_devices.id as id ,tc_devices.name as deviceName,tc_devices.simcardNumber as simcardNumber, tc_devices.uniqueid as uniqueId,"
	     		+ " tc_devices.sequence_number as sequenceNumber,tc_devices.delete_date as delete_date ,tc_devices.lastupdate as lastUpdate "
	     		+ " ,tc_devices.reference_key as referenceKey, tc_devices.expired as expired, "
				 + " tc_devices.start_date as startDate ,tc_devices.end_date as endDate , "
	     		+ " tc_drivers.name as driverName,tc_users.name as companyName,tc_users.id as companyId ,GROUP_CONCAT(tc_geofences.name )AS geofenceName"
	     		+ " ,tc_devices.create_date as create_date ,tc_devices.delete_from_elm_date as delete_date_elm  ,tc_devices.update_date_in_elm as update_date_elm , DATEDIFF(DATE_ADD(tc_devices.update_date_in_elm, INTERVAL 275 DAY),CURDATE()) as leftDays  FROM tc_devices LEFT JOIN  tc_device_driver ON tc_devices.id=tc_device_driver.deviceid"
	     		+ " LEFT JOIN  tc_drivers ON tc_drivers.id=tc_device_driver.driverid and tc_drivers.delete_date is null" 
	     		+ " LEFT JOIN  tc_device_geofence ON tc_devices.id=tc_device_geofence.deviceid" 
	     		+ " LEFT JOIN  tc_geofences ON tc_geofences.id=tc_device_geofence.geofenceid and tc_geofences.delete_date"
	     		+ " is null INNER JOIN tc_user_device ON tc_user_device.deviceid = tc_devices.id "
	     		+ " LEFT JOIN tc_users ON tc_user_device.userid = tc_users.id" 
	     		+ " where tc_user_device.userid IN(:userIds) and tc_devices.delete_date is not null AND ((TIMESTAMPDIFF(day ,CURDATE(),tc_devices.end_date) >=0)  OR :isAdmin)  "
	     		+ " AND ( tc_devices.simcardNumber LIKE LOWER(CONCAT('%',:search, '%')) OR   tc_devices.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.uniqueid LIKE LOWER(CONCAT('%',:search, '%')) "
	     		+ " OR tc_devices.reference_key LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.sequence_number LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.lastupdate LIKE LOWER(CONCAT('%',:search, '%'))"
	     		+ " OR tc_drivers.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_geofences.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_users.name LIKE LOWER(CONCAT('%',:search, '%')) ) "
	     		+ " GROUP BY tc_devices.id,tc_drivers.id,tc_users.id LIMIT :offset,10"),
	
	@NamedNativeQuery(name="getDevicesListDeactiveExport", 
    resultSetMapping="DevicesListSFDA", 
    query=" SELECT tc_devices.id as id ,tc_devices.name as deviceName,tc_devices.simcardNumber as simcardNumber, tc_devices.uniqueid as uniqueId,"
    		+ " tc_devices.sequence_number as sequenceNumber,tc_devices.delete_date as delete_date ,tc_devices.lastupdate as lastUpdate "
    		+ " ,tc_devices.reference_key as referenceKey, tc_devices.expired as expired, "
			+ " tc_devices.start_date as startDate ,tc_devices.end_date as endDate , "
    		+ " tc_drivers.name as driverName,tc_users.name as companyName,tc_users.id as companyId ,GROUP_CONCAT(tc_geofences.name )AS geofenceName"
    		+ " ,tc_devices.create_date as create_date ,tc_devices.delete_from_elm_date as delete_date_elm  ,tc_devices.update_date_in_elm as update_date_elm , DATEDIFF(DATE_ADD(tc_devices.update_date_in_elm, INTERVAL 275 DAY),CURDATE()) as leftDays  FROM tc_devices LEFT JOIN  tc_device_driver ON tc_devices.id=tc_device_driver.deviceid"
    		+ " LEFT JOIN  tc_drivers ON tc_drivers.id=tc_device_driver.driverid and tc_drivers.delete_date is null" 
    		+ " LEFT JOIN  tc_device_geofence ON tc_devices.id=tc_device_geofence.deviceid" 
    		+ " LEFT JOIN  tc_geofences ON tc_geofences.id=tc_device_geofence.geofenceid and tc_geofences.delete_date"
    		+ " is null INNER JOIN tc_user_device ON tc_user_device.deviceid = tc_devices.id "
    		+ " LEFT JOIN tc_users ON tc_user_device.userid = tc_users.id" 
    		+ " where tc_user_device.userid IN(:userIds) and tc_devices.delete_date is not null  AND ((TIMESTAMPDIFF(day ,CURDATE(),tc_devices.end_date) >=0)  OR :isAdmin)   "
    		+ " AND ( tc_devices.simcardNumber LIKE LOWER(CONCAT('%',:search, '%')) OR   tc_devices.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.uniqueid LIKE LOWER(CONCAT('%',:search, '%')) "
    		+ " OR tc_devices.reference_key LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.sequence_number LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.lastupdate LIKE LOWER(CONCAT('%',:search, '%'))"
    		+ " OR tc_drivers.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_geofences.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_users.name LIKE LOWER(CONCAT('%',:search, '%')) ) "
    		+ " GROUP BY tc_devices.id,tc_drivers.id,tc_users.id "),
	
	@NamedNativeQuery(name="getDevicesListByIdsDeactive", 
	resultSetMapping="DevicesListSFDA", 
	query=" SELECT tc_devices.id as id ,tc_devices.name as deviceName, tc_devices.simcardNumber as simcardNumber,tc_devices.uniqueid as uniqueId,"
			+ " tc_devices.sequence_number as sequenceNumber,tc_devices.delete_date as delete_date ,tc_devices.lastupdate as lastUpdate "
			+ " ,tc_devices.reference_key as referenceKey , tc_devices.expired as expired , "
			+ " tc_devices.start_date as startDate ,tc_devices.end_date as endDate , "
			+ " tc_drivers.name as driverName,tc_users.name as companyName,tc_users.id as companyId ,GROUP_CONCAT(tc_geofences.name )AS geofenceName"
			+ " ,tc_devices.create_date as create_date ,tc_devices.delete_from_elm_date as delete_date_elm  ,tc_devices.update_date_in_elm as update_date_elm , DATEDIFF(DATE_ADD(tc_devices.update_date_in_elm, INTERVAL 275 DAY),CURDATE()) as leftDays  FROM tc_devices LEFT JOIN  tc_device_driver ON tc_devices.id=tc_device_driver.deviceid"
			+ " LEFT JOIN  tc_drivers ON tc_drivers.id=tc_device_driver.driverid and tc_drivers.delete_date is null" 
			+ " LEFT JOIN  tc_device_geofence ON tc_devices.id=tc_device_geofence.deviceid" 
			+ " LEFT JOIN  tc_geofences ON tc_geofences.id=tc_device_geofence.geofenceid and tc_geofences.delete_date"
			+ " is null INNER JOIN tc_user_device ON tc_user_device.deviceid = tc_devices.id "
			+ " LEFT JOIN tc_users ON tc_user_device.userid = tc_users.id" 
			+ " where tc_devices.id IN(:deviceIds) and tc_devices.delete_date is not null  AND ((TIMESTAMPDIFF(day ,CURDATE(),tc_devices.end_date) >=0)  OR :isAdmin)    "
			+ " AND ( tc_devices.simcardNumber LIKE LOWER(CONCAT('%',:search, '%')) OR  tc_devices.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.uniqueid LIKE LOWER(CONCAT('%',:search, '%')) "
			+ " OR tc_devices.reference_key LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.sequence_number LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.lastupdate LIKE LOWER(CONCAT('%',:search, '%'))"
			+ " OR tc_drivers.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_geofences.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_users.name LIKE LOWER(CONCAT('%',:search, '%')) ) "
			+ " GROUP BY tc_devices.id,tc_drivers.id,tc_users.id LIMIT :offset,10"),
	
	@NamedNativeQuery(name="getDevicesListByIdsDeactiveExport", 
	resultSetMapping="DevicesListSFDA", 
	query=" SELECT tc_devices.id as id ,tc_devices.name as deviceName,tc_devices.simcardNumber as simcardNumber, tc_devices.uniqueid as uniqueId,"
			+ " tc_devices.sequence_number as sequenceNumber,tc_devices.delete_date as delete_date ,tc_devices.lastupdate as lastUpdate "
			+ " ,tc_devices.reference_key as referenceKey , tc_devices.expired as expired , "
			+ " tc_devices.start_date as startDate ,tc_devices.end_date as endDate , "
			+ " tc_drivers.name as driverName,tc_users.name as companyName,tc_users.id as companyId ,GROUP_CONCAT(tc_geofences.name )AS geofenceName"
			+ " ,tc_devices.create_date as create_date ,tc_devices.delete_from_elm_date as delete_date_elm  ,tc_devices.update_date_in_elm as update_date_elm , DATEDIFF(DATE_ADD(tc_devices.update_date_in_elm, INTERVAL 275 DAY),CURDATE()) as leftDays  "
			+ " FROM tc_devices LEFT JOIN  tc_device_driver ON tc_devices.id=tc_device_driver.deviceid"
			+ " LEFT JOIN  tc_drivers ON tc_drivers.id=tc_device_driver.driverid and tc_drivers.delete_date is null" 
			+ " LEFT JOIN  tc_device_geofence ON tc_devices.id=tc_device_geofence.deviceid" 
			+ " LEFT JOIN  tc_geofences ON tc_geofences.id=tc_device_geofence.geofenceid and tc_geofences.delete_date"
			+ " is null INNER JOIN tc_user_device ON tc_user_device.deviceid = tc_devices.id "
			+ " LEFT JOIN tc_users ON tc_user_device.userid = tc_users.id" 
			+ " where tc_devices.id IN(:deviceIds) and tc_devices.delete_date is not null  AND ((TIMESTAMPDIFF(day ,CURDATE(),tc_devices.end_date) >=0)  OR :isAdmin) "
			+ " AND ( tc_devices.simcardNumber LIKE LOWER(CONCAT('%',:search, '%')) OR  tc_devices.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.uniqueid LIKE LOWER(CONCAT('%',:search, '%')) "
			+ " OR tc_devices.reference_key LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.sequence_number LIKE LOWER(CONCAT('%',:search, '%')) OR tc_devices.lastupdate LIKE LOWER(CONCAT('%',:search, '%'))"
			+ " OR tc_drivers.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_geofences.name LIKE LOWER(CONCAT('%',:search, '%')) OR tc_users.name LIKE LOWER(CONCAT('%',:search, '%')) ) "
			+ " GROUP BY tc_devices.id,tc_drivers.id,tc_users.id ")
})

/**
 * child from Device to add activity and storingCategory to device and SFDA for column DTYPE 
 * @author fuinco
 *
 */
@Entity
@DiscriminatorValue("null")
public class DeviceSFDA extends Device{

}
