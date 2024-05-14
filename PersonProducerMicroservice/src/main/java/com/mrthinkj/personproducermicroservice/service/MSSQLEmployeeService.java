package com.mrthinkj.personproducermicroservice.service;

import com.mrthinkj.core.MSSQLEmployee;
import com.mrthinkj.core.MessageTemplate;
import com.mrthinkj.core.UserPayload;

import java.util.concurrent.ExecutionException;

public interface MSSQLEmployeeService {
    MessageTemplate createNewMSSQLEmployee(MSSQLEmployee mssqlEmployee) throws ExecutionException, InterruptedException;
    MessageTemplate updateMSSQLEmployee(MSSQLEmployee mssqlEmployee) throws ExecutionException, InterruptedException;
    MessageTemplate deleteMSSQLEmployee(UserPayload userPayload) throws ExecutionException, InterruptedException;
}
