package com.example.food_drugs.helpers.Impl;

import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;
import com.example.food_drugs.dto.AuditGObject;
import com.example.food_drugs.entity.UserAuditLogger;
import com.example.food_drugs.exception.ApiGetException;
import com.example.food_drugs.helpers.AssistantService;
import com.example.food_drugs.repository.UserAuditLoggerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Assem
 */
@Service
@Slf4j
public class AssistantServiceImpl implements AssistantService {
    private final UserServiceImpl userServiceImpl;
    private final UserAuditLoggerRepository userAuditLoggerRepository;
    private final UserRepository userRepository;
    public AssistantServiceImpl(@Lazy UserServiceImpl userServiceImpl, UserAuditLoggerRepository userAuditLoggerRepository, UserRepository userRepository) {
        this.userServiceImpl = userServiceImpl;
        this.userAuditLoggerRepository = userAuditLoggerRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Long> getChildrenOfUser(Long userId) {

        List<Long> userIds = new ArrayList<>();
        User user = userServiceImpl.findById(userId);
//        userServiceImpl.resetChildernArray();

        if (userId != 0) {
            if (user == null) {
                throw new ApiGetException("This User is not Found");
            }
            if (user.getDeleteDate() != null) {
                throw new ApiGetException("This User Was Delete at : " + user.getDeleteDate());
            }
            if (user.getAccountType().equals(4)) {
                userIds.add(userId);
            } else {
                List<User> childrenUsers = userServiceImpl.getActiveAndInactiveChildern(userId);
                if (childrenUsers.isEmpty()) {
                    userIds.add(userId);
                } else {
                    userIds.add(userId);
//                    AuditGObject<Device> device =new AuditGObject<>();
//                    saveOnAuditChanges(userId,device,device,"device","update");
                    for (User object : childrenUsers) {
                        userIds.add(object.getId());
                    }
                }
            }
            return userIds;
        }
        throw new ApiGetException ("User Id is Invalid");
    }
    @Override
    public List<Long> getUserChildrens(Long userId) {
    Optional<User> optionalUser = userRepository.findById(userId);
    List<Long> userIds =new ArrayList<>();
    userIds.add(userId);
    if (optionalUser.isPresent()) {
        User user = optionalUser.get();
        if (user.getAccountType() == 2){
            //get all clients of vendor
            Optional<List<User>> optionalUserList = userRepository.findByVendorIdAndDeleteDate(userId,null);
            if (optionalUserList.isPresent()) {
                List<User> userList = optionalUserList.get();
                userIds = userList.stream()
                        .map(User::getId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                userIds.add(userId);
            }
        }else if (user.getAccountType() == 3){
            //get all users of client
            Optional<List<User>> optionalUserList = userRepository.findAllByClientIdAndDeleteDate(userId,null);
            if (optionalUserList.isPresent()) {
                List<User> userList = optionalUserList.get();
                userIds = userList.stream()
                        .map(User::getId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                userIds.add(userId);
            }
        }
        return userIds;
    }
    return userIds;
    }
    @Override
    public void saveOnAuditChanges(Long userId, AuditGObject<?> oldEntity, AuditGObject<?> newEntity, String action,Long entityId) {
        log.info("***************saveOnAuditChanges STARTED***************");
        UserAuditLogger userAuditLogger;
        Map<String, Object> oldObject;
        Map<String, Object> newObject;
        Map<String, Object> finalOldObject = new HashMap<>();
        Map<String, Object> finalNewObject = new HashMap<>();
        Date date = new Date();String classType;String act = null; ObjectMapper oMapper = new ObjectMapper();
        //        AuditGObject<?> oldOne = new AuditGObject<>();
//        AuditGObject<?> newOne = new AuditGObject<>();
//        if (oldEntity != null) {
//            oldOne.setEntity( oldEntity.getEntity());
//        }
//        if (newEntity != null) {
//            newOne.setEntity((Device) newEntity.getEntity());
//        }
//             oldEntity = new AuditGObject<Device>();
//             newEntity = new AuditGObject<Device>();
        switch (action) {
            case "update":
                classType = oldEntity.getEntity().getClass().getSimpleName();
                if (oldEntity != null&&newEntity!=null) {
                    DiffNode diff = ObjectDifferBuilder.buildDefault().compare(oldEntity.getEntity(),newEntity.getEntity());
                    if (diff.hasChanges()) {
                        diff.visit((node, visit) -> {
//                        while (node.hasChildren()){
//                        }
                            if (!node.hasChildren()) { // Only print if the property has no child
                                final Object oldValue = node.canonicalGet(oldEntity.getEntity());
                                final Object newValue = node.canonicalGet(newEntity.getEntity());
//                            final String message = node.getPropertyName() + " changed from " +
//                                    oldValue + " to " + newValue;
                                finalOldObject.put(node.getPropertyName(), oldValue);
                                finalNewObject.put(node.getPropertyName(), newValue);
                            }
                        });
                        act = "updated";
                    } else {
                        act = "Updated But No Changes";
                    }
                }
                userAuditLogger = UserAuditLogger
                        .builder()
                        .userId(userId)
                        .entity(classType)
                        .entityId(entityId)
                        .genericId(entityId+"-"+classType)
                        .action(act)
                        .date(date)
                        .oldValue(finalOldObject)
                        .newValue(finalNewObject)
                        .build();
                userAuditLoggerRepository.save(userAuditLogger);
                break;
            case "create":
                classType = newEntity.getEntity().getClass().getSimpleName();
                newObject = oMapper.convertValue(newEntity.getEntity(), Map.class);
                userAuditLogger = UserAuditLogger
                        .builder()
                        .userId(userId)
                        .entity(classType)
                        .entityId(entityId)
                        .genericId(entityId+"-"+classType)
                        .action(action)
                        .date(date)
                        .oldValue(null)
                        .newValue(newObject)
                        .build();
                userAuditLoggerRepository.save(userAuditLogger);
                break;
            case "delete":
                classType = oldEntity.getEntity().getClass().getSimpleName();
                oldObject = oMapper.convertValue(oldEntity.getEntity(), Map.class);
                userAuditLogger = UserAuditLogger
                        .builder()
                        .userId(userId)
                        .entity(classType)
                        .genericId(entityId+"-"+classType)
                        .entityId(entityId)
                        .action(action)
                        .date(date)
                        .oldValue(oldObject)
                        .newValue(null)
                        .build();
                userAuditLoggerRepository.save(userAuditLogger);
                break;
        }
        log.info("***************saveOnAuditChanges ENDED***************");
    }

}