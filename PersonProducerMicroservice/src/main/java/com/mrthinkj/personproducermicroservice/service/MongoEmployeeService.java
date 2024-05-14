package com.mrthinkj.personproducermicroservice.service;

import com.mrthinkj.core.MessageTemplate;
import com.mrthinkj.core.MongoEmployee;
import com.mrthinkj.core.UserPayload;

import java.util.concurrent.ExecutionException;

public interface MongoEmployeeService {
    MessageTemplate createNewMongoEmployee(MongoEmployee mongoEmployee) throws ExecutionException, InterruptedException;
    MessageTemplate updateMongoEmployee(MongoEmployee mongoEmployee) throws ExecutionException, InterruptedException;
    MessageTemplate deleteMongoEmployee(UserPayload userPayload) throws ExecutionException, InterruptedException;
}
