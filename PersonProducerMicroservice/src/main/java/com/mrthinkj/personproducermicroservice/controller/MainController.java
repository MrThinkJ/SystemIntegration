package com.mrthinkj.personproducermicroservice.controller;

import com.mrthinkj.core.MergePerson;
import com.mrthinkj.core.UserPayload;
import com.mrthinkj.personproducermicroservice.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/merge")
public class MainController {
    EmployeeService employeeService;
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteMergeEmployeeByFirstNameAndLastName(@RequestBody UserPayload deletePayload) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(employeeService.deleteMergeEmployeeByFirstNameAndLastName(deletePayload));
    }

    @PostMapping("/update/{typeId}/{isCreated}")
    public ResponseEntity<String> updateMergeEmployeeByFirstNameAndLastName(@RequestBody MergePerson mergePerson,
                                                                            @PathVariable Integer typeId,
                                                                            @PathVariable boolean isCreated) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(employeeService.updateMergeEmployeeByFirstNameAndLastName(typeId, isCreated, mergePerson));
    }

    @PostMapping
    public ResponseEntity<String> createEmployee(@RequestBody MergePerson mergePerson) throws Exception {
        return new ResponseEntity<>(employeeService.createNewEmployee(mergePerson), HttpStatus.CREATED);
    }
}
