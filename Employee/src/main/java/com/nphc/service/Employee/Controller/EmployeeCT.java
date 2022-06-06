package com.nphc.service.Employee.Controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.nphc.service.Employee.Constant;
import com.nphc.service.Employee.Service.EmployeeService;
import com.nphc.service.Employee.TO.Employee;
import com.nphc.service.Employee.Util.EmployeeListResultBuilder;
import com.nphc.service.Employee.Util.EmployeeUtil;
import com.nphc.service.Employee.Util.MessageBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

@RestController
@RequestMapping(path = "/users/")
public class EmployeeCT {
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeCT(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    public EmployeeUtil employeeUtil = new EmployeeUtil();
    @GetMapping
    public ResponseEntity getEmployeeList(
            @RequestParam(value = "minSalary", required = false) String minSalaryStr,
            @RequestParam(value = "maxSalary", required = false) String maxSalaryStr,
            @RequestParam(value = "offset", required = false) String offsetStr,
            @RequestParam(value = "limit", required = false) String limitStr,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "sortBy", required = false) String sortByStr,
            @RequestParam(value = "order", required = false) String orderStr){
        float minSalary = 0;
        float maxSalary = 4000;
        int offset = 0;
        int limit = 0;
        String sortBy = "id";
        String order = "asc";

        List<String> sortByList = Arrays.asList("id", "name", "login", "salary", "startDate");
        List<String> orderList = Arrays.asList("asc", "desc");
        System.out.println("inputs: " + minSalaryStr + ", " + maxSalaryStr + ", " + ", " + offsetStr + ", " + limitStr + ", " + sortByStr + ", " + name);

        if(sortByStr != null){
            if(sortByList.contains(sortByStr)){
                sortBy = sortByStr;
            }else{
                return new ResponseEntity(new MessageBuilder("Please enter a valid sort by type. Valid sort by's are id, name, login, salary, startDate"), HttpStatus.BAD_REQUEST);
            }
        }
        if(orderStr != null){
            if(orderList.contains(orderStr)){
                order = orderStr;
            }else{
                return new ResponseEntity(new MessageBuilder("Please enter a valid order by type. Valid orders are asc, desc"), HttpStatus.BAD_REQUEST);
            }
        }


        if(minSalaryStr != null){
            try {
                minSalary = Float.parseFloat(minSalaryStr);
            }catch(Exception e){
                return new ResponseEntity(new MessageBuilder("Failed. Unable to parse minSalary variable."), HttpStatus.BAD_REQUEST);
            }
        }
        if(minSalary < 0 ){
            return new ResponseEntity(new MessageBuilder("Failed. min salary is smaller than 0."), HttpStatus.BAD_REQUEST);
        }
        if(maxSalaryStr != null){
            try {
                maxSalary = Float.parseFloat(maxSalaryStr);
            }catch(Exception e){
                return new ResponseEntity(new MessageBuilder("Unable to parse maxSalary variable."), HttpStatus.BAD_REQUEST);
            }
        }
        if(minSalary >= maxSalary){
            return new ResponseEntity(new MessageBuilder("minSalary is bigger than maxSalary."), HttpStatus.BAD_REQUEST);
        }
        if(offsetStr != null){
            try{
                offset = Integer.parseInt(offsetStr);
            }catch(Exception e){
                return new ResponseEntity(new MessageBuilder("Unable to parse offset variable."), HttpStatus.BAD_REQUEST);
            }
        }
        if(offset < 0){
            return new ResponseEntity(new MessageBuilder("Failed. offset is smaller than 0."), HttpStatus.BAD_REQUEST);
        }
        if(limitStr != null){
            try{
                limit = Integer.parseInt(limitStr);
            }catch(Exception e){
                return new ResponseEntity(new MessageBuilder("Unable to parse limit variable."), HttpStatus.BAD_REQUEST);
            }
        }
        if(limit < 0){
            return new ResponseEntity(new MessageBuilder("Failed. limit is smaller than 0."), HttpStatus.BAD_REQUEST);
        }

        System.out.println("inputs parsed: " + minSalary + ", " + maxSalary + ", " + ", " + offset + ", " + limit + ", " + sortBy);

        return new ResponseEntity(new EmployeeListResultBuilder(employeeService.getEmployeeListByFilter(minSalary, maxSalary, offset, limit, sortBy, order, name)),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity createEmployee(@RequestBody Employee employee){
        String message = employeeService.createEmployee(employee);
        if(message.equals(Constant.EMPLOYEE_SERVICE_MESSAGE.CREATE_SUCCESS)){
            return new ResponseEntity(new MessageBuilder(message),HttpStatus.resolve(201));
        }else{
            return new ResponseEntity(new MessageBuilder(message),HttpStatus.resolve(400));
        }

    }
    @GetMapping(path = "{id}")
    public ResponseEntity getEmployeeById(@PathVariable("id") String id){
        Employee employee = new Employee();
        if(id != null){
            try {
                employee = employeeService.getEmployeeById(id);
                return new ResponseEntity(employee, HttpStatus.OK);
            }catch(NoSuchElementException e){
                return new ResponseEntity(new MessageBuilder(Constant.EMPLOYEE_SERVICE_MESSAGE.NO_SUCH_EMPLOYEE), HttpStatus.resolve(400));
            }catch(Exception e){
                return new ResponseEntity(new MessageBuilder("Bad input."), HttpStatus.resolve(400));
            }
        }else{
            System.out.println("Employee id passed is null or blank.");
            return new ResponseEntity(new MessageBuilder(Constant.EMPLOYEE_SERVICE_MESSAGE.NO_SUCH_EMPLOYEE), HttpStatus.resolve(400));
        }

    }
    @PutMapping(path = "{id}")
    public ResponseEntity updateEmployee(@PathVariable("id") String id,
                                         @RequestParam(value = "login",required = false) String login,
                                         @RequestParam(value = "name",required = false) String name,
                                         @RequestParam(value = "salary",required = false) String salaryStr,
                                         @RequestParam(value = "startDate",required = false) String startDateStr){
        float salary = 0;
        LocalDate startDate = null;
        if(salaryStr != null){
            try {
                salary = Float.parseFloat(salaryStr);
            }catch(NumberFormatException e){
                System.out.println(e.getMessage());
                return new ResponseEntity(new MessageBuilder(Constant.EMPLOYEE_SERVICE_MESSAGE.INVALID_SALARY),HttpStatus.resolve(400));
            }
        }
        if(startDateStr != null){
            try{
                startDate = employeeUtil.convertInputDateString(startDateStr);
            }catch (DateTimeParseException e){
                System.out.println(e.getMessage());
                return new ResponseEntity(new MessageBuilder(Constant.EMPLOYEE_SERVICE_MESSAGE.INVALID_DATE),HttpStatus.resolve(400));
            }
        }
        String message = employeeService.updateEmployee(id, login, name, salary, startDate);
        if(message.equals(Constant.EMPLOYEE_SERVICE_MESSAGE.UPDATE_SUCCESS)) {
            return new ResponseEntity(new MessageBuilder(message), HttpStatus.resolve(200));
        }else{
            return new ResponseEntity(new MessageBuilder(message), HttpStatus.resolve(400));
        }
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity deleteEmployee(@PathVariable("id") String id){
        String message = employeeService.deleteEmployee(id);
        if(message.equals(Constant.EMPLOYEE_SERVICE_MESSAGE.DELETE_SUCCESS)){
            return new ResponseEntity(new MessageBuilder(message), HttpStatus.resolve(200));
        }else{
            return new ResponseEntity(new MessageBuilder(message), HttpStatus.resolve(400));
        }
    }

    @PostMapping("/upload")
    public ResponseEntity uploadCSVFile(@RequestParam("file") MultipartFile file) {
        // validate file
        boolean updateOrCreateReq = false;
        List<Employee> employees = new ArrayList<Employee>();
        if (file.isEmpty() || !file.getOriginalFilename().contains(".csv")) {
            return new ResponseEntity(new MessageBuilder("Invalid file type. File must be of type .csv"), HttpStatus.PRECONDITION_FAILED);
        } else {

            // parse CSV file to create a list of `User` objects
            try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

                // create csv bean reader
                CsvToBean<Employee> csvToBean = new CsvToBeanBuilder(reader)
                        .withType(Employee.class)
                        .withIgnoreLeadingWhiteSpace(true)
                        .build();

                // convert `CsvToBean` object to list of users
                employees = csvToBean.parse();
                //Validate entire list first
                for(Employee employee: employees){
                    if(employeeService.checkDuplicateLogin(employee.getLogin(), employee.getId())){
                        return new ResponseEntity(new MessageBuilder(Constant.EMPLOYEE_SERVICE_MESSAGE.DUPLICATE_LOGIN + "employee id : " + employee.getId() + " login id: " + employee.getLogin()),HttpStatus.PRECONDITION_FAILED);
                    }
                    if(!employeeService.validateEmployeeId(employee.getId())){
                        return new ResponseEntity(new MessageBuilder(Constant.EMPLOYEE_SERVICE_MESSAGE.INVALID_EMPLOYEE_ID + " employee : " + employee.getId() + " login id: " + employee.getLogin()),HttpStatus.PRECONDITION_FAILED);
                    }
                    if(!employeeService.validateEmployeeLogin(employee.getLogin())){
                        return new ResponseEntity(new MessageBuilder(Constant.EMPLOYEE_SERVICE_MESSAGE.INVALID_LOGIN + " employee : " + employee.getId() + " login id: " + employee.getLogin()),HttpStatus.PRECONDITION_FAILED);
                    }
                    if(!employeeService.validateEmployeeName(employee.getName())){
                        return new ResponseEntity(new MessageBuilder(Constant.EMPLOYEE_SERVICE_MESSAGE.INVALID_NAME + " employee : " + employee.getId() + " login id: " + employee.getLogin()),HttpStatus.PRECONDITION_FAILED);
                    }
                    if(!employeeService.validateEmployeeSalary(employee.getSalary())){
                        return new ResponseEntity(new MessageBuilder(Constant.EMPLOYEE_SERVICE_MESSAGE.INVALID_SALARY + " employee : " + employee.getId() + " login id: " + employee.getLogin()),HttpStatus.PRECONDITION_FAILED);
                    }
                    if(!employeeService.validateEmployeeStartDate(employee.getStartDate())){
                        return new ResponseEntity(new MessageBuilder(Constant.EMPLOYEE_SERVICE_MESSAGE.INVALID_DATE + " employee : " + employee.getId() + " login id: " + employee.getLogin()),HttpStatus.PRECONDITION_FAILED);
                    }
                    if(employeeService.checkCreateOrUpdateRequired(employee)){
                        updateOrCreateReq = true;
                    }
                }
                for(Employee employee: employees){
                    String checkId = employee.getId();
                    int count = 0;
                    for(Employee employee1: employees){
                        if(checkId.equals(employee1.getId())){
                            count++;
                        }
                        if(count>=2){
                            return new ResponseEntity(new MessageBuilder("Duplicate employee id found in the same csv file : " + employee.getId()) ,HttpStatus.PRECONDITION_FAILED);
                        }
                    }
                }



            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
                //return new ResponseEntity(new MessageBuilder("Unable to parse CSV data. Please check data format is correct." ), HttpStatus.EXPECTATION_FAILED);

            }
        }
        if(updateOrCreateReq){
            for(Employee employee: employees){
//                System.out.println(employee.getName());
//                System.out.println(employee.getStartDate());
                String message = employeeService.createOrUpdateEmployeeIfExist(employee);
            }
            return new ResponseEntity(new MessageBuilder(Constant.EMPLOYEE_SERVICE_MESSAGE.UPDATE_SUCCESS), HttpStatus.resolve(201));
        }else{
            return new ResponseEntity(new MessageBuilder("Nothing to update or create."), HttpStatus.resolve(200));
        }


    }



    @ExceptionHandler({ DateTimeParseException.class, InvalidFormatException.class, NumberFormatException.class })
    public ResponseEntity handleDateException(Exception e) {
        System.out.println(e.getMessage());
        if(e.getMessage().contains("startDate")) {
            return new ResponseEntity(new MessageBuilder(Constant.EMPLOYEE_SERVICE_MESSAGE.INVALID_DATE), HttpStatus.resolve(400));
        }else if(e.getMessage().contains("salary")){
            return new ResponseEntity(new MessageBuilder(Constant.EMPLOYEE_SERVICE_MESSAGE.INVALID_SALARY),HttpStatus.resolve(400));
        }else{
            return new ResponseEntity(new MessageBuilder(Constant.EMPLOYEE_SERVICE_MESSAGE.FAILED),HttpStatus.resolve(500));
        }
    }

//    @ExceptionHandler({ InvalidFormatException.class })
//    public ResponseEntity handleFloatException() {
//
//    }



}
