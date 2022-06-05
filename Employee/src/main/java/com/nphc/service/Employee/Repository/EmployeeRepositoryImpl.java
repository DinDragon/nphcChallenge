package com.nphc.service.Employee.Repository;

import com.nphc.service.Employee.TO.Employee;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
class EmployeeRepositoryImpl implements CustomEmployeeRepository{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Employee> retrieveListOfEmployeeFilter(float minSalary, float maxSalary, int offset, int limit, String sortBy, String order, String name) {
        String queryString = "Select e from Employee e where e.salary >= :minSalary and e.salary < :maxSalary ";
        if(name!=null){
            queryString = queryString +  "and e.name like '%" + name +  "%' ";
        }

        queryString = queryString + "ORDER BY e." + sortBy + " " + order;
        return entityManager.
                createQuery(queryString, Employee.class).
                setParameter("minSalary", minSalary).
                setParameter("maxSalary", maxSalary).
                setMaxResults(limit).
                setFirstResult(offset).
                getResultList();
    }
}
