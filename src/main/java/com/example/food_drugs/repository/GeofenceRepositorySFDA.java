package com.example.food_drugs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;

/**
 * Queries on table tc_geofences with SFDA
 * @author fuinco
 *
 */
@Component
public interface GeofenceRepositorySFDA extends  JpaRepository<Geofence, Long>, QueryDslPredicateExecutor<Geofence> {

	@Query(value = "SELECT tc_geofences.* FROM tc_geofences INNER JOIN tc_user_geofence ON tc_user_geofence.geofenceid = tc_geofences.id"
			+ " WHERE tc_user_geofence.userid IN(:userIds) "
			+ " and ((tc_geofences.name Like %:search%) OR (tc_geofences.type Like %:search%))"
			+ " LIMIT :offset,10", nativeQuery = true)
	public List<Geofence> getAllGeofencesAll(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_geofences.* FROM tc_geofences INNER JOIN tc_user_geofence ON tc_user_geofence.geofenceid = tc_geofences.id"
			+ " WHERE tc_user_geofence.userid IN(:userIds) "
			+ " and ((tc_geofences.name Like %:search%) OR (tc_geofences.type Like %:search%))", nativeQuery = true)
	public List<Geofence> getAllGeofencesAllExport(@Param("userIds")List<Long> userIds,@Param("search") String search);
	
	
	@Query(value = "SELECT count(*) FROM tc_geofences INNER JOIN tc_user_geofence ON tc_user_geofence.geofenceid = tc_geofences.id"
			+ " WHERE tc_user_geofence.userid IN(:userIds) "
			+ " and ((tc_geofences.name Like %:search%) OR (tc_geofences.type Like %:search%))", nativeQuery = true)
	public Integer getAllGeofencesSizeAll(@Param("userIds")List<Long> userIds,@Param("search") String search);
	

	@Query(value = "SELECT tc_geofences.* FROM tc_geofences "
			+ " WHERE tc_geofences.id IN(:geofenceIds) "
			+ " and ((tc_geofences.name Like %:search%) OR (tc_geofences.type Like %:search%))"
			+ " LIMIT :offset,10", nativeQuery = true)
	public List<Geofence> getAllGeofencesByIdsAll(@Param("geofenceIds")List<Long> geofenceIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_geofences.* FROM tc_geofences "
			+ " WHERE tc_geofences.id IN(:geofenceIds) "
			+ " and ((tc_geofences.name Like %:search%) OR (tc_geofences.type Like %:search%))" , nativeQuery = true)
	public List<Geofence> getAllGeofencesByIdsAllExport(@Param("geofenceIds")List<Long> geofenceIds,@Param("search") String search);
	
	@Query(value = "SELECT count(*) FROM tc_geofences"
			+ " WHERE tc_geofences.id IN(:geofenceIds) "
			+ " and ((tc_geofences.name Like %:search%) OR (tc_geofences.type Like %:search%))", nativeQuery = true)
	public Integer getAllGeofencesSizeByIdsAll(@Param("geofenceIds")List<Long> geofenceIds,@Param("search") String search);
	

	
	@Query(value = "SELECT tc_geofences.* FROM tc_geofences INNER JOIN tc_user_geofence ON tc_user_geofence.geofenceid = tc_geofences.id"
			+ " WHERE tc_user_geofence.userid IN(:userIds)and tc_geofences.delete_date is not null"
			+ " and ((tc_geofences.name Like %:search%) OR (tc_geofences.type Like %:search%))"
			+ " LIMIT :offset,10", nativeQuery = true)
	public List<Geofence> getAllGeofencesDeactive(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_geofences.* FROM tc_geofences INNER JOIN tc_user_geofence ON tc_user_geofence.geofenceid = tc_geofences.id"
			+ " WHERE tc_user_geofence.userid IN(:userIds)and tc_geofences.delete_date is not null"
			+ " and ((tc_geofences.name Like %:search%) OR (tc_geofences.type Like %:search%))", nativeQuery = true)
	public List<Geofence> getAllGeofencesDeactiveExport(@Param("userIds")List<Long> userIds,@Param("search") String search);
	
	@Query(value = "SELECT count(*) FROM tc_geofences INNER JOIN tc_user_geofence ON tc_user_geofence.geofenceid = tc_geofences.id"
			+ " WHERE tc_user_geofence.userid IN(:userIds)and tc_geofences.delete_date is not null"
			+ " and ((tc_geofences.name Like %:search%) OR (tc_geofences.type Like %:search%))", nativeQuery = true)
	public Integer getAllGeofencesSizeDeactive(@Param("userIds")List<Long> userIds,@Param("search") String search);
	

	@Query(value = "SELECT tc_geofences.* FROM tc_geofences "
			+ " WHERE tc_geofences.id IN(:geofenceIds)and tc_geofences.delete_date is not null"
			+ " and ((tc_geofences.name Like %:search%) OR (tc_geofences.type Like %:search%))"
			+ " LIMIT :offset,10", nativeQuery = true)
	public List<Geofence> getAllGeofencesByIdsDeactive(@Param("geofenceIds")List<Long> geofenceIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_geofences.* FROM tc_geofences "
			+ " WHERE tc_geofences.id IN(:geofenceIds)and tc_geofences.delete_date is not null"
			+ " and ((tc_geofences.name Like %:search%) OR (tc_geofences.type Like %:search%))", nativeQuery = true)
	public List<Geofence> getAllGeofencesByIdsDeactiveExport(@Param("geofenceIds")List<Long> geofenceIds,@Param("search") String search);
	
	@Query(value = "SELECT count(*) FROM tc_geofences"
			+ " WHERE tc_geofences.id IN(:geofenceIds)and tc_geofences.delete_date is not null"
			+ " and ((tc_geofences.name Like %:search%) OR (tc_geofences.type Like %:search%))", nativeQuery = true)
	public Integer getAllGeofencesSizeByIdsDeactive(@Param("geofenceIds")List<Long> geofenceIds,@Param("search") String search);
	
}
