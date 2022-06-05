package com.nphc.service.Employee.Util;

import com.nphc.service.Employee.TO.Employee;

import java.util.List;

public class EmployeeResultBuilder {
    public List<Employee> results;

    public List<Employee> getResults() {
        return results;
    }

    public void setResults(List<Employee> results) {
        this.results = results;
    }

    public EmployeeResultBuilder(List<Employee> results) {
        this.results = results;
    }
}
