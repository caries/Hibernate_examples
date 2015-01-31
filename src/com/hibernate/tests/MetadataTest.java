package com.hibernate.tests;

import com.hibernate.metadata.Unit;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;

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