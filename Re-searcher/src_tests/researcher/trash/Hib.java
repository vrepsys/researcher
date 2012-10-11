package researcher.trash;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

public class Hib {

    static {
        System.setProperty("derby.system.home", "/home/posu/projects/Starter/dist/researcher/db");
    }
    
    private static SessionFactory sessionFactory;
    
    static {
        try {
            Configuration config = new AnnotationConfiguration().configure();
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
