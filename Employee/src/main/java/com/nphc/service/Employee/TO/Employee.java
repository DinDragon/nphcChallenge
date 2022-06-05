package com.nphc.service.Employee.TO;




import com.nphc.service.Employee.Util.CustomLocalDateConverter;
import com.opencsv.bean.*;
import com.sun.istack.NotNull;

import javax.persistence.*;
import java.text.ParseException;
import java.time.LocalDate;

@Entity
@Table
public class Employee {
    @Id
    @CsvBindByName(column = "id")
    private String id;
    @CsvBindByName(column = "login")
    private String login;
    @CsvBindByName(column = "name")
    private String name;
    @CsvBindByName(column = "salary")
    private float salary;
    @CsvCustomBindByName(converter = CustomLocalDateConverter.class, column = "startDate")
    private LocalDate startDate;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }



    public Employee() {
    }


    public Employee(String id, String login, String name, float salary, LocalDate startDate) {
        this.id = id;
        this.login = login;
        this.name = name;
        this.salary = salary;
        this.startDate = startDate;
    }


}
