package com.example.food_drugs.helpers.Impl;

import com.example.food_drugs.dto.responses.ResponseWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ResponseHandler<T> {

    private static final Log logger = LogFactory.getLog(ResponseHandler.class);
    public ResponseWrapper<T> reportSuccess( String message, T body){
        return new ResponseWrapper<>(true, message, body ,0);
    }
    public ResponseWrapper<T> reportSuccess( String message, T body,int size){
        return new ResponseWrapper<>(true, message, body,size);
    }


    public ResponseWrapper<T> reportError(String message){
        return new ResponseWrapper<>(false,message, null,0);
    }

    public void errorLogger(String errormessage){
        logger.info(errormessage);
    }
}
