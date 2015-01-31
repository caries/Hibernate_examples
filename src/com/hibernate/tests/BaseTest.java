package com.hibernate.tests;

import com.hsql_utils.HSQLServer;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class BaseTest {
    private static SessionFactory sessionFactory;

    @BeforeClass
    public static void setUp() throws Exception {
        HSQLServer.start();

        Configuration configuration = new Configuration();
        configuration.configure();
        sessionFactory = configuration.buildSessionFactory(
                new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry());
    }

    @AfterClass
    public static void tearDown() throws Exception {
        HSQLServer.stop();
    }

    protected Session getSession() throws HibernateException {
        return sessionFactory.openSession();
    }
}