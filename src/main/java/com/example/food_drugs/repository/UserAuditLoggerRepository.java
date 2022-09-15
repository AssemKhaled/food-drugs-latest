package com.example.food_drugs.repository;

import com.example.food_drugs.entity.UserAuditLogger;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Assem
 */
@Repository
public interface UserAuditLoggerRepository extends MongoRepository<UserAuditLogger,String> {

//    @Query("{'$match':{'userId':{$in:?0},'date':{$gt:?1 , $lte:?2}}}{'$sort': {'date':-1}}")
//    List<UserAuditLogger> findAuditPage(List<Long> deviceId , Date from , Date to, Pageable pageable);

    Page<UserAuditLogger> findAllByUserIdAndDateBetweenOrderByDateDesc
            (Long ids , Date from , Date to, Pageable pageable);
    Page<UserAuditLogger>
    findAllByUserIdAndEntityIdInAndEntityAndDateBetweenOrderByDateDesc
            (Long userId,List<Long> ids ,String entity, Date from , Date to,Pageable pageable);
    Page<UserAuditLogger>
    findAllByUserIdAndEntityInAndGenericIdInAndDateBetweenOrderByDateDesc
            (Long userId,List<String> entityNames ,List<String> entityIds, Date from , Date to,Pageable pageable);
}
