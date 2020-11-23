package com.vaadin.flow.connect.data.endpoint;

import java.util.List;
import java.util.Optional;

import com.vaadin.flow.connect.data.entity.Person;
import com.vaadin.flow.connect.data.servcie.PersonService;
import com.vaadin.flow.server.connect.Deferrable;
import com.vaadin.flow.server.connect.Endpoint;
import com.vaadin.flow.server.connect.auth.AnonymousAllowed;

import org.springframework.beans.factory.annotation.Autowired;

@Endpoint
@AnonymousAllowed
public class PersonEndpoint {

    private PersonService service;

    public PersonEndpoint(@Autowired PersonService service) {
        this.service = service;
    }

    protected PersonService getService() {
        return service;
    }

    public List<Person> list() {
        return service.list();
    }

    public Optional<Person> get(Integer id) {
        return service.get(id);
    }

    @Deferrable
    public Person update(Person entity) {
        return service.update(entity);
    }

    public void delete(Integer id) {
        service.delete(id);
    }

    public int count() {
        return service.count();
    }

    public void deleteAll() {
        service.deleteAll();
    }

}
