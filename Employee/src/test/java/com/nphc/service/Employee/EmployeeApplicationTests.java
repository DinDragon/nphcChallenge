package com.nphc.service.Employee;

import com.nphc.service.Employee.Controller.EmployeeCT;
import com.nphc.service.Employee.Repository.EmployeeRepository;
import com.nphc.service.Employee.Service.EmployeeService;
import com.nphc.service.Employee.TO.Employee;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class EmployeeApplicationTest {


	@Autowired
	private EmployeeService service;

	@Test
	public void testCreateUpdateDeleteEmployee() {
		Employee employee = new Employee(
                        "t0001",
                        "employee1",
                        "name1",
                        1234.00f,
                        LocalDate.of(2001,11,16)
            );


		String message = service.createEmployee(employee);


		Employee employee2 = new Employee(
				"t0002",
				"employee2",
				"",
				123,
				LocalDate.of(2001,11,16)
		);
		String message2 = service.createEmployee(employee2);


		Employee employee3 = new Employee(
				"t0003",
				"employee3",
				"name2",
				123,
				null
		);
		String message3 = service.createEmployee(employee3);


		Employee employee4 = new Employee(
				"t0004",
				"employee1",
				"name4",
				123,
				LocalDate.of(2001,11,16)
		);
		String message4 = service.createEmployee(employee4);


		String message5 = service.updateEmployee("t0001", "employee1New", "name1", 0, null);


		Employee employee5 = service.getEmployeeById("t0001");


		String message6 = service.deleteEmployee("t0001");

		Assert.assertTrue(message.equals(Constant.EMPLOYEE_SERVICE_MESSAGE.CREATE_SUCCESS));
		Assert.assertTrue(message2.equals(Constant.EMPLOYEE_SERVICE_MESSAGE.INVALID_NAME));
		Assert.assertTrue(message3.equals(Constant.EMPLOYEE_SERVICE_MESSAGE.INVALID_DATE));
		Assert.assertTrue(message4.equals(Constant.EMPLOYEE_SERVICE_MESSAGE.DUPLICATE_LOGIN));
		Assert.assertTrue(message5.equals(Constant.EMPLOYEE_SERVICE_MESSAGE.UPDATE_SUCCESS));
		Assert.assertTrue(employee5.getLogin().equals("employee1New"));
		Assert.assertTrue(message6.equals(Constant.EMPLOYEE_SERVICE_MESSAGE.DELETE_SUCCESS));
	}


}
