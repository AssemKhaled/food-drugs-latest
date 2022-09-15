package com.example.food_drugs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.example.examplequerydslspringdatajpamaven.entity.Points;


/**
 * Queries on table tc_points with SFDA
 * @author fuinco
 *
 */
@Component
public interface PointsRepositorySFDA extends  JpaRepository<Points, Long>, QueryDslPredicateExecutor<Points> {

	@Query(value = "SELECT tc_points.* FROM tc_points"
			+ " WHERE tc_points.userId IN(:userIds) "
			+ " and ( (tc_points.name Like %:search%) ) " + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Points> getAllPointsAll(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_points.* FROM tc_points"
			+ " WHERE tc_points.userId IN(:userIds) "
			+ " and ( (tc_points.name Like %:search%) ) " , nativeQuery = true)
	public List<Points> getAllPointsAllExport(@Param("userIds")List<Long> userIds,@Param("search") String search);
		
	@Query(value = "SELECT count(*) FROM tc_points  " + 
			"  WHERE tc_points.userId IN(:userIds) "
			+ " and ( (tc_points.name Like %:search%) ) " , nativeQuery = true)
	public Integer getAllPointsSizeAll(@Param("userIds")List<Long> userIds,@Param("search") String search);
	

	@Query(value = "SELECT tc_points.* FROM tc_points"
			+ " WHERE tc_points.id IN(:pointIds) "
			+ " and ( (tc_points.name Like %:search%) ) " + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Points> getAllPointsByIdsAll(@Param("pointIds")List<Long> pointIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_points.* FROM tc_points"
			+ " WHERE tc_points.id IN(:pointIds) "
			+ " and ( (tc_points.name Like %:search%) ) " , nativeQuery = true)
	public List<Points> getAllPointsByIdsAllExport(@Param("pointIds")List<Long> pointIds,@Param("search") String search);
	
	
	@Query(value = "SELECT count(*) FROM tc_points  " + 
			"  WHERE tc_points.id IN(:pointIds) "
			+ " and ( (tc_points.name Like %:search%) ) " , nativeQuery = true)
	public Integer getAllPointsSizeByIdsAll(@Param("pointIds")List<Long> pointIds,@Param("search") String search);
	
	
	@Query(value = "SELECT tc_points.* FROM tc_points"
			+ " WHERE tc_points.userId IN(:userIds) and tc_points.delete_date is not null"
			+ " and ( (tc_points.name Like %:search%) ) " + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Points> getAllPointsDeactive(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_points.* FROM tc_points"
			+ " WHERE tc_points.userId IN(:userIds) and tc_points.delete_date is not null"
			+ " and ( (tc_points.name Like %:search%) ) " , nativeQuery = true)
	public List<Points> getAllPointsDeactiveExport(@Param("userIds")List<Long> userIds,@Param("search") String search);
	
	
	@Query(value = "SELECT count(*) FROM tc_points  " + 
			"  WHERE tc_points.userId IN(:userIds) and tc_points.delete_date is not null"
			+ " and ( (tc_points.name Like %:search%) ) " , nativeQuery = true)
	public Integer getAllPointsSizeDeactive(@Param("userIds")List<Long> userIds,@Param("search") String search);
	

	@Query(value = "SELECT tc_points.* FROM tc_points"
			+ " WHERE tc_points.id IN(:pointIds) and tc_points.delete_date is not null"
			+ " and ( (tc_points.name Like %:search%) ) " + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Points> getAllPointsByIdsDeactive(@Param("pointIds")List<Long> pointIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_points.* FROM tc_points"
			+ " WHERE tc_points.id IN(:pointIds) and tc_points.delete_date is not null"
			+ " and ( (tc_points.name Like %:search%) ) " , nativeQuery = true)
	public List<Points> getAllPointsByIdsDeactiveExport(@Param("pointIds")List<Long> pointIds,@Param("search") String search);
	
	
	
	@Query(value = "SELECT count(*) FROM tc_points  " + 
			"  WHERE tc_points.id IN(:pointIds) and tc_points.delete_date is not null"
			+ " and ( (tc_points.name Like %:search%) ) " , nativeQuery = true)
	public Integer getAllPointsSizeByIdsDeactive(@Param("pointIds")List<Long> pointIds,@Param("search") String search);
	
	
}
