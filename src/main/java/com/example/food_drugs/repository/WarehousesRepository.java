package com.example.food_drugs.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;
import com.example.food_drugs.entity.Warehouse;;

public interface WarehousesRepository extends JpaRepository<Warehouse, Long>, QueryDslPredicateExecutor<Warehouse>{


	Optional<List<Warehouse>> findAllByUserIdInAndDeleteDate(List<Long> userIds, Date deleteDate);
	Optional<List<Warehouse>> findAllByUserIdInAndDeleteDate(List<Long> userIds, Date deleteDate,Pageable pageable);
	Optional<List<Warehouse>> findAllByIdIn(List<Long> ids);
	@Query(value = "SELECT count(tc_warehouses.id) FROM tc_warehouses " +
			"where tc_warehouses.userId IN (:userIds) and tc_warehouses.delete_date is null ",nativeQuery = true )
	public Integer getTotalNumberOfUserWarehouse(@Param("userIds")List<Long> userIds);
	
	
	@Query(value = "SELECT count(tc_warehouses.id) FROM tc_warehouses " + 
			"where tc_warehouses.id IN (:warehouseIds) and tc_warehouses.delete_date is null ",nativeQuery = true )
	public Integer getTotalNumberOfUserWarehouseByIds(@Param("warehouseIds")List<Long> warehouseIds);

