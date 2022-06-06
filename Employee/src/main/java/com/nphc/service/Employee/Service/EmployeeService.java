package com.nphc.service.Employee.Service;

import com.nphc.service.Employee.Constant;
import com.nphc.service.Employee.Repository.EmployeeRepository;
import com.nphc.service.Employee.TO.Employee;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
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

    public String createEmployee(Employee employee){

        if(checkDuplicateLogin(employee.getLogin(), employee.getId())){
            return Constant.EMPLOYEE_SERVICE_MESSAGE.DUPLICATE_LOGIN;
        }else if (!validateEmployeeSalary(employee.getSalary())){
            return Constant.EMPLOYEE_SERVICE_MESSAGE.INVALID_SALARY;
        }else if(checkDuplicateEmployeeID(employee.getId())){
            return Constant.EMPLOYEE_SERVICE_MESSAGE.DUPLICATE_ID;
        }else if(!validateEmployeeStartDate(employee.getStartDate())){
            return Constant.EMPLOYEE_SERVICE_MESSAGE.INVALID_DATE;
        }else if(!validateEmployeeName(employee.getName())){
            return Constant.EMPLOYEE_SERVICE_MESSAGE.INVALID_NAME;
        }else if(!validateEmployeeId(employee.getId())){
            return Constant.EMPLOYEE_SERVICE_MESSAGE.INVALID_EMPLOYEE_ID;
        }else{
            employeeRepository.save(employee);
            return Constant.EMPLOYEE_SERVICE_MESSAGE.CREATE_SUCCESS;
        }
    }



    public String deleteEmployee(String id){
        boolean exists = employeeRepository.existsById(id);
        if(!exists){
            return Constant.EMPLOYEE_SERVICE_MESSAGE.NO_SUCH_EMPLOYEE;
        }else{
            employeeRepository.deleteById(id);
            return Constant.EMPLOYEE_SERVICE_MESSAGE.DELETE_SUCCESS;
        }
    }

    @Transactional
    public String updateEmployee(Employee newEmployee) {
        Employee employee = employeeRepository.findById(newEmployee.getId()).orElse(null);
        if(employee == null){
            return Constant.EMPLOYEE_SERVICE_MESSAGE.NO_SUCH_EMPLOYEE;
        }
        if(checkDuplicateLogin(newEmployee.getLogin(), newEmployee.getId())){
            return Constant.EMPLOYEE_SERVICE_MESSAGE.DUPLICATE_LOGIN;
        }
        if(!validateEmployeeId(newEmployee.getId())){
            return Constant.EMPLOYEE_SERVICE_MESSAGE.INVALID_EMPLOYEE_ID;
        }
        if(!validateEmployeeLogin(newEmployee.getLogin())){
            return Constant.EMPLOYEE_SERVICE_MESSAGE.INVALID_LOGIN;
        }
        if(!validateEmployeeName(newEmployee.getName())){
            return Constant.EMPLOYEE_SERVICE_MESSAGE.INVALID_NAME;
        }
        if(!validateEmployeeSalary(newEmployee.getSalary())){
            return Constant.EMPLOYEE_SERVICE_MESSAGE.INVALID_SALARY;
        }
        if(!validateEmployeeStartDate(newEmployee.getStartDate())){
            return Constant.EMPLOYEE_SERVICE_MESSAGE.INVALID_DATE;
        }

        employee.setLogin(newEmployee.getLogin());
        employee.setName(newEmployee.getName());
        employee.setSalary(newEmployee.getSalary());
        employee.setStartDate(newEmployee.getStartDate());

        return Constant.EMPLOYEE_SERVICE_MESSAGE.UPDATE_SUCCESS;
    }

    @Transactional
    public String updateEmployee(String id, String login, String name, float salary, LocalDate startDate) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        if(employee == null){
            return Constant.EMPLOYEE_SERVICE_MESSAGE.NO_SUCH_EMPLOYEE;
        }
        if(checkDuplicateLogin(login, id)){
            return Constant.EMPLOYEE_SERVICE_MESSAGE.DUPLICATE_LOGIN;
        }
        if(name != null && !validateEmployeeName(name)){
            return Constant.EMPLOYEE_SERVICE_MESSAGE.INVALID_NAME;
        }
        if(salary != 0 && !validateEmployeeSalary(salary)){
            return Constant.EMPLOYEE_SERVICE_MESSAGE.INVALID_SALARY;
        }
        if(startDate != null && !validateEmployeeStartDate(startDate)){
            return Constant.EMPLOYEE_SERVICE_MESSAGE.INVALID_DATE;
        }

        if(login!=null)employee.setLogin(login);
        if(name!=null)employee.setName(name);
        if(salary!=0)employee.setSalary(salary);
        if(startDate!=null)employee.setStartDate(startDate);

        return Constant.EMPLOYEE_SERVICE_MESSAGE.UPDATE_SUCCESS;
    }

    public String createOrUpdateEmployeeIfExist(Employee newEmployee){

        if(!checkDuplicateLogin(newEmployee.getLogin(), newEmployee.getId())){
            employeeRepository.save(newEmployee);
            return Constant.EMPLOYEE_SERVICE_MESSAGE.SUCCESS;
        }else{
            return Constant.EMPLOYEE_SERVICE_MESSAGE.FAILED;
        }

    }

    @Deprecated
    public boolean validateEmployee(Employee employee){
        if(employee.getId()!= null && employee.getId().length()>0 &&
                employee.getName() != null && employee.getName().length() > 0 &&
                employee.getLogin() != null && employee.getLogin().length() > 0 &&
                validateEmployeeSalary(employee.getSalary()) &&
                employee.getStartDate() != null){
            return true;
        }else{
            return false;
        }
    }

    public boolean validateEmployeeId(String id){
        if(id != null && id.length()>0){
            return true;
        }else{
            return false;
        }
    }

    public boolean validateEmployeeName(String name){
        if(name != null && name.length()>0){
            return true;
        }else{
            return false;
        }
    }

    public boolean validateEmployeeLogin(String login){
        if(login != null && login.length()>0){
            return true;
        }else{
            return false;
        }
    }

    public boolean validateEmployeeSalary(float salary){
        if(salary > 0){
            return true;
        }else{
            return false;
        }
    }

    public boolean validateEmployeeStartDate(LocalDate startDate){
        if(startDate != null){
            return true;
        }else{
            return false;
        }
    }

    //True if duplicate exists. False if no duplicate.
    public boolean checkDuplicateLogin(String login, String id){
        return employeeRepository.findEmployeeByLoginWithDifferentId(login, id).isPresent();
    }

    private boolean checkDuplicateEmployeeID(String id) {
        return employeeRepository.findById(id).isPresent();
    }

    public Employee getEmployeeById(String id) {
        return employeeRepository.findById(id).get();
    }


    public boolean checkCreateOrUpdateRequired(Employee employee) {
        Employee employee1 = employeeRepository.findById(employee.getId()).orElse(null);

        if(employee1 != null) {
            if (!employee1.getName().equals(employee.getName())) {
                return true;
            }
            if (!employee1.getLogin().equals(employee.getLogin())) {
                return true;
            }
            if (!(employee.getSalary() == employee1.getSalary())) {
                return true;
            }
            if (!employee.getStartDate().isEqual(employee1.getStartDate())) {
                return true;
            }
            return false;
        }else{
            return true;
        }

    }
}
