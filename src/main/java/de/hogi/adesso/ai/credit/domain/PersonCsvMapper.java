package de.hogi.adesso.ai.credit.domain;

import java.util.Map;
import lombok.extern.java.Log;

@Log
public class PersonCsvMapper {

    public static Person toPerson(Map<String, String> columns) {
        var person = new Person();

        columns.forEach((key, value) -> toPerson(person, key, value));

        return person;
    }

    static void toPerson(Person person, String header, String column) {
        switch (header) {
            case "Person"                      -> person.setName(column);
            case "Current Age"                 -> person.setCurrentAge(Integer.parseInt(column));
            case "Retirement Age"              -> person.setRetirementAge(Integer.parseInt(column));
            case "Birth Year"                  -> person.setBirthYear(Integer.parseInt(column));
            case "Birth Month"                 -> person.setBirthMonth(Integer.parseInt(column));
            case "Gender"                      -> person.setGender(column);
            case "Address"                     -> person.setAddress(column);
            case "Apartment"                   -> person.setApartment(column);
            case "City"                        -> person.setCity(column);
            case "State"                       -> person.setState(column);
            case "Zipcode"                     -> person.setZip(column);
            case "Latitude"                    -> person.setLatitude(Float.parseFloat(column));
            case "Longitude"                   -> person.setLongitude(Float.parseFloat(column));
            case "Per Capita Income - Zipcode" -> person.setIncomeZip(Float.parseFloat(column.substring(1)));
            case "Yearly Income - Person"      -> person.setIncomePerson(Float.parseFloat(column.substring(1)));
            case "Total Debt"                  -> person.setDebt(Float.parseFloat(column.substring(1)));
            case "FICO Score"                  -> person.setFicoScore(Integer.parseInt(column));
            case "Num Credit Cards"            -> person.setNumCreditCards(Integer.parseInt(column));
            default                            -> log.warning("csv header %s unknown".formatted(header));
        }
    }

}
