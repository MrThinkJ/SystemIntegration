package com.mrthinkj.integratemiddlewareapplication.handler;

import com.mrthinkj.core.CreateOrUpdate;
import com.mrthinkj.core.MongoEmployeePayload;
import com.mrthinkj.core.UserPayload;
import com.mrthinkj.integratemiddlewareapplication.model.MongoEmployee;
import com.mrthinkj.integratemiddlewareapplication.service.MergeService;
import com.mrthinkj.integratemiddlewareapplication.service.MongoEmployeeService;
import com.mrthinkj.integratemiddlewareapplication.service.SocketService;
import com.mrthinkj.integratemiddlewareapplication.service.SqlEmployeeService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@AllArgsConstructor
@KafkaListener(topics = "mongo-person-topic")
public class MongoEmployeeEventHandler {
    MongoEmployeeService mongoEmployeeService;
    SqlEmployeeService sqlEmployeeService;
    MergeService mergeService;
    SocketService socketService;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @KafkaHandler
    public void handleMongoEmployeePayload(MongoEmployeePayload mongoEmployeePayload){
        mergeService.createToTwoDBMSFromMongoDBWithTransaction(mongoEmployeePayload);
        socketService.sendToTopic("/topic/mssql", sqlEmployeeService.getAllEmployee());
        socketService.sendToTopic("/topic/public", mergeService.mergeAllPerson());
        socketService.sendToTopic("/topic/mongo", mongoEmployeeService.getAllEmployee());
        LOGGER.info("Create successfully");
    }

    @KafkaHandler
    public void handleDeleteMongoEmployee(UserPayload userPayload){
        mergeService.deleteFromTwoDBMSWithTransaction(userPayload.getFirstName(), userPayload.getLastName());
        socketService.sendToTopic("/topic/mssql", sqlEmployeeService.getAllEmployee());
        socketService.sendToTopic("/topic/public", mergeService.mergeAllPerson());
        socketService.sendToTopic("/topic/mongo", mongoEmployeeService.getAllEmployee());
        LOGGER.info("Deleted from two DBMS");
    }
}
