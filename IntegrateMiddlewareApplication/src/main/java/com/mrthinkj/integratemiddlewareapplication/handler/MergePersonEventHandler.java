package com.mrthinkj.integratemiddlewareapplication.handler;

import com.mrthinkj.core.MergePerson;
import com.mrthinkj.core.UpdateMergePersonPayload;
import com.mrthinkj.core.UserPayload;
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

@Component
@KafkaListener(topics = "merge-person-topic")
@AllArgsConstructor
public class MergePersonEventHandler {
    MergeService mergeService;
    SqlEmployeeService sqlEmployeeService;
    MongoEmployeeService mongoEmployeeService;
    SocketService socketService;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @KafkaHandler
    public void handleCreatePerson(MergePerson mergePerson){
        mergeService.createToTwoDBMS(mergePerson);
        socketService.sendToTopic("/topic/mssql", sqlEmployeeService.getAllEmployee());
        socketService.sendToTopic("/topic/public", mergeService.mergeAllPerson());
        socketService.sendToTopic("/topic/mongo", mongoEmployeeService.getAllEmployee());
        LOGGER.info("Created");
    }

    @KafkaHandler
    public void handleUpdatePerson(UpdateMergePersonPayload updateMergePersonPayload){
        mergeService.updateFromTwoDBMS(
                updateMergePersonPayload.getTypeId(), updateMergePersonPayload.isCreated(), updateMergePersonPayload.getMergePerson());
        socketService.sendToTopic("/topic/mssql", sqlEmployeeService.getAllEmployee());
        socketService.sendToTopic("/topic/public", mergeService.mergeAllPerson());
        socketService.sendToTopic("/topic/mongo", mongoEmployeeService.getAllEmployee());
        LOGGER.info("Updated");
    }

    @KafkaHandler
    public void handleDeletePerson(UserPayload userPayload){
        mergeService.deleteFromTwoDBMSWithTransaction(userPayload.getFirstName(), userPayload.getLastName());
        socketService.sendToTopic("/topic/mssql", sqlEmployeeService.getAllEmployee());
        socketService.sendToTopic("/topic/public", mergeService.mergeAllPerson());
        socketService.sendToTopic("/topic/mongo", mongoEmployeeService.getAllEmployee());
        LOGGER.info("Deleted from two DBMS");
    }
}
