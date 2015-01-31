package com.hibernate.tests;

import com.hibernate.metadata.Unit;
import com.hibernate.metadata.office.Company;
import com.hibernate.metadata.office.Employee;
import com.hibernate.metadata.office.Government;
import com.hibernate.metadata.office.Person;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * Unit test for metadata classes.
 */
public class MetadataTest extends BaseTest {
    @Test
    public void testUnitCRUDOperations() throws Exception {
        final Unit unit = new Unit();
        unit.setName("Yoho");
        unit.setComments(new HashSet<String>());
        unit.getComments().add("Hello world");
        unit.getComments().add("Where is my beer?");

        runTransactionSession(new TransactionSession() {
            @Override
            public void run(Session session) {
                session.persist(unit);
            }
        });

        runTransactionSession(new TransactionSession() {
            @Override
            public void run(Session session) {
                List units = session.createQuery("from Unit").list();
                assertEquals(units.size(), 1);
            }
        });

        runTransactionSession(new TransactionSession() {
            @Override
            public void run(Session session) {
                Unit persistedUnit = (Unit) session.createQuery("from Unit u where u.id = :id").setParameter("id", unit.getId()).uniqueResult();
                assertEquals(persistedUnit, unit);
                assertNotSame(persistedUnit, unit);
            }
        });

        runTransactionSession(new TransactionSession() {
            @Override
            public void run(Session session) {
                Unit persistedUnit = (Unit) session.createQuery("from Unit u where u.id = :id").setParameter("id", unit.getId()).uniqueResult();
                persistedUnit.setName("Paul");
                assertEquals(persistedUnit.getName(), "Paul");
            }
        });

        runTransactionSession(new TransactionSession() {
            @Override
            public void run(Session session) {
                Unit persistedUnit = (Unit) session.createQuery("from Unit u where u.id = :id").setParameter("id", unit.getId()).uniqueResult();
                assertEquals(persistedUnit.getName(), "Paul");
            }
        });

        runTransactionSession(new TransactionSession() {
            @Override
            public void run(Session session) {
                session.delete(unit);
            }
        });

        runTransactionSession(new TransactionSession() {
            @Override
            public void run(Session session) {
                List units = session.createQuery("from Unit").list();
                assertEquals(units.size(), 0);
            }
        });
    }

    @Test
    public void testOfficeStructure() throws Exception {
        final Government government = new Government();
        Person chief = new Person();
        chief.setName("Peter");
        government.setChief(chief);

        final Company company = new Company();
        company.setGovernment(government);
        company.setName("Icecream&co");
        company.setEmployees(new HashSet<Employee>());

        addNewEmployees(company, "Bob", "Kate", "Luice", "Rob", "Obama", "Robin");
        setSecretSantaVictim(company.getEmployees());

        runTransactionSession(new TransactionSession() {
            @Override
            public void run(Session session) {
                session.persist(company);
            }
        });

        runTransactionSession(new TransactionSession() {
            @Override
            public void run(Session session) {
                Government persistedGovernment = (Government) session.createQuery("from Government").uniqueResult();
                assertEquals(persistedGovernment, government);
            }
        });

//        Company persistedCompany = (Company) entityManager.createQuery("Select c from Company c").getSingleResult();
//        assertEquals(persistedCompany, company);
//
//        Employee obama = findEmployee(company.getEmployees(), "Obama");
//        Employee persistedObama = (Employee) entityManager.createQuery("Select emp from Employee emp where emp.name = 'Obama'").getSingleResult();
//        assertEquals(persistedObama, obama);
    }

    private static void addNewEmployees(Company company, String... names) {
        for (String name : names) {
            addNewEmployee(company, name);
        }
    }

    private static void addNewEmployee(Company company, String name) {
        Employee employee = new Employee();
        employee.setName(name);
        employee.setCompany(company);
        company.getEmployees().add(employee);
    }

    private static void setSecretSantaVictim(Set<Employee> employees) {
        List<Employee> praisedEmployees = new ArrayList<Employee>(employees);
        Random random = new Random();
        for (Employee employee : employees) {
            Employee praisedEmployee;
            do {
                praisedEmployee = praisedEmployees.get(random.nextInt(praisedEmployees.size()));
            } while (employee.getName().equals(praisedEmployee.getName()));

            employee.setSecretSantaVictim(praisedEmployee);
            praisedEmployees.remove(praisedEmployee);
        }
    }

    private static Employee findEmployee(List<Employee> employees, String name) {
        for (Employee employee : employees) {
            if (employee.getName().equals(name)) {
                return employee;
            }
        }

        return null;
    }

    private void runTransactionSession(TransactionSession transactionSession) throws Exception {
        Session session = getSession();
        Transaction transaction = session.getTransaction();
        transaction.begin();
        try {
            transactionSession.run(session);
            transaction.commit();
        } catch(Exception e) {
            try {
                throw e;
            } finally {
                transaction.rollback();
            }
        } finally {
            session.close();
        }
    }

    private interface TransactionSession {
        void run(Session session);
    }
}