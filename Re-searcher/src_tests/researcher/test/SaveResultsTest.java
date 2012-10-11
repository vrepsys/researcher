package researcher.test;

import java.io.IOException;
import java.util.Set;

import org.hibernate.Session;

import researcher.beans.BlastQuery;
import researcher.beans.Hit;
import researcher.blast.periodical.JobsManager;
import researcher.db.GlobalDAO;
import researcher.test.utils.Stuff;
import researcher.test.utils.TestHibUtil;

public class SaveResultsTest {

    public static Set<Hit> testHitsSave(BlastQuery query, Set<Hit> hits) throws IOException,
            InterruptedException {

        Session s = TestHibUtil.getSession();
        s.beginTransaction();
        GlobalDAO dao = new GlobalDAO(s);

        JobsManager.saveHits(query, hits, dao, Stuff.log);

        s.getTransaction().commit();
        s.close();

        return hits;
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        System.setProperty("derby.system.home", "/home/posu/projects/Starter/db/");

        System.out.println("Saving query1 results..");
        testHitsSave(Init.getQuery1(), Init.getQuery1Hits());

        System.out.println();
        System.out.println("Saving query2 results..");
        testHitsSave(Init.getQuery2(), Init.getQuery2Hits());

        System.out.println("TEST FINISHED");

    }

}
