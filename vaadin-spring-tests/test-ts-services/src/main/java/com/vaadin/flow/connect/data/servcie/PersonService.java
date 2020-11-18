package com.vaadin.flow.connect.data.servcie;

import java.util.List;
import java.util.Optional;

import com.vaadin.flow.connect.data.entity.Person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    private PersonRepository repository;

    public PersonService(@Autowired PersonRepository repository) {
        this.repository = repository;
    }

    public List<Person> list() {
        return repository.findAll();
    }

    public Optional<Person> get(Integer id) {
        return repository.findById(id);
    }

    public Person update(Person entity) {
        return repository.save(entity);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public int count() {
        return (int) repository.count();
    }

    public void deleteAll() {
        repository.deleteAll();
    }

}
