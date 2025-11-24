/*
 * Copyright by https://conxult.de
 */
package de.hogi.adesso.ai.credit.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 *
 * @author joerg
 */
@Getter @Setter @Accessors(chain = true)
public class Person {

    String name;
    String firstName;
    String lastName;
    int    currentAge;
    int    retirementAge;
    int    birthYear;
    int    birthMonth;
    String gender;
    String address;
    String apartment;
    String state;
    String city;
    String zip;
    float  latitude;
    float  longitude;
    float  incomeZip;
    float  incomePerson;
    float  debt;
    int    ficoScore;
    int    numCreditCards;

}
