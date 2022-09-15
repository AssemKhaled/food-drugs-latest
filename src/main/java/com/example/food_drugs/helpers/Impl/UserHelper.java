package com.example.food_drugs.helpers.Impl;

import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;

import com.example.food_drugs.dto.responses.ResponseWrapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

import java.util.List;


@Repository
public class UserHelper extends RestServiceController {

    private final UserServiceImpl userServiceImpl;

    public UserHelper(@Lazy UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    public ResponseWrapper userErrorsChecker(String TOKEN , Long userId){
        ResponseHandler responseHandler = new ResponseHandler();

        if(TOKEN.equals("")) {
            return responseHandler.reportError("TOKEN id is required");
        }

        if(super.checkActive(TOKEN) != null) {
            return responseHandler.reportError("TOKEN IS NOT VALID");
        }

        if(userId == 0) {
            return responseHandler.reportError("No loggedId");
        }


        if(userServiceImpl.findById(userId) == null) {
            return responseHandler.reportError("User Not Found");
        }

        return responseHandler.reportSuccess("Success",null);
    }


    public ResponseWrapper userTokenErrorsChecker(String TOKEN){
        ResponseHandler responseHandler = new ResponseHandler();

        if(TOKEN.equals("")) {
            return responseHandler.reportError("TOKEN id is required");
        }

        if(super.checkActive(TOKEN) != null) {
            return responseHandler.reportError("TOKEN IS NOT VALID");
        }

        return responseHandler.reportSuccess("Success",null);
    }

    public List<Long> getUserChildrenId(Long userId){
        List<Long> usersIds = new ArrayList<>();
        List<User> childrenUsers = userServiceImpl.getActiveAndInactiveChildern(userId);
        if(childrenUsers.isEmpty()) {
            usersIds.add(userId);
        }
        else {
            usersIds.add(userId);
            for(User object : childrenUsers) {
                usersIds.add(object.getId());
            }
        }
        return usersIds;

    }



}
