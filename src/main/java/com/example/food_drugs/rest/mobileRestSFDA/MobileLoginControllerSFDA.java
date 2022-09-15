package com.example.food_drugs.rest.mobileRestSFDA;

import com.example.examplequerydslspringdatajpamaven.service.LoginService;
import com.example.examplequerydslspringdatajpamaven.service.LoginServiceImpl;
import com.example.food_drugs.dto.ApiResponse;
import com.example.food_drugs.dto.responses.LoginResponse;
import com.example.food_drugs.exception.ApiGetException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Component
@RestController
@RequestMapping(path = "/mobile/auth")
public class MobileLoginControllerSFDA {

    private final LoginService loginService;


    public MobileLoginControllerSFDA(LoginService loginService) {
        this.loginService = loginService;
    }


    @GetMapping(path = "/loginSFDA")
    public ResponseEntity<?> login(@RequestHeader(value = "Authorization", defaultValue = "")String authtorization ){
        return loginService.login(authtorization);
//        try{
//            return ResponseEntity.ok(loginServiceImpl.login(authtorization));
//
//        }catch (Exception | Error e){
//            throw new ApiGetException(e.getLocalizedMessage());
//        }
    }

    @GetMapping(path = "/logoutSFDA")
    public ResponseEntity<?> logout(@RequestHeader(value ="TOKEN", defaultValue = "")String TOKEN ){

        return loginService.logout(TOKEN);
    }

}