	@Query(value = "SELECT tc_warehouses.* FROM tc_warehouses"
			+ " WHERE tc_warehouses.userId IN(:userIds) and tc_warehouses.delete_date is null"
			+ " and ( (tc_warehouses.name Like %:search%) or (tc_warehouses.city Like %:search%) or (tc_warehouses.email Like %:search%) or (tc_warehouses.phone Like %:search%)) " + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Warehouse> getWarehouses(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_warehouses.* FROM tc_warehouses"
			+ " WHERE tc_warehouses.userId IN(:userIds) and tc_warehouses.delete_date is null"
			+ " and ( (tc_warehouses.name Like %:search%) or (tc_warehouses.city Like %:search%) or (tc_warehouses.email Like %:search%) or (tc_warehouses.phone Like %:search%)) " , nativeQuery = true)
	public List<Warehouse> getWarehousesExport(@Param("userIds")List<Long> userIds,@Param("search") String search);
	
	
	@Query(value = "SELECT count(*) FROM tc_warehouses " + 
			"  WHERE tc_warehouses.userId IN(:userIds) and tc_warehouses.delete_date is null " , nativeQuery = true)
	public Integer getWarehouseSize(@Param("userIds")List<Long> userIds);
	
	@Query(value = "SELECT tc_warehouses.* FROM tc_warehouses"
			+ " WHERE tc_warehouses.id IN(:warehouseIds) and tc_warehouses.delete_date is null"
			+ " and ( (tc_warehouses.name Like %:search%) or (tc_warehouses.city Like %:search%) or (tc_warehouses.email Like %:search%) or (tc_warehouses.phone Like %:search%)) " + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Warehouse> getWarehousesByIds(@Param("warehouseIds")List<Long> warehouseIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_warehouses.* FROM tc_warehouses"
			+ " WHERE tc_warehouses.id IN(:warehouseIds) and tc_warehouses.delete_date is null"
			+ " and ( (tc_warehouses.name Like %:search%) or (tc_warehouses.city Like %:search%) or (tc_warehouses.email Like %:search%) or (tc_warehouses.phone Like %:search%)) " , nativeQuery = true)
	public List<Warehouse> getWarehousesByIdsExport(@Param("warehouseIds")List<Long> warehouseIds,@Param("search") String search);
	
	
	@Query(value = "SELECT count(*) FROM tc_warehouses " + 
			"  WHERE tc_warehouses.id IN(:warehouseIds) and tc_warehouses.delete_date is null " , nativeQuery = true)
	public Integer getWarehouseSizeByIds(@Param("warehouseIds")List<Long> warehouseIds);
	
	
	@Query(value = "select * from tc_warehouses "
			+ " where (tc_warehouses.email=:email or tc_warehouses.name=:name or tc_warehouses.phone=:phone  or tc_warehouses.licenseNumber=:licenseNumber ) and tc_warehouses.userId=:userId and tc_warehouses.delete_date IS NULL", nativeQuery = true)
	public List<Warehouse> checkDublicateAdd(@Param("userId") Long id,
			 @Param("email") String email
			,@Param("name") String name
			,@Param("phone") String phone
			,@Param("licenseNumber") String licenseNumber);
	
	
	@Query(value ="select * from tc_warehouses "
			+ " where (tc_warehouses.email=:email or tc_warehouses.name=:name or tc_warehouses.phone=:phone  or tc_warehouses.licenseNumber=:licenseNumber ) "
			+ " and tc_warehouses.userId=:userId and tc_warehouses.delete_date IS NULL and tc_warehouses.id !=:id ", nativeQuery = true)
	public List<Warehouse> checkDublicateEdit(@Param("id") Long id,@Param("userId") Long userId,
			@Param("email") String email
			,@Param("name") String name
			,@Param("phone") String phone
			,@Param("licenseNumber") String licenseNumber);
	
	@Query(value = "SELECT tc_warehouses.* FROM tc_warehouses"
			+ " WHERE tc_warehouses.userId IN(:userIds) and tc_warehouses.delete_date is null ", nativeQuery = true)
	public List<Warehouse> getAllWarehousesSelect(@Param("userIds")List<Long> userIds);
	
	@Query(value = "SELECT tc_warehouses.* FROM tc_warehouses"
			+ " WHERE tc_warehouses.id IN(:warehouseIds) and tc_warehouses.delete_date is null ", nativeQuery = true)
	public List<Warehouse> getAllWarehousesSelectByIds(@Param("warehouseIds")List<Long> warehouseIds);


	@Query(value = " SELECT tc_warehouses.userId as userId ,tc_inventories.id as inventoryId ,tc_warehouses.name as wareHouseName , tc_inventories.name as inventoryName , "
			+ " tc_inventories.storingCategory as storingCategory , tc_inventories.lastUpdate as lastUpdate , tc_inventories.lastDataId as lastDataId , tc_warehouses.id FROM tc_inventories "
			+ " JOIN tc_warehouses "
			+ " ON tc_inventories.warehouseId = tc_warehouses.id "
			+ " WHERE tc_warehouses.userId in (:usersIds) "
			+ " AND tc_warehouses.delete_date IS NULL "
			+ " AND tc_inventories.lastDataId IS NOT NULL " , nativeQuery = true)
	List<Object[]> getInventoryWarehouseDataByUserIds (@Param("usersIds")List<Long> usersIds);

	@Query(value = " SELECT tc_warehouses.userId as userId ,tc_inventories.id as inventoryId ,tc_warehouses.name as wareHouseName , tc_inventories.name as inventoryName , "
			+ " tc_inventories.storingCategory as storingCategory , tc_inventories.lastUpdate as lastUpdate , tc_inventories.lastDataId as lastDataId , tc_warehouses.id FROM tc_inventories "
			+ " JOIN tc_warehouses "
			+ " ON tc_inventories.warehouseId = tc_warehouses.id "
			+ " WHERE tc_warehouses.userId in (:usersIds) "
			+ " AND tc_warehouses.delete_date IS NULL "
			+ " AND tc_inventories.lastDataId IS NOT NULL LIMIT :offset,10" , nativeQuery = true)
	List<Object[]> getInventoryWarehouseDataByUserIdsOffset (@Param("usersIds")List<Long> usersIds, @Param("offset") int offset);

	@Query(value = " SELECT tc_warehouses.id ,tc_warehouses.name FROM `tc_warehouses`"
			+ " WHERE tc_warehouses.userId in (:usersIds)",  nativeQuery = true)
	List<Object[]> getWarehouseForUser (@Param("usersIds")List<Long> usersIds);
	@Query(value = " SELECT tc_warehouses.id ,tc_warehouses.name FROM `tc_warehouses`"
			+ " WHERE tc_warehouses.userId in (:usersIds) LIMIT :offset,10",  nativeQuery = true)
	List<Object[]> getWarehouseForUserOffset (@Param("usersIds")List<Long> usersIds, @Param("offset") int offset);

	@Query(value = " SELECT tc_warehouses.id FROM tc_warehouses " + 
			" where tc_warehouses.userId IN (:userId) and tc_warehouses.delete_date is null "
			+ " and tc_warehouses.name=:name and tc_warehouses.licenseNumber=:licenseNumber " ,nativeQuery = true )
	public Long getWarehouseIdByName(@Param("userId") Long userId,@Param("name") String name,@Param("licenseNumber") String licenseNumber);
	

	@Query(value = "SELECT tc_warehouses.* FROM tc_warehouses"
			+ " WHERE tc_warehouses.userId IN(:userIds) "
			+ " and ( (tc_warehouses.name Like %:search%) or (tc_warehouses.city Like %:search%) or (tc_warehouses.email Like %:search%) or (tc_warehouses.phone Like %:search%)) " + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Warehouse> getWarehousesAll(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);
	
	
	@Query(value = "SELECT tc_warehouses.* FROM tc_warehouses"
			+ " WHERE tc_warehouses.userId IN(:userIds) "
			+ " and ( (tc_warehouses.name Like %:search%) or (tc_warehouses.city Like %:search%) or (tc_warehouses.email Like %:search%) or (tc_warehouses.phone Like %:search%)) " , nativeQuery = true)
	public List<Warehouse> getWarehousesAllExport(@Param("userIds")List<Long> userIds,@Param("search") String search);
	
	@Query(value = "SELECT count(*) FROM tc_warehouses " + 
			"  WHERE tc_warehouses.userId IN(:userIds) " , nativeQuery = true)
	public Integer getWarehouseSizeAll(@Param("userIds")List<Long> userIds);
	
	@Query(value = "SELECT tc_warehouses.* FROM tc_warehouses"
			+ " WHERE tc_warehouses.id IN(:warehouseIds) "
			+ " and ( (tc_warehouses.name Like %:search%) or (tc_warehouses.city Like %:search%) or (tc_warehouses.email Like %:search%) or (tc_warehouses.phone Like %:search%)) " + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Warehouse> getWarehousesByIdsAll(@Param("warehouseIds")List<Long> warehouseIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_warehouses.* FROM tc_warehouses"
			+ " WHERE tc_warehouses.id IN(:warehouseIds) "
			+ " and ( (tc_warehouses.name Like %:search%) or (tc_warehouses.city Like %:search%) or (tc_warehouses.email Like %:search%) or (tc_warehouses.phone Like %:search%)) " , nativeQuery = true)
	public List<Warehouse> getWarehousesByIdsAllExport(@Param("warehouseIds")List<Long> warehouseIds,@Param("search") String search);
	
	@Query(value = "SELECT count(*) FROM tc_warehouses " + 
			"  WHERE tc_warehouses.id IN(:warehouseIds) " , nativeQuery = true)
	public Integer getWarehouseSizeByIdsAll(@Param("warehouseIds")List<Long> warehouseIds);
	
	
	@Query(value = "SELECT tc_warehouses.* FROM tc_warehouses"
			+ " WHERE tc_warehouses.userId IN(:userIds) and tc_warehouses.delete_date is not null"
			+ " and ( (tc_warehouses.name Like %:search%) or (tc_warehouses.city Like %:search%) or (tc_warehouses.email Like %:search%) or (tc_warehouses.phone Like %:search%)) " + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Warehouse> getWarehousesDeactive(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_warehouses.* FROM tc_warehouses"
			+ " WHERE tc_warehouses.userId IN(:userIds) and tc_warehouses.delete_date is not null"
			+ " and ( (tc_warehouses.name Like %:search%) or (tc_warehouses.city Like %:search%) or (tc_warehouses.email Like %:search%) or (tc_warehouses.phone Like %:search%)) " , nativeQuery = true)
	public List<Warehouse> getWarehousesDeactiveExport(@Param("userIds")List<Long> userIds,@Param("search") String search);
	
	
	@Query(value = "SELECT count(*) FROM tc_warehouses " + 
			"  WHERE tc_warehouses.userId IN(:userIds) and tc_warehouses.delete_date is not null " , nativeQuery = true)
	public Integer getWarehouseSizeDeactive(@Param("userIds")List<Long> userIds);
	
	@Query(value = "SELECT tc_warehouses.* FROM tc_warehouses"
			+ " WHERE tc_warehouses.id IN(:warehouseIds) and tc_warehouses.delete_date is not null"
			+ " and ( (tc_warehouses.name Like %:search%) or (tc_warehouses.city Like %:search%) or (tc_warehouses.email Like %:search%) or (tc_warehouses.phone Like %:search%)) " + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Warehouse> getWarehousesByIdsDeactive(@Param("warehouseIds")List<Long> warehouseIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_warehouses.* FROM tc_warehouses"
			+ " WHERE tc_warehouses.id IN(:warehouseIds) and tc_warehouses.delete_date is not null"
			+ " and ( (tc_warehouses.name Like %:search%) or (tc_warehouses.city Like %:search%) or (tc_warehouses.email Like %:search%) or (tc_warehouses.phone Like %:search%)) " , nativeQuery = true)
	public List<Warehouse> getWarehousesByIdsDeactiveExport(@Param("warehouseIds")List<Long> warehouseIds,@Param("search") String search);
	
	
	@Query(value = "SELECT count(*) FROM tc_warehouses " + 
			"  WHERE tc_warehouses.id IN(:warehouseIds) and tc_warehouses.delete_date is not null " , nativeQuery = true)
	public Integer getWarehouseSizeByIdsDeactive(@Param("warehouseIds")List<Long> warehouseIds);

	@Query(value = "SELECT tc_warehouses.id,tc_warehouses.name FROM tc_warehouses " 
			+ " WHERE tc_warehouses.userId IN(:loggedUserId) and tc_warehouses.delete_date is null "
			+ " and tc_warehouses.id Not IN(Select tc_user_client_warehouse.warehouseid from tc_user_client_warehouse where tc_user_client_warehouse.userid !=:userId ) ",nativeQuery = true)
	public List<DriverSelect> getWarehouseUnSelectOfClient(@Param("loggedUserId") Long loggedUserId,@Param("userId") Long userId);
	
	
	@Query(value = "SELECT tc_warehouses.id,tc_warehouses.name FROM tc_warehouses "
			+ " Inner join tc_user_client_warehouse on tc_user_client_warehouse.warehouseid = tc_warehouses.id "
			+ " where tc_user_client_warehouse.userid IN (:usersIds) and tc_warehouses.delete_date is null ",nativeQuery = true)
	public List<DriverSelect> getWarehousesSelectClient(@Param("usersIds") List<Long>usersIds);
	
	@Query(value = "SELECT tc_warehouses.id,tc_warehouses.name FROM tc_warehouses "
			+ " where tc_warehouses.userId IN (:usersIds) and tc_warehouses.delete_date is null ",nativeQuery = true)
	public List<DriverSelect> getWarehousesSelect(@Param("usersIds") List<Long>usersIds);

	Optional<Warehouse> findById(Long id);
}
