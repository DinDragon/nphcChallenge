package com.nphc.service.Employee.Config;

import com.nphc.service.Employee.Repository.EmployeeRepository;
import com.nphc.service.Employee.TO.Employee;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class EmployeeConfig {
    @Bean
    CommandLineRunner commandLineRunner(EmployeeRepository employeeRepository){
        return args -> {
//            Employee employee = new Employee(
//                        "emp0001",
//                        "Harry Potter",
//                        "hpotter",
//                        1234.00f,
//                        LocalDate.of(2001,11,16)
//            );
//            Employee employee2 = new Employee(
//                    "emp0002",
//                    "jack123",
//                    "jack",
//                    321.00f,
//                    LocalDate.of(2021,11,16)
//            );
//            employeeRepository.saveAll(List.of(employee, employee2));
        };
    };
}
