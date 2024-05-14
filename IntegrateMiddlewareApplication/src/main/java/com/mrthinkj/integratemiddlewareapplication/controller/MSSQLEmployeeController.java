package com.mrthinkj.integratemiddlewareapplication.controller;

import com.mrthinkj.integratemiddlewareapplication.model.SqlEmployee;
import com.mrthinkj.integratemiddlewareapplication.payload.UserPayload;
import com.mrthinkj.integratemiddlewareapplication.service.MergeService;
import com.mrthinkj.integratemiddlewareapplication.service.SocketService;
import com.mrthinkj.integratemiddlewareapplication.service.SqlEmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sql")
@AllArgsConstructor
public class MSSQLEmployeeController {
    SqlEmployeeService sqlEmployeeService;
    MergeService mergeService;
    SocketService socketService;
    @GetMapping
    public ResponseEntity<List<SqlEmployee>> updateSocket(){
        List<SqlEmployee> sqlEmployees = sqlEmployeeService.getAllEmployee();
        socketService.sendToTopic("/topic/mssql", sqlEmployees);
        return ResponseEntity.ok(sqlEmployees);
    }

    @PostMapping
    public ResponseEntity<Boolean> createNewEmployee(@RequestBody SqlEmployee sqlEmployee){
        try {
            sqlEmployeeService.createNewEmployee(sqlEmployee);
            return ResponseEntity.ok(true);
        } catch (Exception e){
            return ResponseEntity.ok(false);
        }
    }

    @DeleteMapping
    public ResponseEntity<Boolean> deleteEmployee(@RequestBody UserPayload userPayload){
        boolean state = sqlEmployeeService.deleteEmployeeByFirstNameAndLastName(userPayload.getFirstName(), userPayload.getLastName());
        return ResponseEntity.ok(state);
    }
}
