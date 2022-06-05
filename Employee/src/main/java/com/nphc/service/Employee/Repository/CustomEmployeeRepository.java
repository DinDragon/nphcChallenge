package com.nphc.service.Employee.Repository;

import com.nphc.service.Employee.TO.Employee;

import java.util.List;

interface CustomEmployeeRepository {
    List<Employee> retrieveListOfEmployeeFilter(float minSalary, float maxSalary, int offset, int limit, String sortBy, String order, String name);
}
