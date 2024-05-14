package com.mrthinkj.integratemiddlewareapplication.repository.mongodao;

import com.mrthinkj.integratemiddlewareapplication.model.MongoEmployee;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoEmployeeRepository extends MongoRepository<MongoEmployee, String> {
    MongoEmployee findByFirstNameAndLastName(String firstName, String lastName);
}
