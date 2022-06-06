package com.nphc.service.Employee.Util;

import com.nphc.service.Employee.TO.Employee;

import java.util.List;

public class EmployeeListResultBuilder {
    public List<Employee> results;

    public List<Employee> getResults() {
        return results;
    }

    public void setResults(List<Employee> results) {
        this.results = results;
    }

    public EmployeeListResultBuilder(List<Employee> results) {
        this.results = results;
    }
}
