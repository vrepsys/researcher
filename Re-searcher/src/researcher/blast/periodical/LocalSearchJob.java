package researcher.blast.periodical;

import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import researcher.beans.BlastQuery;
import researcher.beans.Configuration;
import researcher.blast.SearchLocation;
import researcher.blast.exec.SshPsiBlastExecutor;
import researcher.cache.Cache;
import researcher.db.GlobalDAO;


public class LocalSearchJob extends TimerTask {

    public static Logger log = Logger.getLogger("localPeriodical");

    @Override
    public void run() {
        try {
            
            final Configuration conf = Cache.getConfig();
            
            if (conf == null) {
                log.warn("No configuration - no go");
                return;
            }
            
            if (!conf.localServerIsReady()){
                log.info("Local server not configured yet - no go");
                return;
            }

            GlobalDAO dao = null;
            List<BlastQuery> queries = null;
            try {
                dao = new GlobalDAO();
                queries = dao.getBlastQueriesForSearch(SearchLocation.LOCAL_SERVER);
            }
            finally{
                if (dao != null) dao.close();
            }

            if (queries.size() > 0)
                log.info("local search queries=" + queries);

            final SshPsiBlastExecutor sshExecutor = new SshPsiBlastExecutor(conf);
            try {
            	JobsManager.executeQueriesAndSaveResults(sshExecutor, queries, conf, log);
            }
            finally {
            	sshExecutor.close();
            }

        } catch (final Throwable t) {        	
            log.error("unknown exception", t);
        }

    }
    
//    public static void initializeFasta() {
//        Session sess = HibernateUtil.openSession();
//        sess.beginTransaction(); 
//        List<BlastQuery> l = sess.createCriteria(BlastQuery.class)                
//                .setFetchMode("secondaryQuery", FetchMode.JOIN)
//                .setFetchMode("hits", FetchMode.JOIN).setResultTransformer(
//                        new DistinctRootEntityResultTransformer()).list();
//        
//        
//        SshPsiBlastExecutor sshExecutor = new SshPsiBlastExecutor(Cache.getConfig());
//        for (BlastQuery q : l){
//            if (q.getSearchLocation().equals(SearchLocation.LOCAL_SERVER)){
//                Set<Hit> hits = q.getHits();
//                for (Hit h : hits){                    
//                    String fasta = sshExecutor.getFasta(h, q.getDeepestDatabasePath());
//                    System.out.println("fasta="+fasta);
//                    h.setFasta(fasta);
//                }                
//            }
//            else {
//                QBlastExecutor qex = new QBlastExecutor();
//                Set<Hit> hits = q.getHits();
//                for (Hit h : hits){                    
//                    String fasta = qex.getFasta(h, q.getDeepestDatabasePath());
//                    System.out.println("fasta="+fasta);                    
//                    h.setFasta(fasta);
//                }
//            }
//        }
//        sshExecutor.close();
//        sess.getTransaction().commit();
//        sess.close();
//    }

}
