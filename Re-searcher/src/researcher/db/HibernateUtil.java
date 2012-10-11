package researcher.db;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import researcher.utils.Registry;


import com.javaforge.honeycomb.hivemind.hibernate.ISessionFactoryProvider;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    static {
        ISessionFactoryProvider provider = (ISessionFactoryProvider) Registry.getHivemindRegistry()
                .getService("honey.SessionFactoryProvider", ISessionFactoryProvider.class);
        sessionFactory = provider.getSessionFactory();
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static Session openSession() throws HibernateException {
        try {
            return sessionFactory.openSession();
        } catch (HibernateException he) {
            throw he;
        }
    }

    public static Session getCurrentSession() throws HibernateException {
        try {
            return sessionFactory.getCurrentSession();
        } catch (HibernateException he) {
            throw he;
        }
    }

    public static void main(String[] args) throws Exception {
        // Session session = openSession();
        // Transaction t = session.beginTransaction();
        //		
        // BlastQuery query = (BlastQuery) session.load(BlastQuery.class, new
        // Long(4140));
        // session.delete(query);
        //		
        // User user = (User) session.load(User.class, new Long(1));
        // Set<BlastQuery> queries = user.getQueries();
        // for (BlastQuery q : queries){
        // System.out.println(q.getId());
        // }
        // List<Query> qrs = session.createQuery("from Query").list();
        // for (Query q : qrs){
        // System.out.println(q.getId());
        // }

        // t.commit();
        // session.close();
    }
}