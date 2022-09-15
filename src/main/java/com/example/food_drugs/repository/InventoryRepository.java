package com.example.food_drugs.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.example.food_drugs.dto.responses.InventoriesAndWarehousesWrapper;
import com.example.food_drugs.dto.responses.InventorySummaryDataWrapper;
import com.example.food_drugs.dto.responses.InventorySummaryDataWrapperResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;
import com.example.food_drugs.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long>, QueryDslPredicateExecutor<Inventory>{

	List<Inventory> findAllByWarehouseId(Long warehouseId);

//	List<Inventory> findAllByUserIdInAndDeleteDate(List<Long> userIds , String deleteDate , Pageable pageable);
	List<Inventory> findAllByUserIdInAndDeleteDate(List<Long> userIds , String deleteDate );

	Optional<List<Inventory>> findAllByWarehouseIdInAndDeleteDate(List<Long> wareHouseIds, Date deleteDate);

	@Query(value = " SELECT tc_inventories.id FROM tc_inventories " + 
			" where tc_inventories.userId IN (:userId) and tc_inventories.delete_date is null "
			+ " and tc_inventories.name=:name and tc_inventories.inventoryNumber=:inventoryNumber " ,nativeQuery = true )
	public Long getInventoryIdByName(@Param("userId") Long userId,@Param("name") String name,@Param("inventoryNumber") String inventoryNumber);
	
	
	@Query(value = "SELECT tc_inventories.* FROM tc_inventories"
			+ " WHERE tc_inventories.userId IN(:userIds) and tc_inventories.delete_date is null"
			+ " and ( (tc_inventories.name Like %:search%) or (tc_inventories.inventoryNumber Like %:search%) or (tc_inventories.trackerIMEI Like %:search%) )" + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Inventory> getInventories(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);

	@Query(value = "SELECT tc_inventories.* FROM tc_inventories"
			+ " WHERE tc_inventories.userId IN(:userIds) and tc_inventories.delete_date is null"
			+ " and ( (tc_inventories.name Like %:search%) or (tc_inventories.inventoryNumber Like %:search%) or (tc_inventories.trackerIMEI Like %:search%) )" , nativeQuery = true)
	public List<Inventory> getInventoriesExport(@Param("userIds")List<Long> userIds,@Param("search") String search);
	
	
	@Query(value = "SELECT count(*) FROM tc_inventories " + 
			"  WHERE tc_inventories.userId IN(:userIds) and tc_inventories.delete_date is null " , nativeQuery = true)
	Integer getInventoriesSize(@Param("userIds")List<Long> userIds);
	
	@Query(value = "SELECT tc_inventories.* FROM tc_inventories"
			+ " WHERE tc_inventories.id IN(:inventoryIds) and tc_inventories.delete_date is null"
			+ " and ( (tc_inventories.name Like %:search%) or (tc_inventories.inventoryNumber Like %:search%) or (tc_inventories.trackerIMEI Like %:search%) )" + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Inventory> getInventoriesByIds(@Param("inventoryIds")List<Long> inventoryIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_inventories.* FROM tc_inventories"
			+ " WHERE tc_inventories.id IN(:inventoryIds) and tc_inventories.delete_date is null"
			+ " and ( (tc_inventories.name Like %:search%) or (tc_inventories.inventoryNumber Like %:search%) or (tc_inventories.trackerIMEI Like %:search%) )" , nativeQuery = true)
	public List<Inventory> getInventoriesByIdsExport(@Param("inventoryIds")List<Long> inventoryIds,@Param("search") String search);
	
	
	@Query(value = "SELECT count(*) FROM tc_inventories " + 
			"  WHERE tc_inventories.id IN(:inventoryIds) and tc_inventories.delete_date is null " , nativeQuery = true)
	public Integer getInventoriesSizeByIds(@Param("inventoryIds")List<Long> inventoryIds);

	@Query(value = "select * from tc_inventories "
			+ " where (tc_inventories.name=:name or tc_inventories.inventoryNumber=:inventoryNumber ) and tc_inventories.userId=:userId and tc_inventories.delete_date IS NULL", nativeQuery = true)
	public List<Inventory> checkDublicateAdd(@Param("userId") Long id,
			 @Param("name") String name
			,@Param("inventoryNumber") String inventoryNumber);
	
	@Query(value = "select * from tc_inventories "
			+ " where tc_inventories.inventoryNumber=:inventoryNumber and tc_inventories.delete_date IS NULL", nativeQuery = true)
	public List<Inventory> checkDublicateAddByInv(@Param("inventoryNumber") String inventoryNumber);
	
	
	@Query(value ="select * from tc_inventories "
			+ " where (tc_inventories.name=:name or tc_inventories.inventoryNumber=:inventoryNumber ) "
			+ " and tc_inventories.userId=:userId and tc_inventories.delete_date IS NULL and tc_inventories.id !=:id ", nativeQuery = true)
	public List<Inventory> checkDublicateEdit(@Param("id") Long id,@Param("userId") Long userId,
			@Param("name") String name
			,@Param("inventoryNumber") String inventoryNumber);
	
	@Query(value ="select * from tc_inventories "
			+ " where tc_inventories.inventoryNumber=:inventoryNumber "
			+ " and tc_inventories.delete_date IS NULL and tc_inventories.id !=:id ", nativeQuery = true)
	public List<Inventory> checkDublicateEditByInv(@Param("id") Long id,@Param("inventoryNumber") String inventoryNumber);
	
	@Query(value = "SELECT tc_inventories.id FROM tc_inventories"
			+ " WHERE tc_inventories.userId IN(:userIds) and tc_inventories.delete_date is null", nativeQuery = true)
	public List<Long> getAllInventoriesIds(@Param("userIds")List<Long> userIds);

	@Query(value = "SELECT * FROM tc_inventories"
			+ " WHERE tc_inventories.userId IN(:userIds) and tc_inventories.delete_date is null", nativeQuery = true)
	public List<Inventory> getAllInventoriesIdss(@Param("userIds")List<Long> userIds);


	@Query(value = "SELECT tc_inventories.id as id , tc_inventories.name as name , tc_inventories.lastDataId as lastDataId"
			+ " FROM tc_inventories WHERE tc_inventories.userId IN(:userIds) and tc_inventories.delete_date is null  " +
			"and tc_inventories.lastDataId is Not null LIMIT :offset,10 ", nativeQuery = true)
	List<InventorySummaryDataWrapper> getAllInventoriesSummaryData(@Param("userIds")List<Long> userIds, @Param("offset") int offset);

	@Query(value = "SELECT tc_inventories.id, tc_inventories.name, tc_inventories.lastDataId, tc_inventories.warehouseId," +
			"tc_inventories.storingCategory FROM tc_inventories WHERE tc_inventories.userId IN(:userIds) and tc_inventories.delete_date is null  " +
			"and tc_inventories.lastDataId is Not null LIMIT :offset,10 ", nativeQuery = true)
	List<InventorySummaryDataWrapperResponse> getAllInventoriesSummaryDataNew(@Param("userIds")List<Long> userIds, @Param("offset") int offset);
	@Query(value = "SELECT tc_inventories.* FROM tc_inventories"
			+ " WHERE tc_inventories.userId IN(:userIds) and tc_inventories.delete_date is null and tc_inventories.protocolType =:type", nativeQuery = true)
	public List<Inventory> getAllInventoriesTypeProtocolCSV(@Param("userIds") Long userIds,@Param("type") String type);
	
	@Query(value = "SELECT tc_inventories.* FROM tc_inventories"
			+ " WHERE tc_inventories.userId IN(:userIds) and tc_inventories.delete_date is null and tc_inventories.protocolType =:type limit 0,1", nativeQuery = true)
	public List<Inventory> getAllInventoriesTypeProtocolEasyClould(@Param("userIds") Long userIds,@Param("type") String type);
	
	@Query(value = "SELECT tc_inventories.* FROM tc_inventories"
			+ " WHERE tc_inventories.delete_date is null and tc_inventories.protocolType =:type", nativeQuery = true)
	public List<Inventory> getInventoriesTypeProtocol(@Param("type") String type);
	
	@Query(value = "select * from tc_inventories "
			+ " where tc_inventories.referenceKey IS NOT NULL and tc_inventories.delete_date IS NULL", nativeQuery = true)
	public List<Inventory> getConnectedInventories();
	
	
	@Query(value = "SELECT tc_inventories.* FROM tc_inventories"
			+ " WHERE tc_inventories.userId IN(:userIds) and tc_inventories.delete_date is null", nativeQuery = true)
	public List<Inventory> getAllInventoriesSelect(@Param("userIds")List<Long> userIds);
	
	@Query(value = "SELECT tc_inventories.* FROM tc_inventories"
			+ " WHERE tc_inventories.id IN(:inventoryIds) and tc_inventories.delete_date is null", nativeQuery = true)
	public List<Inventory> getAllInventoriesSelectByIds(@Param("inventoryIds")List<Long> inventoryIds);
	
	@Query(value = "SELECT tc_inventories.id FROM tc_inventories"
			+ " WHERE tc_inventories.warehouseId IN(:warehouseIds) and tc_inventories.delete_date is null", nativeQuery = true)
	public List<Long> getAllInventoriesOfWarehouse(@Param("warehouseIds")List<Long> warehouseIds);


	@Query(value = "SELECT tc_inventories.id AS id ,tc_inventories.name AS inventoryName , tc_warehouses.name AS warehouseName " +
			"FROM tc_inventories " +
			"LEFT JOIN tc_warehouses " +
			"ON tc_inventories.warehouseId = tc_warehouses.id " +
			"WHERE tc_inventories.id IN (:inventoriesIds)" , nativeQuery = true)
	List<InventoriesAndWarehousesWrapper> getAllInventoriesAndWarehouses(@Param("inventoriesIds") Long[] inventoriesIds);
	
	@Query(value = "SELECT tc_inventories.* FROM tc_inventories"
			+ " WHERE tc_inventories.warehouseId IN(:warehouseId) and tc_inventories.delete_date is null", nativeQuery = true)
	public List<Inventory> getAllInventoriesOfWarehouseList(@Param("warehouseId")Long warehouseId);
	
	
	
	@Query(value = "SELECT tc_inventories.* FROM tc_inventories"
			+ " WHERE tc_inventories.userId IN(:userIds) "
			+ " and ( (tc_inventories.name Like %:search%) or (tc_inventories.inventoryNumber Like %:search%) or (tc_inventories.trackerIMEI Like %:search%) )" + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Inventory> getInventoriesAll(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_inventories.* FROM tc_inventories"
			+ " WHERE tc_inventories.userId IN(:userIds) "
			+ " and ( (tc_inventories.name Like %:search%) or (tc_inventories.inventoryNumber Like %:search%) or (tc_inventories.trackerIMEI Like %:search%) )" , nativeQuery = true)
	public List<Inventory> getInventoriesAllExport(@Param("userIds")List<Long> userIds,@Param("search") String search);
	
	
	@Query(value = "SELECT count(*) FROM tc_inventories " + 
			"  WHERE tc_inventories.userId IN(:userIds)  " , nativeQuery = true)
	public Integer getInventoriesSizeAll(@Param("userIds")List<Long> userIds);
	
	@Query(value = "SELECT tc_inventories.* FROM tc_inventories"
			+ " WHERE tc_inventories.id IN(:inventoryIds) "
			+ " and ( (tc_inventories.name Like %:search%) or (tc_inventories.inventoryNumber Like %:search%) or (tc_inventories.trackerIMEI Like %:search%) )" + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Inventory> getInventoriesByIdsAll(@Param("inventoryIds")List<Long> inventoryIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_inventories.* FROM tc_inventories"
			+ " WHERE tc_inventories.id IN(:inventoryIds) "
			+ " and ( (tc_inventories.name Like %:search%) or (tc_inventories.inventoryNumber Like %:search%) or (tc_inventories.trackerIMEI Like %:search%) )" , nativeQuery = true)
	public List<Inventory> getInventoriesByIdsAllExport(@Param("inventoryIds")List<Long> inventoryIds,@Param("search") String search);
	
	
	@Query(value = "SELECT count(*) FROM tc_inventories " + 
			"  WHERE tc_inventories.id IN(:inventoryIds)" , nativeQuery = true)
	public Integer getInventoriesSizeByIdsAll(@Param("inventoryIds")List<Long> inventoryIds);

	
	@Query(value = "SELECT tc_inventories.* FROM tc_inventories"
			+ " WHERE tc_inventories.userId IN(:userIds) and tc_inventories.delete_date is not null"
			+ " and ( (tc_inventories.name Like %:search%) or (tc_inventories.inventoryNumber Like %:search%) or (tc_inventories.trackerIMEI Like %:search%) )" + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Inventory> getInventoriesDeactive(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_inventories.* FROM tc_inventories"
			+ " WHERE tc_inventories.userId IN(:userIds) and tc_inventories.delete_date is not null"
			+ " and ( (tc_inventories.name Like %:search%) or (tc_inventories.inventoryNumber Like %:search%) or (tc_inventories.trackerIMEI Like %:search%) )" , nativeQuery = true)
	public List<Inventory> getInventoriesDeactiveExport(@Param("userIds")List<Long> userIds,@Param("search") String search);
	
	@Query(value = "SELECT count(*) FROM tc_inventories " + 
			"  WHERE tc_inventories.userId IN(:userIds) and tc_inventories.delete_date is not null " , nativeQuery = true)
	public Integer getInventoriesSizeDeactive(@Param("userIds")List<Long> userIds);
	
	@Query(value = "SELECT tc_inventories.* FROM tc_inventories"
			+ " WHERE tc_inventories.id IN(:inventoryIds) and tc_inventories.delete_date is not null"
			+ " and ( (tc_inventories.name Like %:search%) or (tc_inventories.inventoryNumber Like %:search%) or (tc_inventories.trackerIMEI Like %:search%) )" + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Inventory> getInventoriesByIdsDeactive(@Param("inventoryIds")List<Long> inventoryIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_inventories.* FROM tc_inventories"
			+ " WHERE tc_inventories.id IN(:inventoryIds) and tc_inventories.delete_date is not null"
			+ " and ( (tc_inventories.name Like %:search%) or (tc_inventories.inventoryNumber Like %:search%) or (tc_inventories.trackerIMEI Like %:search%) )" , nativeQuery = true)
	public List<Inventory> getInventoriesByIdsDeactiveExport(@Param("inventoryIds")List<Long> inventoryIds,@Param("search") String search);
	
	
	@Query(value = "SELECT count(*) FROM tc_inventories " + 
			"  WHERE tc_inventories.id IN(:inventoryIds) and tc_inventories.delete_date is not null " , nativeQuery = true)
	public Integer getInventoriesSizeByIdsDeactive(@Param("inventoryIds")List<Long> inventoryIds);

	@Query(value = "SELECT tc_inventories.id,tc_inventories.name FROM tc_inventories " 
			+ " WHERE tc_inventories.userId IN(:loggedUserId) and tc_inventories.delete_date is null "
			+ " and tc_inventories.id Not IN(Select tc_user_client_inventory.inventoryid from tc_user_client_inventory where tc_user_client_inventory.userid !=:userId ) ",nativeQuery = true)
	public List<DriverSelect> getInventoryUnSelectOfClient(@Param("loggedUserId") Long loggedUserId,@Param("userId") Long userId);
	
	@Query(value = "SELECT tc_inventories.lastDataId FROM tc_inventories "
			+ "where tc_inventories.lastUpdate>date_sub(now(), interval 0 minute)=false  AND tc_inventories.lastUpdate<date_sub(now(), interval 3 minute)=false "
			+ " AND tc_inventories.userId IN (:userIds) and tc_inventories.delete_date is null and tc_inventories.lastDataId is not null ", nativeQuery = true)
	public List<String> getNumberOfOnlineInventoryList(@Param("userIds")List<Long> userIds);
	                    
	@Query(value = "SELECT tc_inventories.lastDataId FROM tc_inventories "
			+ " where tc_inventories.lastUpdate>date_sub(now(), interval 0 minute)=false  AND tc_inventories.lastUpdate<date_sub(now(), interval 3 minute)=false "
			+ " AND tc_inventories.id IN (:inventoryIds) and tc_inventories.delete_date is null and tc_inventories.lastDataId is not null", nativeQuery = true)
	public List<String> getNumberOfOnlineInventoryListByIds(@Param("inventoryIds")List<Long> inventoryIds);
	
	@Query(value = "SELECT tc_inventories.lastDataId FROM tc_inventories "
			+ "where tc_inventories.lastUpdate>date_sub(now(), interval 3 minute)=false  AND tc_inventories.lastUpdate<date_sub(now(), interval 8 minute)=false "
			+ " AND tc_inventories.userId IN (:userIds) and tc_inventories.delete_date is null and tc_inventories.lastDataId is not null", nativeQuery = true)
	public List<String> getNumberOfOutOfNetworkInventoryList(@Param("userIds")List<Long> userIds);
	
	@Query(value = "SELECT tc_inventories.lastDataId FROM tc_inventories "
			+ "where tc_inventories.lastUpdate>date_sub(now(), interval 8 minute)=false "
			+ " AND tc_inventories.userId IN (:userIds) and tc_inventories.delete_date is null and tc_inventories.lastDataId is not null", nativeQuery = true)
	public List<String> getNumberOfOfflineInventoryList(@Param("userIds")List<Long> userIds);
	
	@Query(value = "SELECT tc_inventories.lastDataId FROM tc_inventories "
			+ " where  tc_inventories.lastUpdate>date_sub(now(), interval 8 minute)=false "
			+ " AND tc_inventories.id IN (:inventoryIds) and tc_inventories.delete_date is null and tc_inventories.lastDataId is not null", nativeQuery = true)
	public List<String> getNumberOfOfflineInventoryListByIds(@Param("inventoryIds")List<Long> inventoryIds);
	
	@Query(value = "SELECT tc_inventories.lastDataId FROM tc_inventories "
			+ "where tc_inventories.lastUpdate>date_sub(now(), interval 3 minute)=false  AND tc_inventories.lastUpdate<date_sub(now(), interval 8 minute)=false "
			+ " AND tc_inventories.id IN (:inventoryIds) and tc_inventories.delete_date is null and tc_inventories.lastDataId is not null", nativeQuery = true)
	public List<String> getNumberOfOutOfNetworkInventoryListByIds(@Param("inventoryIds")List<Long> inventoryIds);
	
	@Query(value = "SELECT count(tc_inventories.id) FROM tc_inventories " + 
			"where tc_inventories.userId IN (:userIds) and tc_inventories.delete_date is null ",nativeQuery = true )
	public Integer getTotalNumberOfUserInventory(@Param("userIds")List<Long> userIds);
	
	@Query(value = "SELECT count(tc_inventories.id) FROM tc_inventories " + 
			"where tc_inventories.id IN (:inventoryIds) and tc_inventories.delete_date is null ",nativeQuery = true )
	public Integer getTotalNumberOfUserInventoryByIds(@Param("inventoryIds")List<Long> inventoryIds);

	
	@Query(value = "SELECT count(tc_inventories.id) FROM tc_inventories " + 
			"where tc_inventories.userId IN (:userIds) and tc_inventories.delete_date is null and tc_inventories.lastUpdate is null ",nativeQuery = true )
	public Integer getTotalNumberOfUserInventoryNoData(@Param("userIds")List<Long> userIds);
	
	@Query(value = "SELECT count(tc_inventories.id) FROM tc_inventories " + 
			"where tc_inventories.id IN (:inventoryIds) and tc_inventories.delete_date is null and tc_inventories.lastUpdate is null ",nativeQuery = true )
	public Integer getTotalNumberOfUserInventoryNoDataByIds(@Param("inventoryIds")List<Long> inventoryIds);

	@Query(value = "SELECT tc_inventories.id FROM tc_inventories"
			+ " WHERE tc_inventories.inventoryNumber=:inventoryNumber "
			+ " and tc_inventories.protocolType=:protocolType "
			+ " and tc_inventories.delete_date is null"
			+ " limit 0,1 ", nativeQuery = true)
	public Long getInventoryByNumber(@Param("inventoryNumber")String inventoryNumber,@Param("protocolType")String protocolType);


	@Query(value = " select new_food_drugs_db.tc_users.name AS companyName,new_food_drugs_db.tc_users.email AS email,new_food_drugs_db.tc_users.identity_num AS identity_num,new_food_drugs_db.tc_users.commercial_num AS commercial_num,new_food_drugs_db.tc_users.manager_name AS manager_name,new_food_drugs_db.tc_users.manager_mobile AS manager_mobile,new_food_drugs_db.tc_users.commercial_reg AS commercial_reg,new_food_drugs_db.tc_inventories.name AS InventoryName,new_food_drugs_db.tc_inventories.inventoryNumber AS inventoryNumber,new_food_drugs_db.tc_inventories.create_date AS create_date,new_food_drugs_db.tc_inventories.delete_date AS delete_date from (new_food_drugs_db.tc_inventories join new_food_drugs_db.tc_users on((new_food_drugs_db.tc_inventories.userId = new_food_drugs_db.tc_users.id))) where ((new_food_drugs_db.tc_inventories.create_date > :endDate) and isnull(new_food_drugs_db.tc_inventories.delete_date)) order by new_food_drugs_db.tc_inventories.delete_date " ,nativeQuery = true)
	List<Object[]> inventoryMonthelyView(@Param("endDate")String endDate);

	Inventory findById(Long id);
}
