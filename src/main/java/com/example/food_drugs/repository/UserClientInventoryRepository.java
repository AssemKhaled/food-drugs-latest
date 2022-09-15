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
import com.example.food_drugs.entity.userClientInventory;
/**
 * Queries related to type 3 and 4 for tc_user_client_inventory
 * @author fuinco
 *
 */
@Component
public interface UserClientInventoryRepository extends JpaRepository<userClientInventory, Long>, QueryDslPredicateExecutor<userClientInventory>{

	@Query(value = "select tc_user_client_inventory.inventoryid from tc_user_client_inventory "
			+ " where tc_user_client_inventory.userid=:userId and tc_user_client_inventory.inventoryid=:inventoryId  ", nativeQuery = true)
	public List<Long> getInventory(@Param("userId") Long userId,@Param("inventoryId") Long inventoryId);
	
	@Query(value = "select tc_user_client_inventory.inventoryid from tc_user_client_inventory where tc_user_client_inventory.userid=:userId", nativeQuery = true)
	public List<Long> getInventoryIds(@Param("userId") Long userId);

	@Query(value = "select tc_inventories.id,tc_inventories.name from tc_inventories "
			+ " INNER JOIN tc_user_client_inventory ON tc_user_client_inventory.inventoryid=tc_inventories.id "
			+ "  where tc_user_client_inventory.userid=:userId ", nativeQuery = true)
	public List<DriverSelect> getInventoriesOfUserList(@Param("userId") Long userId);
	
	@Query(value = "select * from tc_user_client_inventory where tc_user_client_inventory.inventoryid IN (:inventoryIds) and tc_user_client_inventory.userid !=:userId", nativeQuery = true)
	public List<userClientInventory> getInventoryByInvIds(@Param("inventoryIds") Long[] inventoryIds ,@Param("userId") Long userId);

	@Transactional
    @Modifying
	@Query(value = "Delete from tc_user_client_inventory where tc_user_client_inventory.userid=:userId", nativeQuery = true)
	public void deleteInventoriesByUserId(@Param("userId") Long userId);
	
	@Query(value = "select * from tc_user_client_inventory where tc_user_client_inventory.userid=:userId", nativeQuery = true)
	public List<userClientInventory> getInventoriesOfUser(@Param("userId") Long userId);
	
	@Transactional
    @Modifying
	@Query(value = "Delete from tc_user_client_inventory where tc_user_client_inventory.inventoryid=:inventoryId", nativeQuery = true)
	public void deleteInventoryById(@Param("inventoryId") Long inventoryId);
	
	@Query(value = "select tc_user_client_inventory.inventoryid from tc_user_client_inventory where tc_user_client_inventory.inventoryid=:inventoryId", nativeQuery = true)
	public List<Long> getInventoriesToDelete(@Param("inventoryId") Long inventoryId);
}
