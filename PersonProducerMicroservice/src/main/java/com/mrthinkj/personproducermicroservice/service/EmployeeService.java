package com.mrthinkj.personproducermicroservice.service;

import com.mrthinkj.core.MergePerson;
import com.mrthinkj.core.UserPayload;

import java.util.concurrent.ExecutionException;

public interface EmployeeService {
    String createNewEmployee(MergePerson mergePerson) throws Exception;
    String updateMergeEmployeeByFirstNameAndLastName(Integer typeId, boolean isCreated, MergePerson mergePerson) throws ExecutionException, InterruptedException;
    String deleteMergeEmployeeByFirstNameAndLastName(UserPayload userPayload) throws ExecutionException, InterruptedException;
}
