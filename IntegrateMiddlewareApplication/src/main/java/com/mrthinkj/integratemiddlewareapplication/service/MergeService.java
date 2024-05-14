package com.mrthinkj.integratemiddlewareapplication.service;

import com.mrthinkj.core.MergePerson;
import com.mrthinkj.core.MongoEmployeePayload;
import com.mrthinkj.integratemiddlewareapplication.payload.UpdateInfo;

import java.util.List;

public interface MergeService {
    List<MergePerson> mergeAllPerson();
    String deleteFromTwoDBMS(String firstName, String lastName);
    String updateFromTwoDBMS(Integer typeId, boolean isUpdated, MergePerson mergePerson);
    String createToTwoDBMS(MergePerson mergePerson);
    UpdateInfo getUpdateInfo(String firstName, String lastName);
    String deleteFromTwoDBMSWithTransaction(String firstName, String lastName);
    void createToTwoDBMSFromMongoDBWithTransaction(MongoEmployeePayload mongoEmployeePayload);
}
