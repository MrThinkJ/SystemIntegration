package com.mrthinkj.personproducermicroservice.service.impl;

import com.mrthinkj.core.*;
import com.mrthinkj.personproducermicroservice.service.MSSQLEmployeeService;
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
public class MSSQLEmployeeServiceImpl implements MSSQLEmployeeService {
    KafkaTemplate<String, MSSQLEmployeePayload> mssqlEmployeePayloadKafkaTemplate;
    KafkaTemplate<String, UserPayload> userPayloadKafkaTemplate;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public MessageTemplate createNewMSSQLEmployee(MSSQLEmployee mssqlEmployee) throws ExecutionException, InterruptedException {
        String transactionId = UUID.randomUUID().toString();
        LOGGER.info(mssqlEmployee.toString());
        SendResult<String, MSSQLEmployeePayload> result = mssqlEmployeePayloadKafkaTemplate.send
                ("mssql-person-topic", transactionId,
                        MSSQLEmployeePayload.builder()
                                .operation("CREATE")
                                .mssqlEmployee(mssqlEmployee)
                                .build()).get();
        logInfo(result);
        return MessageTemplate.builder()
                .transactionId(transactionId)
                .function("CREATE")
                .dbms("MSSQL")
                .build();
    }

    @Override
    public MessageTemplate updateMSSQLEmployee(MSSQLEmployee mssqlEmployee) throws ExecutionException, InterruptedException {
        String transactionId = UUID.randomUUID().toString();
        SendResult<String, MSSQLEmployeePayload> result = mssqlEmployeePayloadKafkaTemplate.send
                ("mssql-person-topic", transactionId,
                        MSSQLEmployeePayload.builder()
                                .operation("UPDATE")
                                .mssqlEmployee(mssqlEmployee)
                                .build()).get();
        logInfo(result);
        return MessageTemplate.builder()
                .transactionId(transactionId)
                .function("UPDATE")
                .dbms("MSSQL")
                .build();
    }

    @Override
    public MessageTemplate deleteMSSQLEmployee(UserPayload userPayload) throws ExecutionException, InterruptedException {
        String transactionId = UUID.randomUUID().toString();
        SendResult<String, UserPayload> result = userPayloadKafkaTemplate.send("mssql-person-topic", transactionId, userPayload).get();
        logInfo(result);
        return MessageTemplate.builder()
                .transactionId(transactionId)
                .function("DELETE")
                .dbms("MSSQL")
                .build();
    }

    private void logInfo(SendResult result){
        LOGGER.info("Partition: "+result.getRecordMetadata().partition());
        LOGGER.info("Topic: "+result.getRecordMetadata().topic());
        LOGGER.info("Offset: "+result.getRecordMetadata().offset());
    }
}
