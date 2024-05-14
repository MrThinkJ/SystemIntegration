package com.mrthinkj.integratemiddlewareapplication.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "Personal")
public class SqlEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Employee_ID")
    private int id;
    private String firstName;
    private String lastName;
    private String middleInitial;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private int zip;
    private String email;
    private String phoneNumber;
    private String socialSecurityNumber;
    private String driversLicense;
    private String maritalStatus;
    private boolean gender;
    private boolean shareholderStatus;
    private Integer benefitPlans;
    private String ethnicity;

    @Override
    public String toString() {
        return "SqlEmployee{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleInitial='" + middleInitial + '\'' +
                ", address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zip=" + zip +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", socialSecurityNumber='" + socialSecurityNumber + '\'' +
                ", driversLicense='" + driversLicense + '\'' +
                ", maritalStatus='" + maritalStatus + '\'' +
                ", gender=" + gender +
                ", shareholderStatus=" + shareholderStatus +
                ", benefitPlans=" + benefitPlans +
                ", ethnicity='" + ethnicity + '\'' +
                '}';
    }
}
