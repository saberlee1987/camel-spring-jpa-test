package com.saber.camel.spring_jpa_test.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "persons")
@Data
@NamedQuery(name = "Person.findAll",query = "SELECT p FROM Person p")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "firstName",length = 70)
    private String firstName;
    @Column(name = "lastName",length = 90)
    private String lastName;
    @Column(name = "age")
    private Integer age;
}
