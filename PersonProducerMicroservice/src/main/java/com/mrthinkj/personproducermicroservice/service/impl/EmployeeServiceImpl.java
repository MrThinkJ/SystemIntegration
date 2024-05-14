package com.mrthinkj.personproducermicroservice.service.impl;

import com.mrthinkj.core.MergePerson;
import com.mrthinkj.core.UpdateMergePersonPayload;
import com.mrthinkj.core.UserPayload;
import com.mrthinkj.personproducermicroservice.service.EmployeeService;
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
public class EmployeeServiceImpl implements EmployeeService {
    KafkaTemplate<String, MergePerson> personKafkaTemplate;
    KafkaTemplate<String, UpdateMergePersonPayload> updateMergePersonPayloadKafkaTemplate;
    KafkaTemplate<String, UserPayload> userPayloadKafkaTemplate;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public String createNewEmployee(MergePerson mergePerson) throws Exception {
        String employeeId = UUID.randomUUID().toString();
        SendResult<String, MergePerson> result = personKafkaTemplate.send("merge-person-topic", employeeId, mergePerson).get();
        LOGGER.info("Partition: "+result.getRecordMetadata().partition());
        LOGGER.info("Topic: "+result.getRecordMetadata().topic());
        LOGGER.info("Offset: "+result.getRecordMetadata().offset());
        return employeeId;
    }

    @Override
    public String updateMergeEmployeeByFirstNameAndLastName(Integer typeId, boolean isCreated, MergePerson mergePerson) throws ExecutionException, InterruptedException {
        String transactionId = UUID.randomUUID().toString();
        UpdateMergePersonPayload updateMergePersonPayload = UpdateMergePersonPayload.builder()
                .isCreated(isCreated)
                .typeId(typeId)
                .mergePerson(mergePerson)
                .build();
        SendResult<String, UpdateMergePersonPayload> result = updateMergePersonPayloadKafkaTemplate.send(
                "merge-person-topic", transactionId, updateMergePersonPayload).get();
        LOGGER.info("Partition: "+result.getRecordMetadata().partition());
        LOGGER.info("Topic: "+result.getRecordMetadata().topic());
        LOGGER.info("Offset: "+result.getRecordMetadata().offset());
        return transactionId;
    }

    @Override
    public String deleteMergeEmployeeByFirstNameAndLastName(UserPayload userPayload) throws ExecutionException, InterruptedException {
        String transactionId = UUID.randomUUID().toString();
        SendResult<String, UserPayload> result = userPayloadKafkaTemplate.send("merge-person-topic", transactionId, userPayload).get();
        LOGGER.info("Partition: "+result.getRecordMetadata().partition());
        LOGGER.info("Topic: "+result.getRecordMetadata().topic());
        LOGGER.info("Offset: "+result.getRecordMetadata().offset());
        return transactionId;
    }
}
