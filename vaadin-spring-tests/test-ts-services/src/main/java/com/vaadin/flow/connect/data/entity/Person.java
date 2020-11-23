package com.vaadin.flow.connect.data.entity;

import javax.persistence.Entity;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import javax.annotation.Nullable;

@Entity
public class Person extends AbstractEntity {

  private String firstName;
  private String lastName;
  @Email
  private String email;
  private String phone;
  @Nullable
  private LocalDate dateOfBirth;
  private String occupation;

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public String getOccupation() {
    return occupation;
  }

  public void setOccupation(String occupation) {
    this.occupation = occupation;
  }

}
