package com.saber.camel.spring_jpa_test.dto;

import com.saber.camel.spring_jpa_test.entities.Person;
import lombok.Data;

import java.util.List;
@Data
public class PersonResponseDto {
    private List<Person> persons;
}
