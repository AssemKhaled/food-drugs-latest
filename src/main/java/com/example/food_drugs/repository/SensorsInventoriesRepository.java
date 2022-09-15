package com.example.food_drugs.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import com.example.food_drugs.entity.SensorsInventories;

public interface SensorsInventoriesRepository extends JpaRepository<SensorsInventories, Long>, QueryDslPredicateExecutor<SensorsInventories>{

	@Query(value = "SELECT tc_sensors_inventories.* FROM tc_sensors_inventories"
			+ " WHERE tc_sensors_inventories.inventoryId IN(:inventoryId)", nativeQuery = true)
	public List<SensorsInventories> getAllSensorsOfInventory(@Param("inventoryId")Long inventoryId);
	
	@Query(value = "SELECT tc_sensors_inventories.* FROM tc_sensors_inventories"
			+ " WHERE tc_sensors_inventories.inventoryId IN(:inventoryIds) and tc_sensors_inventories.name =:name", nativeQuery = true)
	public List<SensorsInventories> getAllSensorsOfInventoryDublicate(@Param("inventoryIds")List<Long> inventoryIds,@Param("name")String name);

	
	@Query(value = "SELECT tc_sensors_inventories.inventoryId FROM tc_sensors_inventories"
			+ " WHERE tc_sensors_inventories.name=:sensorId and tc_sensors_inventories.type=:type "
			+ " limit 0,1 ", nativeQuery = true)
	public Long getBySensorId(@Param("sensorId")String sensorId,@Param("type")String type);
}
