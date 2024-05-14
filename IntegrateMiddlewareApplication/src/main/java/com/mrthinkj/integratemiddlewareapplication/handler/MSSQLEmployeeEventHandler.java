package com.mrthinkj.integratemiddlewareapplication.handler;


import com.mrthinkj.core.MSSQLEmployee;
import com.mrthinkj.core.MSSQLEmployeePayload;
import com.mrthinkj.core.UserPayload;
import com.mrthinkj.integratemiddlewareapplication.model.SqlEmployee;
import com.mrthinkj.integratemiddlewareapplication.service.MergeService;
import com.mrthinkj.integratemiddlewareapplication.service.MongoEmployeeService;
import com.mrthinkj.integratemiddlewareapplication.service.SocketService;
import com.mrthinkj.integratemiddlewareapplication.service.SqlEmployeeService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@AllArgsConstructor
@KafkaListener(topics = "mssql-person-topic")
public class MSSQLEmployeeEventHandler {
    SqlEmployeeService sqlEmployeeService;
    MongoEmployeeService mongoEmployeeService;
    MergeService mergeService;
    SocketService socketService;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @KafkaHandler
    public void handleMSSQLEmployeePayload(MSSQLEmployeePayload mssqlEmployeePayload){
        String createEmployeeURI = "http://localhost:19335/PersonalsApi/Create";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MSSQLEmployee> entity =
                new HttpEntity<>(mssqlEmployeePayload.getMssqlEmployee(), headers);
        try{
            restTemplate.postForObject(createEmployeeURI, entity, MSSQLEmployeePayload.class);
        } catch (Exception e){
            LOGGER.error("Failed to post");
        }
        LOGGER.info("Received a employee from MSSQL: "+mssqlEmployeePayload.getMssqlEmployee().toString());
    }

    @KafkaHandler
    public void handleDeleteMSSQLEmployeePayload(UserPayload userPayload){
        mergeService.deleteFromTwoDBMSWithTransaction(userPayload.getFirstName(), userPayload.getLastName());
        socketService.sendToTopic("/topic/mssql", sqlEmployeeService.getAllEmployee());
        socketService.sendToTopic("/topic/public", mergeService.mergeAllPerson());
        socketService.sendToTopic("/topic/mongo", mongoEmployeeService.getAllEmployee());
        LOGGER.info("Deleted from two DBMS");
    }

}
