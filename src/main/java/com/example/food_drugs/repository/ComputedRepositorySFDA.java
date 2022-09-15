package com.example.food_drugs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.example.examplequerydslspringdatajpamaven.entity.Attribute;

/**
 * Queries on table tc_attributes with SFDA
 * @author fuinco
 *
 */
@Component
public interface ComputedRepositorySFDA extends  JpaRepository<Attribute, Long>, QueryDslPredicateExecutor<Attribute> {

	@Query(value = "SELECT tc_attributes.* FROM tc_attributes INNER JOIN tc_user_attribute ON tc_user_attribute.attributeid = tc_attributes.id"
			+ " WHERE tc_user_attribute.userid IN(:userIds) "
			+ " and ( (tc_attributes.type Like %:search%) ) " + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Attribute> getAllComputedAll(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);

	@Query(value = "SELECT tc_attributes.* FROM tc_attributes INNER JOIN tc_user_attribute ON tc_user_attribute.attributeid = tc_attributes.id"
			+ " WHERE tc_user_attribute.userid IN(:userIds) "
			+ " and ( (tc_attributes.type Like %:search%) ) " , nativeQuery = true)
	public List<Attribute> getAllComputedAllExport(@Param("userIds")List<Long> userIds,@Param("search") String search);

	
	@Query(value = "SELECT count(*) FROM tc_attributes INNER JOIN tc_user_attribute ON tc_user_attribute.attributeid = tc_attributes.id " + 
			"  WHERE tc_user_attribute.userid IN(:userIds) ", nativeQuery = true)
	public Integer getAllComputedSizeAll(@Param("userIds")List<Long> userIds);
	
	
	@Query(value = "SELECT tc_attributes.* FROM tc_attributes "
			+ " WHERE tc_attributes.id IN(:computedIds)"
			+ " and ( (tc_attributes.type Like %:search%) ) " + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Attribute> getAllComputedByIdsAll(@Param("computedIds")List<Long> computedIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_attributes.* FROM tc_attributes "
			+ " WHERE tc_attributes.id IN(:computedIds)"
			+ " and ( (tc_attributes.type Like %:search%) ) " , nativeQuery = true)
	public List<Attribute> getAllComputedByIdsAllExport(@Param("computedIds")List<Long> computedIds,@Param("search") String search);
	
	
	@Query(value = "SELECT count(*) FROM tc_attributes " + 
			"  WHERE tc_attributes.id IN(:computedIds)", nativeQuery = true)
	public Integer getAllComputedSizeByIdsAll(@Param("computedIds")List<Long> computedIds);
	
	
	@Query(value = "SELECT tc_attributes.* FROM tc_attributes INNER JOIN tc_user_attribute ON tc_user_attribute.attributeid = tc_attributes.id"
			+ " WHERE tc_user_attribute.userid IN(:userIds) and  tc_attributes.delete_date is not null"
			+ " and ( (tc_attributes.type Like %:search%) ) " + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Attribute> getAllComputedDeactive(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);

	@Query(value = "SELECT tc_attributes.* FROM tc_attributes INNER JOIN tc_user_attribute ON tc_user_attribute.attributeid = tc_attributes.id"
			+ " WHERE tc_user_attribute.userid IN(:userIds) and  tc_attributes.delete_date is not null"
			+ " and ( (tc_attributes.type Like %:search%) ) " , nativeQuery = true)
	public List<Attribute> getAllComputedDeactiveExport(@Param("userIds")List<Long> userIds,@Param("search") String search);


	@Query(value = "SELECT count(*) FROM tc_attributes INNER JOIN tc_user_attribute ON tc_user_attribute.attributeid = tc_attributes.id " + 
			"  WHERE tc_user_attribute.userid IN(:userIds) and  tc_attributes.delete_date is not null", nativeQuery = true)
	public Integer getAllComputedSizeDeactive(@Param("userIds")List<Long> userIds);
	
	@Query(value = "SELECT tc_attributes.* FROM tc_attributes "
			+ " WHERE tc_attributes.id IN(:computedIds) and  tc_attributes.delete_date is not null "
			+ " and ( (tc_attributes.type Like %:search%) ) " + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Attribute> getAllComputedByIdsDeactive(@Param("computedIds")List<Long> computedIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_attributes.* FROM tc_attributes "
			+ " WHERE tc_attributes.id IN(:computedIds) and  tc_attributes.delete_date is not null "
			+ " and ( (tc_attributes.type Like %:search%) ) " , nativeQuery = true)
	public List<Attribute> getAllComputedByIdsDeactiveExport(@Param("computedIds")List<Long> computedIds,@Param("search") String search);
	
	
	@Query(value = "SELECT count(*) FROM tc_attributes " + 
			"  WHERE tc_attributes.id IN(:computedIds) and tc_attributes.delete_date is not null", nativeQuery = true)
	public Integer getAllComputedSizeByIdsDeactive(@Param("computedIds")List<Long> computedIds);
	
	
}
