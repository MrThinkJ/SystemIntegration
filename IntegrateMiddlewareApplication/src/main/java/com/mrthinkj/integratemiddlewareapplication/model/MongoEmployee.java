package com.mrthinkj.integratemiddlewareapplication.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("employees")
public class MongoEmployee {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private int vacationDays;
    private int paidToDate;
    private int paidLastYear;
    private int payRate;
    private int payRateId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
