package com.example.food_drugs.service.impl;

import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.entity.UserSelect;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.service.UserRoleService;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;
import com.example.food_drugs.dto.ApiResponse;
import com.example.food_drugs.dto.ApiResponseBuilder;
import com.example.food_drugs.dto.Request.UserAuditRequest;
import com.example.food_drugs.entity.UserAuditLogger;
import com.example.food_drugs.helpers.Impl.AssistantServiceImpl;
import com.example.food_drugs.repository.UserAuditLoggerRepository;
import com.example.food_drugs.service.UserServiceSFDA;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * services functionality related to user SFDA
 * @author fuinco
 *
 */
@Service
public class UserServiceImplSFDA extends RestServiceController implements UserServiceSFDA {

    private GetObjectResponse getObjectResponse;

    private final UserServiceImpl userServiceImpl;
    private final UserAuditLoggerRepository userAuditLoggerRepository;
    private final UserRoleService userRoleService;
    private final AssistantServiceImpl assistantServiceImpl;
    private final UserRepository userRepository;

    public UserServiceImplSFDA(UserServiceImpl userServiceImpl, UserAuditLoggerRepository userAuditLoggerRepository, UserRoleService userRoleService, AssistantServiceImpl assistantServiceImpl, UserRepository userRepository) {

        this.userServiceImpl = userServiceImpl;
        this.userAuditLoggerRepository = userAuditLoggerRepository;
        this.userRoleService = userRoleService;
        this.assistantServiceImpl = assistantServiceImpl;
        this.userRepository = userRepository;
    }

    public ResponseEntity userAndTokenErrorCheckerForElm(String TOKEN , Long userId){

        if(TOKEN.equals("")) {
            getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
            return  ResponseEntity.badRequest().body(getObjectResponse);
        }

        if(super.checkActive(TOKEN)!= null)
        {
            return super.checkActive(TOKEN);
        }

        if(userId == 0) {
            getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No loggedId",null);
            return  ResponseEntity.badRequest().body(getObjectResponse);
        }


        User user = userServiceImpl.findById(userId);
        if(user == null) {
            getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this user not found",null);
            return  ResponseEntity.status(404).body(getObjectResponse);
        }

        if(user.getAccountType()!= 1) {
            if(!userRoleService.checkUserHasPermission(userId, "WAREHOUSE", "updateInElm")) {
                getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user does not has permission to connectToElm",null);
                return  ResponseEntity.badRequest().body(getObjectResponse);
            }
        }
        return ResponseEntity.ok(user);
    }

    public ResponseEntity userAndTokenErrorChecker(String TOKEN , Long userId){

        if(TOKEN.equals("")) {
            getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
            return  ResponseEntity.badRequest().body(getObjectResponse);
        }

        if(super.checkActive(TOKEN)!= null)
        {
            return super.checkActive(TOKEN);
        }

        if(userId == 0) {
            getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No loggedId",null);
            return  ResponseEntity.badRequest().body(getObjectResponse);
        }


        User user = userServiceImpl.findById(userId);
        if(user == null) {
            getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this user not found",null);
            return  ResponseEntity.status(404).body(getObjectResponse);
        }

        return ResponseEntity.ok(user);
    }


    public ResponseEntity<?> userErrorChecker(Long userId){

        if(userId == 0) {
            getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No loggedId",null);
            return  ResponseEntity.badRequest().body(getObjectResponse);
        }

        User user = userServiceImpl.findById(userId);
        if(user == null) {
            getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this user not found",null);
            return  ResponseEntity.status(404).body(getObjectResponse);
        }

        return ResponseEntity.ok(user);
    }

    @Override
    public ApiResponse<List<UserSelect>> getUsersChildren(Long userId) {
        ApiResponseBuilder<List<UserSelect>> builder = new ApiResponseBuilder<>();
        List<UserSelect> result ; List<Long> userIds;
        userIds = assistantServiceImpl.getUserChildrens(userId);
        result = userRepository.getUserSelectWithNewChild(userIds);
        if (!result.isEmpty()) {
            builder.setMessage("Success");
            builder.setSize(result.size());
            builder.setStatusCode(200);
            builder.setEntity(result);
            builder.setSuccess(true);
            return builder.build();
        }else {
            builder.setMessage("No Users Found For This User");
            builder.setSize(result.size());
            builder.setStatusCode(200);
            builder.setEntity(result);
            builder.setSuccess(true);
            return builder.build();
        }
    }
}
