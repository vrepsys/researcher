package researcher.web;

import java.util.ResourceBundle;

import org.apache.tapestry.TapestryUtils;
import org.hibernate.Session;

import researcher.beans.User;
import researcher.db.GlobalDAO;
import researcher.db.HibernateUtil;
import researcher.exceptions.SystemUnavailableException;
import researcher.utils.Passwords;


public class Rights {

    static {
        reloadMenu();
    }

    private static String[] userPages;

    private static String[] adminPages;

    public static void reloadMenu() {
        ResourceBundle rb = ResourceBundle.getBundle("researcher");
        userPages = TapestryUtils.split(rb.getString("user-pages"), ' ');
        adminPages = TapestryUtils.split(rb.getString("admin-pages"), ' ');
    }

    public static String[] getAdminPages() {
        return adminPages;
    }

    public static String[] getUserPages() {
        return userPages;
    }

    public static void createAdminIfNoUsersExist() {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            session.beginTransaction();
            GlobalDAO dao = new GlobalDAO(session);
            if (dao.getUsers().size() == 0) {
                User user = new User();
                user.setAdmin(true);
                user.setUsername("admin");
                user.setPassword(Passwords.constructEncryptedPassword("admin", "admin"));
                dao.saveOrUpdatetUser(user);
            }
            session.getTransaction().commit();
        } catch (SystemUnavailableException e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

}
