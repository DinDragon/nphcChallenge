package com.nphc.service.Employee.Repository;

import com.nphc.service.Employee.TO.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository
        extends JpaRepository<Employee, String>, CustomEmployeeRepository {

    @Query("Select e from Employee e where e.login = ?1")
    Optional<Employee> findEmployeeByLogin(String login);
    @Query("Select e from Employee e where e.login != ?1 and e.id = ?2")
    Optional<Employee> findEmployeeByLoginWithDifferentId(String login,String Id);

}
