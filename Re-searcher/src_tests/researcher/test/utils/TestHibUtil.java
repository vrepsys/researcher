package researcher.test.utils;

import org.hibernate.*;
import org.hibernate.cfg.*;

public class TestHibUtil {

    private static final SessionFactory sessionFactory;

    private static final boolean USE_TEST_DATABASE = true;

    static {
        try {
            Configuration config = new AnnotationConfiguration().configure();
            if (USE_TEST_DATABASE) {
                // we load the same configuration as we use in application
                // but change the database - testing framework uses a
                // separate database
                config.setProperty("hibernate.connection.url", "jdbc:derby:hibtest;create=true");
                // we also drop and recreate testing database every time we run
                // tests
                config.setProperty("hibernate.hbm2ddl.auto", "create");
            }
            sessionFactory = config.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static Session getSession() throws HibernateException {
        return sessionFactory.openSession();
    }

}