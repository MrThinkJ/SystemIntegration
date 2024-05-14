package com.mrthinkj.integratemiddlewareapplication.controller;

import com.mrthinkj.core.CreateOrUpdate;
import com.mrthinkj.integratemiddlewareapplication.model.MongoEmployee;
import com.mrthinkj.integratemiddlewareapplication.service.MergeService;
import com.mrthinkj.integratemiddlewareapplication.service.MongoEmployeeService;
import com.mrthinkj.integratemiddlewareapplication.service.SocketService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mongo")
@AllArgsConstructor
public class MongoEmployeeController {
    MongoEmployeeService mongoEmployeeService;
    SocketService socketService;
    MergeService mergeService;
    @GetMapping
    public ResponseEntity<List<MongoEmployee>> updateSocket(){
        List<MongoEmployee> mongoEmployees = mongoEmployeeService.getAllEmployee();
        socketService.sendToTopic("/topic/mongo", mongoEmployees);
        return ResponseEntity.ok(mongoEmployees);
    }
    @PostMapping
    public ResponseEntity<Boolean> createNewEmployee(@RequestBody MongoEmployee mongoEmployee){
        try{
            mongoEmployeeService.createNewEmployee(mongoEmployee);
            return ResponseEntity.ok(true);
        } catch (Exception e){
            return ResponseEntity.ok(false);
        }
    }

    @PutMapping("/createOrUpdate")
    public ResponseEntity<Boolean> createOrUpdateEmployee(@RequestBody CreateOrUpdate createOrUpdateMongo){
        try{
            mongoEmployeeService.createOrUpdateFromMSSQL(createOrUpdateMongo);
            return ResponseEntity.ok(true);
        } catch (Exception e){
            return ResponseEntity.ok(false);
        }
    }

    @DeleteMapping
    public ResponseEntity<Boolean> deleteEmployee(@RequestBody MongoEmployee mongoEmployee){
        boolean success = mongoEmployeeService.deleteEmployeeByFirstNameAndLastName(mongoEmployee.getFirstName(), mongoEmployee.getLastName());
        if (success)
            socketService.sendToTopic("topic/mongo", mergeService.mergeAllPerson());
        return ResponseEntity.ok(success);
    }
}
