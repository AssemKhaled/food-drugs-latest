package com.example.food_drugs.entity;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;

import com.example.examplequerydslspringdatajpamaven.entity.CustomDriverList;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;


@SqlResultSetMappings({
	
	@SqlResultSetMapping(
        name="DriverListSFDA",
        classes={
           @ConstructorResult(
                targetClass=CustomDriverList.class,
                  columns={
                     @ColumnResult(name="id",type=Long.class),
                     @ColumnResult(name="name",type=String.class),
                     @ColumnResult(name="uniqueid",type=String.class),
                     @ColumnResult(name="attributes",type=String.class),
                     @ColumnResult(name="mobile_num",type=String.class),
                     @ColumnResult(name="birth_date",type=String.class),
                     @ColumnResult(name="email",type=String.class),
                     @ColumnResult(name="reference_key",type=String.class),
                     @ColumnResult(name="is_deleted",type=String.class),
                     @ColumnResult(name="delete_date",type=String.class),
                     @ColumnResult(name="reject_reason",type=String.class),
                     @ColumnResult(name="date_type",type=String.class),
                     @ColumnResult(name="is_valid",type=String.class),
                     @ColumnResult(name="photo",type=String.class),
                     @ColumnResult(name="companyName",type=String.class),
                     @ColumnResult(name="create_date_elm",type=String.class),
                     @ColumnResult(name="delete_date_elm",type=String.class),
                     @ColumnResult(name="update_date_elm",type=String.class)
                     }
           )
        }
	)
	
	
})
@NamedNativeQueries({
	@NamedNativeQuery(name="getDriverListAll", 
			resultSetMapping="DriverListSFDA", 
			query="SELECT tc_drivers.id as id,tc_drivers.name as name ,tc_drivers.uniqueid as uniqueid,"
					+ " tc_drivers.attributes as attributes," + 
					" tc_drivers.mobile_num as mobile_num,tc_drivers.birth_date as birth_date,"
					+ " tc_drivers.email as email,tc_drivers.reference_key as reference_key, " + 
					" tc_drivers.is_deleted as is_deleted,tc_drivers.delete_date as delete_date,"
					+ " tc_drivers.reject_reason as reject_reason,tc_drivers.date_type as date_type,tc_drivers.is_valid as is_valid, " + 
					" tc_drivers.photo as photo,tc_users.name as companyName  "
					+" ,tc_drivers.regestration_to_elm_date as create_date_elm , tc_drivers.delete_from_elm_date as delete_date_elm , tc_drivers.update_date_in_elm as update_date_elm "
					+ " FROM tc_drivers "
					+ " INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id " +
					" LEFT JOIN tc_users ON tc_user_driver.userid = tc_users.id " + 
					" WHERE tc_user_driver.userid IN(:userIds)  " 
					+ " and ((tc_users.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.uniqueid Like LOWER(CONCAT('%',:search, '%'))) OR "
					+ " (tc_drivers.mobile_num Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.birth_date Like LOWER(CONCAT('%',:search, '%')))) " + 
					" LIMIT :offset,10"),
	
	@NamedNativeQuery(name="getDriverListAllExport", 
	resultSetMapping="DriverListSFDA", 
	query="SELECT tc_drivers.id as id,tc_drivers.name as name ,tc_drivers.uniqueid as uniqueid,"
			+ " tc_drivers.attributes as attributes," + 
			" tc_drivers.mobile_num as mobile_num,tc_drivers.birth_date as birth_date,"
			+ " tc_drivers.email as email,tc_drivers.reference_key as reference_key, " + 
			" tc_drivers.is_deleted as is_deleted,tc_drivers.delete_date as delete_date,"
			+ " tc_drivers.reject_reason as reject_reason,tc_drivers.date_type as date_type,tc_drivers.is_valid as is_valid, " + 
			" tc_drivers.photo as photo,tc_users.name as companyName  "
			+" ,tc_drivers.regestration_to_elm_date as create_date_elm , tc_drivers.delete_from_elm_date as delete_date_elm , tc_drivers.update_date_in_elm as update_date_elm "
			+ " FROM tc_drivers "
			+ " INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id " +
			" LEFT JOIN tc_users ON tc_user_driver.userid = tc_users.id " + 
			" WHERE tc_user_driver.userid IN(:userIds)  " 
			+ " and ((tc_users.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.uniqueid Like LOWER(CONCAT('%',:search, '%'))) OR "
			+ " (tc_drivers.mobile_num Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.birth_date Like LOWER(CONCAT('%',:search, '%')))) " ),
	
	
	@NamedNativeQuery(name="getDriverListByIdsAll", 
	resultSetMapping="DriverListSFDA", 
	query="SELECT tc_drivers.id as id,tc_drivers.name as name ,tc_drivers.uniqueid as uniqueid,"
			+ " tc_drivers.attributes as attributes," + 
			" tc_drivers.mobile_num as mobile_num,tc_drivers.birth_date as birth_date,"
			+ " tc_drivers.email as email,tc_drivers.reference_key as reference_key, " + 
			" tc_drivers.is_deleted as is_deleted,tc_drivers.delete_date as delete_date,"
			+ " tc_drivers.reject_reason as reject_reason,tc_drivers.date_type as date_type,tc_drivers.is_valid as is_valid, " + 
			" tc_drivers.photo as photo,tc_users.name as companyName  "
			+" ,tc_drivers.regestration_to_elm_date as create_date_elm , tc_drivers.delete_from_elm_date as delete_date_elm , tc_drivers.update_date_in_elm as update_date_elm "
			+ " FROM tc_drivers "
			+ " INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id " +
			" LEFT JOIN tc_users ON tc_user_driver.userid = tc_users.id " + 
			" WHERE tc_drivers.id IN(:driverIds) " 
			+ " and ((tc_users.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.uniqueid Like LOWER(CONCAT('%',:search, '%'))) OR "
			+ " (tc_drivers.mobile_num Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.birth_date Like LOWER(CONCAT('%',:search, '%')))) " + 
			" LIMIT :offset,10"),
	
	@NamedNativeQuery(name="getDriverListByIdsAllExport", 
	resultSetMapping="DriverListSFDA", 
	query="SELECT tc_drivers.id as id,tc_drivers.name as name ,tc_drivers.uniqueid as uniqueid,"
			+ " tc_drivers.attributes as attributes," + 
			" tc_drivers.mobile_num as mobile_num,tc_drivers.birth_date as birth_date,"
			+ " tc_drivers.email as email,tc_drivers.reference_key as reference_key, " + 
			" tc_drivers.is_deleted as is_deleted,tc_drivers.delete_date as delete_date,"
			+ " tc_drivers.reject_reason as reject_reason,tc_drivers.date_type as date_type,tc_drivers.is_valid as is_valid, " + 
			" tc_drivers.photo as photo,tc_users.name as companyName  "
			+" ,tc_drivers.regestration_to_elm_date as create_date_elm , tc_drivers.delete_from_elm_date as delete_date_elm , tc_drivers.update_date_in_elm as update_date_elm "
			+ " FROM tc_drivers "
			+ " INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id " +
			" LEFT JOIN tc_users ON tc_user_driver.userid = tc_users.id " + 
			" WHERE tc_drivers.id IN(:driverIds) " 
			+ " and ((tc_users.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.uniqueid Like LOWER(CONCAT('%',:search, '%'))) OR "
			+ " (tc_drivers.mobile_num Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.birth_date Like LOWER(CONCAT('%',:search, '%')))) " ),
	
	
	@NamedNativeQuery(name="getDriverListDeactive", 
	resultSetMapping="DriverListSFDA", 
	query="SELECT tc_drivers.id as id,tc_drivers.name as name ,tc_drivers.uniqueid as uniqueid,"
			+ " tc_drivers.attributes as attributes," + 
			" tc_drivers.mobile_num as mobile_num,tc_drivers.birth_date as birth_date,"
			+ " tc_drivers.email as email,tc_drivers.reference_key as reference_key, " + 
			" tc_drivers.is_deleted as is_deleted,tc_drivers.delete_date as delete_date,"
			+ " tc_drivers.reject_reason as reject_reason,tc_drivers.date_type as date_type,tc_drivers.is_valid as is_valid, " + 
			" tc_drivers.photo as photo,tc_users.name as companyName  "
			+" ,tc_drivers.regestration_to_elm_date as create_date_elm , tc_drivers.delete_from_elm_date as delete_date_elm , tc_drivers.update_date_in_elm as update_date_elm "
			+ " FROM tc_drivers "
			+ " INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id " +
			" LEFT JOIN tc_users ON tc_user_driver.userid = tc_users.id " + 
			" WHERE tc_user_driver.userid IN(:userIds) and tc_drivers.delete_date is not null " 
			+ " and ((tc_users.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.uniqueid Like LOWER(CONCAT('%',:search, '%'))) OR "
			+ " (tc_drivers.mobile_num Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.birth_date Like LOWER(CONCAT('%',:search, '%')))) " + 
			" LIMIT :offset,10"),
	
	@NamedNativeQuery(name="getDriverListDeactiveExport", 
	resultSetMapping="DriverListSFDA", 
	query="SELECT tc_drivers.id as id,tc_drivers.name as name ,tc_drivers.uniqueid as uniqueid,"
			+ " tc_drivers.attributes as attributes," + 
			" tc_drivers.mobile_num as mobile_num,tc_drivers.birth_date as birth_date,"
			+ " tc_drivers.email as email,tc_drivers.reference_key as reference_key, " + 
			" tc_drivers.is_deleted as is_deleted,tc_drivers.delete_date as delete_date,"
			+ " tc_drivers.reject_reason as reject_reason,tc_drivers.date_type as date_type,tc_drivers.is_valid as is_valid, " + 
			" tc_drivers.photo as photo,tc_users.name as companyName  "
			+" ,tc_drivers.regestration_to_elm_date as create_date_elm , tc_drivers.delete_from_elm_date as delete_date_elm , tc_drivers.update_date_in_elm as update_date_elm "
			+ " FROM tc_drivers "
			+ " INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id " +
			" LEFT JOIN tc_users ON tc_user_driver.userid = tc_users.id " + 
			" WHERE tc_user_driver.userid IN(:userIds) and tc_drivers.delete_date is not null " 
			+ " and ((tc_users.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.uniqueid Like LOWER(CONCAT('%',:search, '%'))) OR "
			+ " (tc_drivers.mobile_num Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.birth_date Like LOWER(CONCAT('%',:search, '%')))) " ),
	
	@NamedNativeQuery(name="getDriverListByIdsDeactive", 
	resultSetMapping="DriverListSFDA", 
	query="SELECT tc_drivers.id as id,tc_drivers.name as name ,tc_drivers.uniqueid as uniqueid,"
		+ " tc_drivers.attributes as attributes," + 
		" tc_drivers.mobile_num as mobile_num,tc_drivers.birth_date as birth_date,"
		+ " tc_drivers.email as email,tc_drivers.reference_key as reference_key, " + 
		" tc_drivers.is_deleted as is_deleted,tc_drivers.delete_date as delete_date,"
		+ " tc_drivers.reject_reason as reject_reason,tc_drivers.date_type as date_type,tc_drivers.is_valid as is_valid, " + 
		" tc_drivers.photo as photo,tc_users.name as companyName  "
		+" ,tc_drivers.regestration_to_elm_date as create_date_elm , tc_drivers.delete_from_elm_date as delete_date_elm , tc_drivers.update_date_in_elm as update_date_elm "
		+ " FROM tc_drivers "
		+ " INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id " +
		" LEFT JOIN tc_users ON tc_user_driver.userid = tc_users.id " + 
		" WHERE tc_drivers.id IN(:driverIds) and tc_drivers.delete_date is not null " 
		+ " and ((tc_users.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.uniqueid Like LOWER(CONCAT('%',:search, '%'))) OR "
		+ " (tc_drivers.mobile_num Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.birth_date Like LOWER(CONCAT('%',:search, '%')))) " + 
		" LIMIT :offset,10"),
	
	@NamedNativeQuery(name="getDriverListByIdsDeactiveExport", 
	resultSetMapping="DriverListSFDA", 
	query="SELECT tc_drivers.id as id,tc_drivers.name as name ,tc_drivers.uniqueid as uniqueid,"
		+ " tc_drivers.attributes as attributes," + 
		" tc_drivers.mobile_num as mobile_num,tc_drivers.birth_date as birth_date,"
		+ " tc_drivers.email as email,tc_drivers.reference_key as reference_key, " + 
		" tc_drivers.is_deleted as is_deleted,tc_drivers.delete_date as delete_date,"
		+ " tc_drivers.reject_reason as reject_reason,tc_drivers.date_type as date_type,tc_drivers.is_valid as is_valid, " + 
		" tc_drivers.photo as photo,tc_users.name as companyName  "
		+" ,tc_drivers.regestration_to_elm_date as create_date_elm , tc_drivers.delete_from_elm_date as delete_date_elm , tc_drivers.update_date_in_elm as update_date_elm "
		+ " FROM tc_drivers "
		+ " INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id " +
		" LEFT JOIN tc_users ON tc_user_driver.userid = tc_users.id " + 
		" WHERE tc_drivers.id IN(:driverIds) and tc_drivers.delete_date is not null " 
		+ " and ((tc_users.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.uniqueid Like LOWER(CONCAT('%',:search, '%'))) OR "
		+ " (tc_drivers.mobile_num Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.birth_date Like LOWER(CONCAT('%',:search, '%'))) ) " )

	
})

@Entity
@DiscriminatorValue("null")
public class DriverSFDA extends Driver{

}
