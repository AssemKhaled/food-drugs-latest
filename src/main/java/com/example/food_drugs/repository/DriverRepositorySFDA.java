package com.example.food_drugs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.example.examplequerydslspringdatajpamaven.entity.CustomDriverList;
import com.example.food_drugs.entity.DriverSFDA;

/**
 * Queries on table tc_drivers with SFDA
 * @author fuinco
 *
 */
@Component
public interface DriverRepositorySFDA extends  JpaRepository<DriverSFDA, Long>, QueryDslPredicateExecutor<DriverSFDA> {

	@Query(nativeQuery = true, name = "getDriverListAll")
	public List<CustomDriverList> getAllDriversCustomAll(@Param("userIds") List<Long> userIds,@Param("offset") int offset,@Param("search") String search);

	@Query(nativeQuery = true, name = "getDriverListAllExport")
	public List<CustomDriverList> getAllDriversCustomAllExport(@Param("userIds") List<Long> userIds,@Param("search") String search);
	

	@Query(nativeQuery = true, name = "getDriverListByIdsDeactive")
	public List<CustomDriverList> getAllDriversCustomByIdsDeactive(@Param("driverIds") List<Long> driverIds,@Param("offset") int offset,@Param("search") String search);

	@Query(nativeQuery = true, name = "getDriverListByIdsDeactiveExport")
	public List<CustomDriverList> getAllDriversCustomByIdsDeactiveExport(@Param("driverIds") List<Long> driverIds,@Param("search") String search);
	
	@Query(value = "SELECT count(*) FROM tc_drivers "
			+ " INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id " +
			" LEFT JOIN tc_users ON tc_user_driver.userid = tc_users.id " 
			+ " WHERE tc_user_driver.userid IN(:userIds) " 
			+ " and ((tc_users.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.uniqueid Like LOWER(CONCAT('%',:search, '%'))) OR "
			+ " (tc_drivers.mobile_num Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.birth_date Like LOWER(CONCAT('%',:search, '%')))) " , nativeQuery = true)
	public Integer getAllDriversSizeAll(@Param("userIds") List<Long> userIds,@Param("search") String search);

	
	@Query(value = "SELECT count(*) FROM tc_drivers "
			+ " INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id " +
			" LEFT JOIN tc_users ON tc_user_driver.userid = tc_users.id " 
			+ " WHERE tc_drivers.id IN(:driverIds) and tc_drivers.delete_date is not null" 
			+ " and ((tc_users.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.uniqueid Like LOWER(CONCAT('%',:search, '%'))) OR "
			+ " (tc_drivers.mobile_num Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.birth_date Like LOWER(CONCAT('%',:search, '%')))) " , nativeQuery = true)
	public Integer getAllDriversSizeByIdsDeactive(@Param("driverIds") List<Long> driverIds,@Param("search") String search);
	
	
	@Query(nativeQuery = true, name = "getDriverListDeactive")
	public List<CustomDriverList> getAllDriversCustomDeactive(@Param("userIds") List<Long> userIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(nativeQuery = true, name = "getDriverListDeactiveExport")
	public List<CustomDriverList> getAllDriversCustomDeactiveExport(@Param("userIds") List<Long> userIds,@Param("search") String search);
	
	@Query(nativeQuery = true, name = "getDriverListByIdsAll")
	public List<CustomDriverList> getAllDriversCustomByIdsAll(@Param("driverIds") List<Long> driverIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(nativeQuery = true, name = "getDriverListByIdsAllExport")
	public List<CustomDriverList> getAllDriversCustomByIdsAllExport(@Param("driverIds") List<Long> driverIds,@Param("search") String search);
	
	
	@Query(value = "SELECT count(*) FROM tc_drivers "
			+ " INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id " +
			" LEFT JOIN tc_users ON tc_user_driver.userid = tc_users.id " 
			+ " WHERE tc_user_driver.userid IN(:userIds) and tc_drivers.delete_date is not null" 
			+ " and ((tc_users.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.uniqueid Like LOWER(CONCAT('%',:search, '%'))) OR "
			+ " (tc_drivers.mobile_num Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.birth_date Like LOWER(CONCAT('%',:search, '%')))) " , nativeQuery = true)
	public Integer getAllDriversSizeDeactive(@Param("userIds") List<Long> userIds,@Param("search") String search);

	
	@Query(value = "SELECT count(*) FROM tc_drivers "
			+ " INNER JOIN tc_user_driver ON tc_user_driver.driverid = tc_drivers.id " +
			" LEFT JOIN tc_users ON tc_user_driver.userid = tc_users.id " 
			+ " WHERE tc_drivers.id IN(:driverIds) " 
			+ " and ((tc_users.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.name Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.uniqueid Like LOWER(CONCAT('%',:search, '%'))) OR "
			+ " (tc_drivers.mobile_num Like LOWER(CONCAT('%',:search, '%'))) OR (tc_drivers.birth_date Like LOWER(CONCAT('%',:search, '%')))) " , nativeQuery = true)
	public Integer getAllDriversSizeByIdsAll(@Param("driverIds") List<Long> driverIds,@Param("search") String search);
	
}
