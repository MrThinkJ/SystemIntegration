package com.mrthinkj.personproducermicroservice.service.impl;

import com.mrthinkj.core.MessageTemplate;
import com.mrthinkj.core.MongoEmployee;
import com.mrthinkj.core.MongoEmployeePayload;
import com.mrthinkj.core.UserPayload;
import com.mrthinkj.personproducermicroservice.service.MongoEmployeeService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
public class MongoEmployeeServiceImpl implements MongoEmployeeService {
    KafkaTemplate<String, MongoEmployeePayload> mongoEmployeePayloadKafkaTemplate;
    KafkaTemplate<String, UserPayload> userPayloadKafkaTemplate;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    
    @Override
    public MessageTemplate createNewMongoEmployee(MongoEmployee mongoEmployee) throws ExecutionException, InterruptedException {
        String transactionId = UUID.randomUUID().toString();
        SendResult<String, MongoEmployeePayload> result = mongoEmployeePayloadKafkaTemplate.send
                ("mongo-person-topic", transactionId,
                        MongoEmployeePayload.builder()
                                .operation("CREATE")
                                .mongoEmployee(mongoEmployee)
                                .build()).get();
        logInfo(result);
        return MessageTemplate.builder()
                .transactionId(transactionId)
                .function("CREATE")
                .dbms("Mongo")
                .build();
    }

    @Override
    public MessageTemplate updateMongoEmployee(MongoEmployee mongoEmployee) throws ExecutionException, InterruptedException {
        String transactionId = UUID.randomUUID().toString();
        SendResult<String, MongoEmployeePayload> result = mongoEmployeePayloadKafkaTemplate.send
                ("mongo-person-topic", transactionId,
                        MongoEmployeePayload.builder()
                                .operation("UPDATE")
                                .mongoEmployee(mongoEmployee)
                                .build()).get();
        logInfo(result);
        return MessageTemplate.builder()
                .transactionId(transactionId)
                .function("UPDATE")
                .dbms("Mongo")
                .build();
    }

    @Override
    public MessageTemplate deleteMongoEmployee(UserPayload userPayload) throws ExecutionException, InterruptedException {
        String transactionId = UUID.randomUUID().toString();
        SendResult<String, UserPayload> result = userPayloadKafkaTemplate.send
                ("mongo-person-topic", transactionId, userPayload).get();
        logInfo(result);
        return MessageTemplate.builder()
                .transactionId(transactionId)
                .function("DELETE")
                .dbms("Mongo")
                .build();
    }

    private void logInfo(SendResult result){
        LOGGER.info("Partition: "+result.getRecordMetadata().partition());
        LOGGER.info("Topic: "+result.getRecordMetadata().topic());
        LOGGER.info("Offset: "+result.getRecordMetadata().offset());
    }
}
