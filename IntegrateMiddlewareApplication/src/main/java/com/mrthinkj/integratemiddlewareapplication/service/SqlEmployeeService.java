package com.mrthinkj.integratemiddlewareapplication.service;

import com.mrthinkj.core.CreateOrUpdate;
import com.mrthinkj.core.MergePerson;
import com.mrthinkj.integratemiddlewareapplication.model.SqlEmployee;

import java.util.List;

public interface SqlEmployeeService {
    List<SqlEmployee> getAllEmployee();
    boolean deleteEmployeeByFirstNameAndLastName(String firstName, String lastName);
    SqlEmployee getEmployeeByFirstNameAndLastname(String firstName, String lastName);
    void createNewEmployee(SqlEmployee sqlEmployee);
    void updateEmployeeByFirstNameAndLastName(String firstName, String lastName, MergePerson mergePerson);
    void createOrUpdate(CreateOrUpdate createOrUpdate, String operation);
}
