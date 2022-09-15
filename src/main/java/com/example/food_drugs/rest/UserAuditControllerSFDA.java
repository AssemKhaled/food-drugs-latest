package com.example.food_drugs.rest;

import com.example.food_drugs.dto.ApiResponse;
import com.example.food_drugs.dto.Request.UserAuditRequest;
import com.example.food_drugs.entity.UserAuditLogger;
import com.example.food_drugs.exception.ApiRequestException;
import com.example.food_drugs.service.impl.UserAuditServiceImplSFDA;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Assem
 */
@RestController
@RequestMapping(path = "/usersAuditSFDA")
public class UserAuditControllerSFDA {

    private final UserAuditServiceImplSFDA userAuditServiceImplSFDA;

    public UserAuditControllerSFDA(UserAuditServiceImplSFDA userAuditServiceImplSFDA) {
        this.userAuditServiceImplSFDA = userAuditServiceImplSFDA;
    }

    @PostMapping("/getUserLogs")
    ResponseEntity<ApiResponse<List<UserAuditLogger>>> getUserLogs(@RequestParam(value = "userId", defaultValue = "") Long userId,
                                                                   @RequestParam (value = "from", defaultValue = "") String from,
                                                                   @RequestParam (value = "to", defaultValue = "") String to,
                                                                   @RequestParam (value = "search", defaultValue = "") List<String> search,
                                                                   @RequestParam (value = "size", defaultValue = "0") Integer size,
                                                                   @RequestParam (value = "timeOffset", defaultValue = "") String timeOffset,
                                                                   @RequestBody(required = false) UserAuditRequest userAuditRequest ) {
        try{
            return ResponseEntity.ok(
                    userAuditServiceImplSFDA.getUserLogs(userId,from,to,search,timeOffset,size,userAuditRequest));

        }catch (Exception | Error e){
            throw new ApiRequestException(e.getLocalizedMessage());
        }
    }
}
