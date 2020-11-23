package com.vaadin.flow.connect.data.servcie;

import com.vaadin.flow.connect.data.entity.Person;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Integer> {

}