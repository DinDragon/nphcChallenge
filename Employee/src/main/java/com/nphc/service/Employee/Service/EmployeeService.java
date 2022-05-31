package com.nphc.service.Employee.Service;

import com.nphc.service.Employee.TO.EmployeeTO;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmployeeService {
    public List<EmployeeTO> getEmployee(){
        return List.of(
                new EmployeeTO(
                        "emp0001",
                        "Harry Potter",
                        "hpotter",
                        1234.00f,
                        LocalDate.of(2001,11,16)
                )
        );
    }
}
