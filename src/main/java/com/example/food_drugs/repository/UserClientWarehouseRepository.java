package com.example.food_drugs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;
import com.example.food_drugs.entity.userClientWarehouse;;

/**
 * Queries related to type 3 and 4 for tc_user_client_warehouse
 * @author fuinco
 *
 */
@Component
public interface UserClientWarehouseRepository extends JpaRepository<userClientWarehouse, Long>, QueryDslPredicateExecutor<userClientWarehouse>{

	@Query(value = "select tc_user_client_warehouse.warehouseid from tc_user_client_warehouse where tc_user_client_warehouse.userid=:userId", nativeQuery = true)
	public List<Long> getWarehouseIds(@Param("userId") Long userId);

	@Query(value = "select tc_user_client_warehouse.warehouseid from tc_user_client_warehouse "
			+ " where tc_user_client_warehouse.userid=:userId and tc_user_client_warehouse.warehouseid=:warehouseId  ", nativeQuery = true)
	public List<Long> getWarehouse(@Param("userId") Long userId,@Param("warehouseId") Long warehouseId);
	
	@Query(value = "select tc_user_client_warehouse.warehouseid from tc_user_client_warehouse where tc_user_client_warehouse.userid=:userId", nativeQuery = true)
	public List<Long> getWarhouseIds(@Param("userId") Long userId);

	@Query(value = "select tc_warehouses.id,tc_warehouses.name from tc_warehouses "
			+ " INNER JOIN tc_user_client_warehouse ON tc_user_client_warehouse.warehouseid=tc_warehouses.id "
			+ "  where tc_user_client_warehouse.userid=:userId ", nativeQuery = true)
	public List<DriverSelect> getWarehousesOfUserList(@Param("userId") Long userId);
	
	@Query(value = "select * from tc_user_client_warehouse where tc_user_client_warehouse.warehouseid IN (:warehouseIds) and tc_user_client_warehouse.userid !=:userId", nativeQuery = true)
	public List<userClientWarehouse> getWarehouseByWarIds(@Param("warehouseIds") Long[] warehouseIds ,@Param("userId") Long userId);

	@Transactional
    @Modifying
	@Query(value = "Delete from tc_user_client_warehouse where tc_user_client_warehouse.userid=:userId", nativeQuery = true)
	public void deleteWarehousesByUserId(@Param("userId") Long userId);
	
	@Query(value = "select * from tc_user_client_warehouse where tc_user_client_warehouse.userid=:userId", nativeQuery = true)
	public List<userClientWarehouse> getWarehousesOfUser(@Param("userId") Long userId);
	
	@Transactional
    @Modifying
	@Query(value = "Delete from tc_user_client_warehouse where tc_user_client_warehouse.warehouseid=:warehouseId", nativeQuery = true)
	public void deleteWarehouseById(@Param("warehouseId") Long warehouseId);
	
	@Query(value = "select tc_user_client_warehouse.warehouseid from tc_user_client_warehouse where tc_user_client_warehouse.warehouseid=:warehouseId", nativeQuery = true)
	public List<Long> getWarehousesToDelete(@Param("warehouseId") Long warehouseId);
}
