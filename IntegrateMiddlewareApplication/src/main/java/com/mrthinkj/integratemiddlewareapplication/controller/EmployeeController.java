package com.mrthinkj.integratemiddlewareapplication.controller;

import com.mrthinkj.core.MergePerson;
import com.mrthinkj.integratemiddlewareapplication.model.MongoEmployee;
import com.mrthinkj.integratemiddlewareapplication.model.SqlEmployee;
import com.mrthinkj.integratemiddlewareapplication.payload.UpdateInfo;
import com.mrthinkj.integratemiddlewareapplication.payload.UserPayload;
import com.mrthinkj.integratemiddlewareapplication.service.MergeService;
import com.mrthinkj.integratemiddlewareapplication.service.MongoEmployeeService;
import com.mrthinkj.integratemiddlewareapplication.service.SocketService;
import com.mrthinkj.integratemiddlewareapplication.service.SqlEmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@EnableWebSocket
@AllArgsConstructor
@RequestMapping("/api")
@Tag(
        name = "REST APIs for interacting with 2 DBMS"
)
public class EmployeeController {
    SqlEmployeeService sqlEmployeeService;
    MongoEmployeeService mongoEmployeeService;
    MergeService mergeService;
    SocketService socketService;

    @Operation(
            summary = "Get All Employee in SQLServer DB REST API",
            description = "Get All Employee in SQLServer DB REST API is used to retrieve data from DB which is set in application.properties"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 OK"
    )
    @GetMapping("/sql/employee")
    public ResponseEntity<List<SqlEmployee>> getAllSqlEmployee(){
        return ResponseEntity.ok(sqlEmployeeService.getAllEmployee());
    }

    @Operation(
            summary = "Get All Employee in MongoDB DB REST API",
            description = "Get All Employee in MongoDB DB REST API is used to retrieve data from DB which is set in application.properties"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 OK"
    )
    @GetMapping("/mongo/employee")
    public ResponseEntity<List<MongoEmployee>> getAllMongoEmployee(){
        return ResponseEntity.ok(mongoEmployeeService.getAllEmployee());
    }

    @Operation(
            summary = "Merge all employee REST API",
            description = "Merge all employee REST API is used to merge all employee in two DBMSs by fistName and lastName column"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 OK"
    )
    @GetMapping("/merge/employee")
    public ResponseEntity<List<MergePerson>> getAllMergeEmployee(){
        return ResponseEntity.ok(mergeService.mergeAllPerson());
    }

    @Operation(
            summary = "Delete from two DBMS API",
            description = "Delete from two DBMS by employee's firstname and lastname"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 OK"
    )
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteMergeEmployeeByFirstNameAndLastName(@RequestBody UserPayload deletePayload){
        String message = mergeService.deleteFromTwoDBMS(deletePayload.getFirstName(), deletePayload.getLastName());
        updateAllWS();
        return ResponseEntity.ok(message);
    }

    @Operation(
            summary = "Get Update Info API",
            description = "Get Update Info API is used to get info about which DBMS contains values or not"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 OK"
    )
    @GetMapping("/update")
    public ResponseEntity<UpdateInfo> getUpdateInfoByFirstNameAndLastName(@RequestBody UserPayload userPayload){
        return ResponseEntity.ok(mergeService.getUpdateInfo(userPayload.getFirstName(), userPayload.getLastName()));
    }

    @Operation(
            summary = "Update two DBMS API",
            description = "Update two DBMS API is used to update employee by firstname and lastname in both DBMS"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 OK"
    )
    @PostMapping("/update/{typeId}/{isCreated}")
    public ResponseEntity<String> updateMergeEmployeeByFirstNameAndLastName(@RequestBody MergePerson mergePerson,
                                                                                 @PathVariable Integer typeId,
                                                                                 @PathVariable boolean isCreated){
        String message = mergeService.updateFromTwoDBMS(typeId, isCreated, mergePerson);
        updateAllWS();
        return ResponseEntity.ok(message);
    }

    @Operation(
            summary = "Create Employee On Two DBMS API",
            description = "Create Employee On Two DBMS API used to create employee on both DBMS"
    )
    @ApiResponse(
        responseCode = "201",
            description = "HTTP Status 201 CREATED"
    )
    @PostMapping
    public ResponseEntity<Map<String, String>> createEmployee(@RequestBody MergePerson mergePerson){
        String message = mergeService.createToTwoDBMS(mergePerson);
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        updateAllWS();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Dashboard update Websocket API",
            description = "Dashboard update Websocket API is used to update dashboard when it's called"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 OK"
    )
    @GetMapping("/ws/update")
    public ResponseEntity<List<MergePerson>> getAllMergeEmployeeSocket(){
        List<MergePerson> mergePersonList = mergeService.mergeAllPerson();
        socketService.sendToTopic("/topic/public", mergePersonList);
        return ResponseEntity.ok(mergePersonList);
    }

    @GetMapping("/ws/all")
    public ResponseEntity<String> updateAllWS(){
        socketService.sendToTopic("/topic/public", mergeService.mergeAllPerson());
        socketService.sendToTopic("/topic/mssql", sqlEmployeeService.getAllEmployee());
        socketService.sendToTopic("/topic/mongo", mongoEmployeeService.getAllEmployee());
        return ResponseEntity.ok("Sent");
    }
}
