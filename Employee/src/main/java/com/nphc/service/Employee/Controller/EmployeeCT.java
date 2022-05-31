package com.nphc.service.Employee.Controller;

import com.nphc.service.Employee.Service.EmployeeService;
import com.nphc.service.Employee.TO.EmployeeTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/users/")
public class EmployeeCT {
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeCT(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeTO> getEmployee(){
        return employeeService.getEmployee();
    }
}
