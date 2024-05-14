package com.mrthinkj.integratemiddlewareapplication.repository.sqldao;

import com.mrthinkj.integratemiddlewareapplication.model.MongoEmployee;
import com.mrthinkj.integratemiddlewareapplication.model.SqlEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SqlEmployeeRepository extends JpaRepository<SqlEmployee, Integer> {
    SqlEmployee findByFirstNameAndLastName(String firstName, String lastName);
}
