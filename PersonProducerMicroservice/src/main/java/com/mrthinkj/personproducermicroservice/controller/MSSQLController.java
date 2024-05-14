package com.mrthinkj.personproducermicroservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mrthinkj.core.MSSQLEmployee;
import com.mrthinkj.core.MessageTemplate;
import com.mrthinkj.core.UserPayload;
import com.mrthinkj.personproducermicroservice.service.MSSQLEmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/sql")
@AllArgsConstructor
public class MSSQLController {
    MSSQLEmployeeService mssqlEmployeeService;
    @PostMapping
    public ResponseEntity<MessageTemplate> createNewEmployee(@RequestBody String sqlEmployee) throws ExecutionException, InterruptedException, JsonProcessingException {
        Gson gson = new Gson();
        MSSQLEmployee mssqlEmployee = gson.fromJson(sqlEmployee, MSSQLEmployee.class);
        return new ResponseEntity<>(mssqlEmployeeService.createNewMSSQLEmployee(mssqlEmployee), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<MessageTemplate> updateEmployee(@RequestBody MSSQLEmployee sqlEmployee) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(mssqlEmployeeService.updateMSSQLEmployee(sqlEmployee));
    }

    @DeleteMapping
    public ResponseEntity<MessageTemplate> deleteEmployee(@RequestBody String userPayload) throws ExecutionException, InterruptedException {
        Gson gson = new Gson();
        UserPayload user = gson.fromJson(userPayload, UserPayload.class);
        return ResponseEntity.ok(mssqlEmployeeService.deleteMSSQLEmployee(user));
    }
}
