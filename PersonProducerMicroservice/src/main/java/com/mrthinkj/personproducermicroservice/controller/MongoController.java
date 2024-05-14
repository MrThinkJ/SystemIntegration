package com.mrthinkj.personproducermicroservice.controller;

import com.mrthinkj.core.MessageTemplate;
import com.mrthinkj.core.MongoEmployee;
import com.mrthinkj.core.UserPayload;
import com.mrthinkj.personproducermicroservice.service.MongoEmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/mongo")
@AllArgsConstructor
public class MongoController {
    MongoEmployeeService mongoEmployeeService;
    @PostMapping
    public ResponseEntity<MessageTemplate> createNewEmployee(@RequestBody MongoEmployee mongoEmployee) throws ExecutionException, InterruptedException {
        return new ResponseEntity<>(mongoEmployeeService.createNewMongoEmployee(mongoEmployee), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<MessageTemplate> updateEmployee(@RequestBody MongoEmployee mongoEmployee) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(mongoEmployeeService.updateMongoEmployee(mongoEmployee));
    }

    @DeleteMapping
    public ResponseEntity<MessageTemplate> deleteEmployee(@RequestBody UserPayload userPayload) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(mongoEmployeeService.deleteMongoEmployee(userPayload));
    }
}
