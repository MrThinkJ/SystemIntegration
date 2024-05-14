package com.mrthinkj.integratemiddlewareapplication.service.impl;

import com.mrthinkj.core.CreateOrUpdate;
import com.mrthinkj.core.MergePerson;
import com.mrthinkj.core.MongoEmployeePayload;
import com.mrthinkj.integratemiddlewareapplication.model.MongoEmployee;
import com.mrthinkj.integratemiddlewareapplication.model.SqlEmployee;
import com.mrthinkj.integratemiddlewareapplication.payload.UpdateInfo;
import com.mrthinkj.integratemiddlewareapplication.payload.UserPayload;
import com.mrthinkj.integratemiddlewareapplication.service.MergeService;
import com.mrthinkj.integratemiddlewareapplication.service.MongoEmployeeService;
import com.mrthinkj.integratemiddlewareapplication.service.SqlEmployeeService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MergeServiceImpl implements MergeService {
    MongoEmployeeService mongoEmployeeService;
    SqlEmployeeService sqlEmployeeService;

    public MergeServiceImpl(MongoEmployeeService mongoEmployeeService, SqlEmployeeService sqlEmployeeService) {
        this.mongoEmployeeService = mongoEmployeeService;
        this.sqlEmployeeService = sqlEmployeeService;
    }

    @Override
    public List<MergePerson> mergeAllPerson() {
        List<MongoEmployee> mongoEmployees = mongoEmployeeService.getAllEmployee();
        List<SqlEmployee> sqlEmployees = sqlEmployeeService.getAllEmployee();
        Map<String, SqlEmployee> sqlEmployeeMap = new HashMap<>();
        List<MergePerson> mergePersons = new ArrayList<>();
        for (SqlEmployee sqlEmployee : sqlEmployees) {
            String key = sqlEmployee.getFirstName() + sqlEmployee.getLastName();
            sqlEmployeeMap.put(key, sqlEmployee);
        }

        for (MongoEmployee mongoEmployee : mongoEmployees) {
            String key = mongoEmployee.getFirstName() + mongoEmployee.getLastName();
            SqlEmployee matchingSqlEmployee = sqlEmployeeMap.get(key);
            if (matchingSqlEmployee != null) {
                mergePersons.add(mergeTwoEmployees(matchingSqlEmployee, mongoEmployee));
                sqlEmployeeMap.remove(key);
            }
            else{
                mergePersons.add(mergeTwoEmployees(SqlEmployee.builder()
                        .firstName(mongoEmployee.getFirstName())
                        .lastName(mongoEmployee.getLastName()).build(),
                        mongoEmployee));
            }
        }
        for (SqlEmployee employee : sqlEmployeeMap.values())
            mergePersons.add(mergeTwoEmployees(employee, new MongoEmployee()));
        return mergePersons;
    }

    private MergePerson mergeTwoEmployees(SqlEmployee sqlEmployee, MongoEmployee mongoEmployee){
        return MergePerson.builder()
                .id(sqlEmployee.getId())
                .firstName(sqlEmployee.getFirstName())
                .lastName(sqlEmployee.getLastName())
                .benefitPlans(sqlEmployee.getBenefitPlans())
                .email(sqlEmployee.getEmail())
                .address1(sqlEmployee.getAddress1())
                .address2(sqlEmployee.getAddress2())
                .ethnicity(sqlEmployee.getEthnicity())
                .gender(sqlEmployee.isGender())
                .maritalStatus(sqlEmployee.getMaritalStatus())
                .middleInitial(sqlEmployee.getMiddleInitial())
                .city(sqlEmployee.getCity())
                .state(sqlEmployee.getState())
                .zip(sqlEmployee.getZip())
                .phoneNumber(sqlEmployee.getPhoneNumber())
                .socialSecurityNumber(sqlEmployee.getSocialSecurityNumber())
                .driversLicense(sqlEmployee.getDriversLicense())
                .shareholderStatus(sqlEmployee.isShareholderStatus())
                .vacationDays(mongoEmployee.getVacationDays())
                .paidToDate(mongoEmployee.getPaidToDate())
                .paidLastYear(mongoEmployee.getPaidLastYear())
                .payRate(mongoEmployee.getPayRate())
                .payRateId(mongoEmployee.getPayRateId())
                .createdAt(mongoEmployee.getCreatedAt())
                .updatedAt(mongoEmployee.getUpdatedAt())
                .build();
    }

    public String deleteFromTwoDBMS(String firstName, String lastName){
        boolean sql = sqlEmployeeService.deleteEmployeeByFirstNameAndLastName(firstName, lastName);
        boolean mongo = mongoEmployeeService.deleteEmployeeByFirstNameAndLastName(firstName, lastName);
        if (sql && !mongo)
            return "Delete in SQLServer";
        if (!sql && mongo)
            return "Delete in Mongo";
        if (sql)
            return "Delete in both DBMS";
        return "Two DBMS do not contain these two values";
    }

    @Override
    public String updateFromTwoDBMS(Integer typeId, boolean isCreated, MergePerson mergePerson) {
        String firstName = mergePerson.getFirstName();
        String lastName = mergePerson.getLastName();
        UpdateInfo updateInfo = UpdateInfo.getInfoFromInteger(typeId);
        if (updateInfo == UpdateInfo.All){
            sqlEmployeeService.updateEmployeeByFirstNameAndLastName(firstName, lastName, mergePerson);
            mongoEmployeeService.updateEmployeeByFirstNameAndLastName(firstName, lastName, mergePerson);
            return "Update in both DBMS";
        }
        if (updateInfo == UpdateInfo.Mongo){
            mongoEmployeeService.updateEmployeeByFirstNameAndLastName(firstName, lastName, mergePerson);
            if (isCreated)
                sqlEmployeeService.createNewEmployee(mapToSQL(mergePerson));
            return "Update in MongoDB and created in MSSQL";
        }
        sqlEmployeeService.updateEmployeeByFirstNameAndLastName(firstName, lastName, mergePerson);
        if (isCreated)
            mongoEmployeeService.createNewEmployee(mapToMongo(mergePerson));
        return "Update in MSSQL and created in MongoDB";
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public String createToTwoDBMS(MergePerson mergePerson) {
        String firstName = mergePerson.getFirstName();
        String lastName = mergePerson.getLastName();
        boolean sql = sqlEmployeeService.getEmployeeByFirstNameAndLastname(firstName, lastName) != null;
        boolean mongo = mongoEmployeeService.getEmployeeByFirstNameAndLastname(firstName, lastName) != null;
        if (sql && mongo)
            return "Already have this person in both DBMS";
        if (!sql && !mongo){
            sqlEmployeeService.createNewEmployee(mapToSQL(mergePerson));
            mongoEmployeeService.createNewEmployee(mapToMongo(mergePerson));
            return "Add this person to MSSQL and MongoDB";
        }
        if (!sql){
            sqlEmployeeService.createNewEmployee(mapToSQL(mergePerson));
            mongoEmployeeService.updateEmployeeByFirstNameAndLastName(firstName, lastName, mergePerson);
            return "Add this person to MSSQL and update to MongoDB";
        }
        mongoEmployeeService.createNewEmployee(mapToMongo(mergePerson));
        sqlEmployeeService.updateEmployeeByFirstNameAndLastName(firstName, lastName, mergePerson);
        return "Add this person to MongoDB and update to MSSQL";
    }

    @Override
    public UpdateInfo getUpdateInfo(String firstName, String lastName) {
        boolean sql = sqlEmployeeService.getEmployeeByFirstNameAndLastname(firstName, lastName) != null;
        boolean mongo = mongoEmployeeService.getEmployeeByFirstNameAndLastname(firstName, lastName) != null;
        if (sql && mongo)
            return UpdateInfo.All;
        if (!sql)
            return UpdateInfo.Mongo;
        return UpdateInfo.SqlServer;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public String deleteFromTwoDBMSWithTransaction(String firstName, String lastName) {
        try{
            mongoEmployeeService.deleteEmployeeByFirstNameAndLastName(firstName, lastName);
        } catch (Exception e){
            throw new RuntimeException("Failed to delete in MongoDB");
        }
        sqlEmployeeService.deleteEmployeeByFirstNameAndLastName(firstName, lastName);
        return "Delete "+firstName+" "+lastName+" successfully";
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void createToTwoDBMSFromMongoDBWithTransaction(MongoEmployeePayload mongoEmployeePayload) {
        mongoEmployeeService.handleCreateOrUpdate(mongoEmployeePayload);
        com.mrthinkj.core.MongoEmployee mongoEmployee = mongoEmployeePayload.getMongoEmployee();
        CreateOrUpdate createOrUpdate = CreateOrUpdate.builder()
                        .firstNameBefore(mongoEmployee.getFirstName())
                        .lastNameBefore(mongoEmployee.getLastName())
                        .firstNameAfter(null)
                        .lastNameAfter(null).build();
        sqlEmployeeService.createOrUpdate(createOrUpdate, "CREATE");
    }

    private MongoEmployee mapToMongo(MergePerson mergePerson){
        return MongoEmployee.builder()
                .firstName(mergePerson.getFirstName())
                .lastName(mergePerson.getLastName())
                .vacationDays(mergePerson.getVacationDays())
                .payRate(mergePerson.getPayRate())
                .paidLastYear(mergePerson.getPaidLastYear())
                .payRateId(mergePerson.getPayRateId())
                .paidToDate(mergePerson.getPaidToDate())
                .createdAt(mergePerson.getCreatedAt())
                .updatedAt(mergePerson.getUpdatedAt())
                .build();
    }

    private SqlEmployee mapToSQL(MergePerson mergePerson){
        return SqlEmployee.builder()
                .firstName(mergePerson.getFirstName())
                .lastName(mergePerson.getLastName())
                .benefitPlans(mergePerson.getBenefitPlans())
                .email(mergePerson.getEmail())
                .address1(mergePerson.getAddress1())
                .address2(mergePerson.getAddress2())
                .ethnicity(mergePerson.getEthnicity())
                .gender(mergePerson.isGender())
                .maritalStatus(mergePerson.getMaritalStatus())
                .middleInitial(mergePerson.getMiddleInitial())
                .city(mergePerson.getCity())
                .state(mergePerson.getState())
                .zip(mergePerson.getZip())
                .phoneNumber(mergePerson.getPhoneNumber())
                .socialSecurityNumber(mergePerson.getSocialSecurityNumber())
                .driversLicense(mergePerson.getDriversLicense())
                .shareholderStatus(mergePerson.isShareholderStatus())
                .build();
    }
}
