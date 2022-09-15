package com.example.food_drugs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import com.example.examplequerydslspringdatajpamaven.entity.Group;


/**
 * Queries on table tc_groups with SFDA
 * @author fuinco
 *
 */
@Component
public interface GroupRepositorySFDA extends  JpaRepository<Group, Long>, QueryDslPredicateExecutor<Group> {

	
	@Query(value = "SELECT tc_groups.* FROM tc_groups INNER JOIN tc_user_group ON tc_user_group.groupid = tc_groups.id"
			+ " WHERE tc_user_group.userid IN(:userIds) "
			+ " and ((tc_groups.name Like %:search%) )"
			+ " LIMIT :offset,10", nativeQuery = true)
	public List<Group> getAllGroupsAll(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);

	@Query(value = "SELECT tc_groups.* FROM tc_groups INNER JOIN tc_user_group ON tc_user_group.groupid = tc_groups.id"
			+ " WHERE tc_user_group.userid IN(:userIds) "
			+ " and ((tc_groups.name Like %:search%) )", nativeQuery = true)
	public List<Group> getAllGroupsAllExport(@Param("userIds")List<Long> userIds,@Param("search") String search);

	
	@Query(value = "SELECT tc_groups.* FROM tc_groups "
			+ " WHERE tc_groups.id IN(:groupIds) "
			+ " and ((tc_groups.name Like %:search%) ) "
			+ " LIMIT :offset,10 ", nativeQuery = true)
	public List<Group> getAllGroupsByIdsAll(@Param("groupIds")List<Long> groupIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_groups.* FROM tc_groups "
			+ " WHERE tc_groups.id IN(:groupIds) "
			+ " and ((tc_groups.name Like %:search%) ) ", nativeQuery = true)
	public List<Group> getAllGroupsByIdsAllExport(@Param("groupIds")List<Long> groupIds,@Param("search") String search);
	
	
	@Query(value = "SELECT count(*) FROM tc_groups INNER JOIN tc_user_group ON tc_user_group.groupid = tc_groups.id"
			+ " WHERE tc_user_group.userid IN(:userIds) "
			+ " and ((tc_groups.name Like %:search%) )", nativeQuery = true)
	public Integer getAllGroupsSizeAll(@Param("userIds")List<Long> userIds,@Param("search") String search);
	
	@Query(value = "SELECT count(*) FROM tc_groups "
			+ " WHERE tc_groups.id IN(:groupIds) "
			+ " and ((tc_groups.name Like %:search%) )", nativeQuery = true)
	public Integer getAllGroupsSizeByIdsAll(@Param("groupIds")List<Long> groupIds,@Param("search") String search);

	
	@Query(value = "SELECT tc_groups.* FROM tc_groups INNER JOIN tc_user_group ON tc_user_group.groupid = tc_groups.id"
			+ " WHERE tc_user_group.userid IN(:userIds)and tc_groups.is_deleted is not null"
			+ " and ((tc_groups.name Like %:search%) )"
			+ " LIMIT :offset,10", nativeQuery = true)
	public List<Group> getAllGroupsDeactive(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);

	@Query(value = "SELECT tc_groups.* FROM tc_groups INNER JOIN tc_user_group ON tc_user_group.groupid = tc_groups.id"
			+ " WHERE tc_user_group.userid IN(:userIds)and tc_groups.is_deleted is not null"
			+ " and ((tc_groups.name Like %:search%) )" , nativeQuery = true)
	public List<Group> getAllGroupsDeactiveExport(@Param("userIds")List<Long> userIds,@Param("search") String search);

	@Query(value = "SELECT tc_groups.* FROM tc_groups "
			+ " WHERE tc_groups.id IN(:groupIds)and tc_groups.is_deleted is not null "
			+ " and ((tc_groups.name Like %:search%) ) "
			+ " LIMIT :offset,10 ", nativeQuery = true)
	public List<Group> getAllGroupsByIdsDeactive(@Param("groupIds")List<Long> groupIds,@Param("offset") int offset,@Param("search") String search);

	@Query(value = "SELECT tc_groups.* FROM tc_groups "
			+ " WHERE tc_groups.id IN(:groupIds)and tc_groups.is_deleted is not null "
			+ " and ((tc_groups.name Like %:search%) ) ", nativeQuery = true)
	public List<Group> getAllGroupsByIdsDeactiveExport(@Param("groupIds")List<Long> groupIds,@Param("search") String search);
	
	
	@Query(value = "SELECT count(*) FROM tc_groups INNER JOIN tc_user_group ON tc_user_group.groupid = tc_groups.id"
			+ " WHERE tc_user_group.userid IN(:userIds)and tc_groups.is_deleted is not null"
			+ " and ((tc_groups.name Like %:search%) )", nativeQuery = true)
	public Integer getAllGroupsSizeDeactive(@Param("userIds")List<Long> userIds,@Param("search") String search);
	
	@Query(value = "SELECT count(*) FROM tc_groups "
			+ " WHERE tc_groups.id IN(:groupIds)and tc_groups.is_deleted is not null"
			+ " and ((tc_groups.name Like %:search%) )", nativeQuery = true)
	public Integer getAllGroupsSizeByIdsDeactive(@Param("groupIds")List<Long> groupIds,@Param("search") String search);

	
}
