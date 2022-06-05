package com.nphc.service.Employee.Service;

import com.nphc.service.Employee.Constant;
import com.nphc.service.Employee.Repository.EmployeeRepository;
import com.nphc.service.Employee.TO.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }
    public List<Employee> getEmployeeListByFilter(float minSalary, float maxSalary, int offset, int limit, String sortBy, String order, String name){

        return employeeRepository.retrieveListOfEmployeeFilter(minSalary, maxSalary, offset, limit, sortBy, order, name);
//        return List.of(
//                new Employee(
//                        "emp0001",
//                        "Harry Potter",
//                        "hpotter",
//                        1234.00f,
//                        LocalDate.of(2001,11,16)
//                )
//        );
    }

    public void createEmployee(Employee employee){
        Optional<Employee>  tempEmployee = employeeRepository.findEmployeeByLogin(employee.getLogin());
        if(tempEmployee.isPresent()){
            throw new IllegalStateException("Login already exists");
        }else {
            employeeRepository.save(employee);
        }
        System.out.println(employee);
    }

    public void deleteEmployee(String id){
        boolean exists = employeeRepository.existsById(id);
        if(!exists){
            throw new IllegalStateException("Employee with id " + id + " does not exists");
        }else{
            employeeRepository.deleteById(id);
        }
    }

    @Transactional
    public void updateEmployee(Employee newEmployee) {
        Employee employee = employeeRepository.findById(newEmployee.getId()).orElseThrow(
                () -> new IllegalStateException(
                        "Employee wth id " + newEmployee.getId() + " does not exists."
                )
        );
        if(validateEmployee(newEmployee)){
            employee.setLogin(newEmployee.getLogin());
            employee.setName(newEmployee.getName());
            employee.setSalary(newEmployee.getSalary());
            employee.setStartDate(newEmployee.getStartDate());
        }
    }

    public String createOrUpdateEmployeeIfExist(Employee newEmployee){

        if(validateEmployee(newEmployee) && !checkDuplicateLogin(newEmployee)){
            employeeRepository.save(newEmployee);
            return Constant.EMPLOYEE_SERVICE_MESSAGE.SUCCESS;
        }else{
            return Constant.EMPLOYEE_SERVICE_MESSAGE.FAILED;
        }

    }

    //validateEmployee
    public boolean validateEmployee(Employee employee){
        if(employee.getId()!= null && employee.getId().length()>0 &&
                employee.getName() != null && employee.getName().length() > 0 &&
                employee.getLogin() != null && employee.getLogin().length() > 0 &&
                employee.getSalary() > 0 &&
                employee.getStartDate() != null){
            return true;
        }else{
            return false;
        }
    }

    //True if duplicate exists. False if no duplicate.
    public boolean checkDuplicateLogin(Employee employee){
        return employeeRepository.findEmployeeByLoginWithDifferentId(employee.getLogin(), employee.getId()).isPresent();
    }
}
