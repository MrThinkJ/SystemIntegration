package com.mrthinkj.integratemiddlewareapplication.service.impl;

import com.mrthinkj.core.CreateOrUpdate;
import com.mrthinkj.core.MergePerson;
import com.mrthinkj.core.MongoEmployeePayload;
import com.mrthinkj.integratemiddlewareapplication.model.MongoEmployee;
import com.mrthinkj.integratemiddlewareapplication.repository.mongodao.MongoEmployeeRepository;
import com.mrthinkj.integratemiddlewareapplication.service.MongoEmployeeService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

@Service
public class MongoEmployeeServiceImpl implements MongoEmployeeService {
    MongoEmployeeRepository mongoEmployeeRepository;

    public MongoEmployeeServiceImpl(MongoEmployeeRepository mongoEmployeeRepository) {
        this.mongoEmployeeRepository = mongoEmployeeRepository;
    }

    @Override
    public List<MongoEmployee> getAllEmployee() {
        return mongoEmployeeRepository.findAll();
    }

    @Override
    public boolean deleteEmployeeByFirstNameAndLastName(String firstName, String lastName) {
        MongoEmployee mongoEmployee = mongoEmployeeRepository.findByFirstNameAndLastName(firstName, lastName);
        if (mongoEmployee == null)
            return false;
        try{
            mongoEmployeeRepository.delete(mongoEmployee);
        } catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public MongoEmployee getEmployeeByFirstNameAndLastname(String firstName, String lastName) {
        return mongoEmployeeRepository.findByFirstNameAndLastName(firstName, lastName);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public MongoEmployee createNewEmployee(MongoEmployee mongoEmployee) {
        if (mongoEmployeeRepository.findByFirstNameAndLastName(mongoEmployee.getFirstName(),mongoEmployee.getLastName()) != null)
            return mongoEmployee;
        mongoEmployee.setCreatedAt(LocalDateTime.now());
        mongoEmployee.setUpdatedAt(LocalDateTime.now());
        return mongoEmployeeRepository.save(mongoEmployee);
    }

    @Override
    public MongoEmployee updateEmployeeByFirstNameAndLastName(String firstName, String lastName, MergePerson mergePerson) {
        MongoEmployee mongoEmployee = getEmployeeByFirstNameAndLastname(firstName, lastName);
        if (mongoEmployee == null)
            return null;
        MongoEmployee newMongoEmployee = MongoEmployee.builder()
                .id(mongoEmployee.getId())
                .firstName(mongoEmployee.getFirstName())
                .lastName(mongoEmployee.getLastName())
                .vacationDays(mergePerson.getVacationDays())
                .payRate(mergePerson.getPayRate())
                .paidLastYear(mergePerson.getPaidLastYear())
                .payRateId(mergePerson.getPayRateId())
                .paidToDate(mergePerson.getPaidToDate())
                .createdAt(mergePerson.getCreatedAt())
                .updatedAt(mergePerson.getUpdatedAt())
                .build();
        return mongoEmployeeRepository.save(newMongoEmployee);
    }

    @Override
    public void createOrUpdateFromMSSQL(CreateOrUpdate createOrUpdateMongo) {
        MongoEmployee mongoEmployee = getEmployeeByFirstNameAndLastname(createOrUpdateMongo.getFirstNameBefore(), createOrUpdateMongo.getLastNameBefore());
        if (mongoEmployee == null){
            MongoEmployee mongoEmployeeUp = getEmployeeByFirstNameAndLastname(createOrUpdateMongo.getFirstNameAfter(), createOrUpdateMongo.getLastNameAfter());
            if (mongoEmployeeUp != null){
                return;
            }
            MongoEmployee mongoEmployee1 = MongoEmployee.builder()
                    .firstName(createOrUpdateMongo.getFirstNameAfter())
                    .lastName(createOrUpdateMongo.getLastNameAfter())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now()).build();
            mongoEmployeeRepository.save(mongoEmployee1);
            return;
        }
        mongoEmployee.setFirstName(createOrUpdateMongo.getFirstNameAfter());
        mongoEmployee.setLastName(createOrUpdateMongo.getLastNameAfter());
        mongoEmployee.setUpdatedAt(LocalDateTime.now());
        mongoEmployeeRepository.save(mongoEmployee);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void handleCreateOrUpdate(MongoEmployeePayload mongoEmployeePayload) {
        try{
            com.mrthinkj.core.MongoEmployee mongoEmployeeReceived = mongoEmployeePayload.getMongoEmployee();
            MongoEmployee mongoEmployee = mongoEmployeeRepository.findByFirstNameAndLastName(mongoEmployeeReceived.getFirstName(), mongoEmployeeReceived.getLastName());
            if (mongoEmployee == null){
                mongoEmployee = new MongoEmployee();
                mongoEmployee.setCreatedAt(LocalDateTime.now());
            }
            updateStringProperty(mongoEmployeeReceived.getFirstName(), mongoEmployee.getFirstName(), mongoEmployee::setFirstName);
            updateStringProperty(mongoEmployeeReceived.getLastName(), mongoEmployee.getLastName(), mongoEmployee::setLastName);
            updateIntProperty(mongoEmployeeReceived.getPayRate(), mongoEmployee.getPayRate(), mongoEmployee::setPayRate);
            updateIntProperty(mongoEmployeeReceived.getPaidLastYear(), mongoEmployee.getPaidLastYear(), mongoEmployee::setPaidLastYear);
            updateIntProperty(mongoEmployeeReceived.getPaidToDate(), mongoEmployee.getPaidToDate(), mongoEmployee::setPaidToDate);
            updateIntProperty(mongoEmployeeReceived.getPayRateId(), mongoEmployee.getPayRateId(), mongoEmployee::setPayRateId);
            updateIntProperty(mongoEmployeeReceived.getVacationDays(), mongoEmployee.getVacationDays(), mongoEmployee::setVacationDays);
            mongoEmployee.setUpdatedAt(LocalDateTime.now());
            mongoEmployeeRepository.save(mongoEmployee);
        } catch (Exception e){
            throw new RuntimeException("Failed to create or update to mongodb");
        }
    }

    @Override
    public MongoEmployee getById(String id) {
        return mongoEmployeeRepository.findById(id).orElseThrow(()-> new RuntimeException("Not found mongo employee with id: "+id));
    }

    private void updateStringProperty(String receivedValue, String currentValue, Consumer<String> setter) {
        setter.accept(receivedValue == null ? currentValue : receivedValue);
    }

    private void updateIntProperty(int receivedValue, int currentValue, IntConsumer setter) {
        setter.accept(receivedValue == 0 ? currentValue : receivedValue);
    }
}
