package com.nphc.service.Employee.Controller;

import com.nphc.service.Employee.Service.EmployeeService;
import com.nphc.service.Employee.TO.Employee;
import com.nphc.service.Employee.Util.EmployeeResultBuilder;
import com.nphc.service.Employee.Util.MessageBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

        System.out.println("inputs: " + minSalary + ", " + maxSalary + ", " + ", " + offset + ", " + limit + ", " + sortBy);

        return new ResponseEntity(new EmployeeResultBuilder(employeeService.getEmployeeListByFilter(minSalary, maxSalary, offset, limit, sortBy, order, name)),HttpStatus.OK);
    }

    @PostMapping
    public void createEmployee(@RequestBody Employee employee){
        employeeService.createEmployee(employee);
    }

    @PostMapping("/upload")
    public ResponseEntity uploadCSVFile(@RequestParam("file") MultipartFile file) {
        // validate file
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
                List<Employee> employees = csvToBean.parse();
                //Validate entire list first
                for(Employee employee: employees){
                    if(!employeeService.validateEmployee(employee)){
                        return new ResponseEntity(new MessageBuilder("Data validation failed on employee: " + employee.getId()),HttpStatus.PRECONDITION_FAILED);
                    }
                    if(employeeService.checkDuplicateLogin(employee)){
                        return new ResponseEntity(new MessageBuilder("Duplicate employee login found for employee : " + employee.getId()) + " login id: " + employee.getLogin(),HttpStatus.PRECONDITION_FAILED);
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

                for(Employee employee: employees){
                    System.out.println(employee.getName());
                    System.out.println(employee.getStartDate());
                    String message = employeeService.createOrUpdateEmployeeIfExist(employee);
                }

            }catch (Exception ex) {
                return new ResponseEntity(new MessageBuilder("Unable to parse CSV data. Please check data format is correct."), HttpStatus.EXPECTATION_FAILED);

            }
        }
        return new ResponseEntity(new MessageBuilder("Successfully updated employee list."), HttpStatus.OK);

    }

    @DeleteMapping(path = "{id}")
    public void deleteEmployee(@PathVariable("id") String id){
        employeeService.deleteEmployee(id);
    }

//    @PutMapping(path = "{id}")
//    public void updateEmployee(@PathVariable String id, @RequestParam(required = false) String name, @RequestParam(required = false) float salary){
//        employeeService.updateEmployee(id, name, salary);
//    }

}
