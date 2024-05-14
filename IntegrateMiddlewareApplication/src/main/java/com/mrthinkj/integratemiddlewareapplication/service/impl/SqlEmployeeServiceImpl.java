package com.mrthinkj.integratemiddlewareapplication.service.impl;

import com.mrthinkj.core.CreateOrUpdate;
import com.mrthinkj.core.MergePerson;
import com.mrthinkj.integratemiddlewareapplication.model.SqlEmployee;
import com.mrthinkj.integratemiddlewareapplication.repository.sqldao.SqlEmployeeRepository;
import com.mrthinkj.integratemiddlewareapplication.service.SqlEmployeeService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class SqlEmployeeServiceImpl implements SqlEmployeeService {
    SqlEmployeeRepository sqlEmployeeRepository;

    public SqlEmployeeServiceImpl(SqlEmployeeRepository sqlEmployeeRepository) {
        this.sqlEmployeeRepository = sqlEmployeeRepository;
    }

    @Override
    public List<SqlEmployee> getAllEmployee() {
        return sqlEmployeeRepository.findAll();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public boolean deleteEmployeeByFirstNameAndLastName(String firstName, String lastName) {
        SqlEmployee sqlEmployee = sqlEmployeeRepository.findByFirstNameAndLastName(firstName, lastName);
        if (sqlEmployee == null)
            return false;
        try{
            sqlEmployeeRepository.delete(sqlEmployee);
        } catch (Exception e){
            throw new RuntimeException("Failed to delete in sql server");
        }
        return true;
    }

    @Override
    public SqlEmployee getEmployeeByFirstNameAndLastname(String firstName, String lastName) {
        return sqlEmployeeRepository.findByFirstNameAndLastName(firstName, lastName);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void createNewEmployee(SqlEmployee sqlEmployee) {
        sqlEmployeeRepository.save(sqlEmployee);
    }

    @Override
    public void updateEmployeeByFirstNameAndLastName(String firstName, String lastName, MergePerson mergePerson) {
        SqlEmployee sqlEmployee = getEmployeeByFirstNameAndLastname(firstName, lastName);
        SqlEmployee updatedEmployee = buildSqlEmployee(mergePerson);
        updatedEmployee.setId(sqlEmployee.getId());
        updatedEmployee.setFirstName(sqlEmployee.getFirstName());
        updatedEmployee.setLastName(sqlEmployee.getLastName());
        sqlEmployeeRepository.save(updatedEmployee);
    }

    @Override
    public void createOrUpdate(CreateOrUpdate createOrUpdate, String operation) {
        SqlEmployee sqlEmployee = getEmployeeByFirstNameAndLastname(createOrUpdate.getFirstNameBefore(), createOrUpdate.getLastNameBefore());
        if (sqlEmployee != null){
            if (operation.equals("CREATE"))
                return;
            if (Objects.equals(createOrUpdate.getFirstNameBefore(), createOrUpdate.getFirstNameAfter())
                    && Objects.equals(createOrUpdate.getLastNameBefore(), createOrUpdate.getLastNameAfter()))
                return;
            sqlEmployee.setFirstName(createOrUpdate.getFirstNameAfter());
            sqlEmployee.setLastName(createOrUpdate.getLastNameAfter());
            sqlEmployeeRepository.save(sqlEmployee);
        }
        if (sqlEmployee == null){
            sqlEmployee = new SqlEmployee();
        }
        sqlEmployee.setFirstName(createOrUpdate.getFirstNameBefore());
        sqlEmployee.setLastName(createOrUpdate.getLastNameBefore());
        sqlEmployeeRepository.save(sqlEmployee);
    }

    private SqlEmployee buildSqlEmployee(MergePerson mergePerson){
        return SqlEmployee.builder()
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
