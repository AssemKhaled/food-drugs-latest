package com.example.food_drugs.config;

import com.example.food_drugs.dto.Request.AddDriverTracerRequest;
import com.example.food_drugs.dto.Request.CreateVehicleInTracerRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

/**
 * @author Assem
 */
@Service
@Slf4j
public class ConnectionToTracer {
    @Value("${createDeviceUrl}")
    private String createDeviceUrl;
    @Value("${addDriverUrl}")
    private String addDriverUrl;
    @Value("${updateDeviceUrl}")
    private String updateDeviceUrl;

    public ResponseEntity<CreateVehicleInTracerRequest> createDeviceTracerResponse(CreateVehicleInTracerRequest createVehicleInTracerRequest) {

        log.info("************************ createDeviceTracerResponse STARTED ***************************");

        String plainCreds = "admin@fuinco.com:admin";
        byte[] plainCredsBytes = plainCreds.getBytes();

        byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Basic " + base64Creds);
        String Post_Url = createDeviceUrl;
//        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
//        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
//        messageConverters.add(converter);
        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.setMessageConverters(messageConverters);
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

//        UriComponents builder = UriComponentsBuilder.fromHttpUrl(Post_Url)..build();

//        String URL = builder.toString();
//        if(allDevices.size()>0) {
//            for(int i=0;i<allDevices.size();i++) {
//                URL +="&deviceId="+allDevices.get(i);
//            }
//        }
        HttpEntity<CreateVehicleInTracerRequest> request = new HttpEntity<>(createVehicleInTracerRequest,headers);

            ResponseEntity<CreateVehicleInTracerRequest> rateResponse =
                    restTemplate.exchange(Post_Url,
                            HttpMethod.POST,request,
                            new ParameterizedTypeReference<CreateVehicleInTracerRequest>() {});
        log.info("************************ createDeviceTracerResponse ENDED ***************************");
        return rateResponse;
    }

    public ResponseEntity<CreateVehicleInTracerRequest> updateDeviceTracerResponse(CreateVehicleInTracerRequest createVehicleInTracerRequest) {

        log.info("************************ updateDeviceTracerResponse STARTED ***************************");

        Long id = createVehicleInTracerRequest.getId();
        String plainCreds = "admin@fuinco.com:admin";
        byte[] plainCredsBytes = plainCreds.getBytes();

        byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Basic " + base64Creds);
        String Post_Url = updateDeviceUrl;
//        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
//        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
//        messageConverters.add(converter);
        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.setMessageConverters(messageConverters);
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

//        UriComponents builder = UriComponentsBuilder.fromHttpUrl(Post_Url)..build();

//        String URL = builder.toString();
//        if(allDevices.size()>0) {
//            for(int i=0;i<allDevices.size();i++) {
//                URL +="&deviceId="+allDevices.get(i);
//            }
//        }
        HttpEntity<CreateVehicleInTracerRequest> request = new HttpEntity<>(createVehicleInTracerRequest,headers);

            ResponseEntity<CreateVehicleInTracerRequest> rateResponse =
                    restTemplate.exchange(Post_Url+id,
                            HttpMethod.PUT,request,
                            new ParameterizedTypeReference<CreateVehicleInTracerRequest>() {});
        log.info("************************ updateDeviceTracerResponse ENDED ***************************");
        return rateResponse;
    }

    public ResponseEntity<AddDriverTracerRequest> addDriverTracerResponse(AddDriverTracerRequest addDriverTracerRequest) {

        log.info("************************ addDriverTracerResponse STARTED ***************************");

        Long id = addDriverTracerRequest.getId();
        String plainCreds = "admin@fuinco.com:admin";
        byte[] plainCredsBytes = plainCreds.getBytes();

        byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Basic " + base64Creds);
        String Post_Url = addDriverUrl;
//        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
//        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
//        messageConverters.add(converter);
        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.setMessageConverters(messageConverters);
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

//        UriComponents builder = UriComponentsBuilder.fromHttpUrl(Post_Url)..build();

//        String URL = builder.toString();
//        if(allDevices.size()>0) {
//            for(int i=0;i<allDevices.size();i++) {
//                URL +="&deviceId="+allDevices.get(i);
//            }
//        }
        HttpEntity<AddDriverTracerRequest> request = new HttpEntity<>(addDriverTracerRequest,headers);

            ResponseEntity<AddDriverTracerRequest> rateResponse =
                    restTemplate.exchange(Post_Url,
                            HttpMethod.POST,request,
                            new ParameterizedTypeReference<AddDriverTracerRequest>() {});
        log.info("************************ addDriverTracerResponse ENDED ***************************");
        return rateResponse;
    }


}
