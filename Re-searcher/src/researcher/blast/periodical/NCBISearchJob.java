package researcher.blast.periodical;

import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import researcher.beans.BlastQuery;
import researcher.beans.Configuration;
import researcher.blast.SearchLocation;
import researcher.blast.exec.QBlastExecutor;
import researcher.cache.Cache;
import researcher.db.GlobalDAO;


public class NCBISearchJob extends TimerTask {

    public static Logger log = Logger.getLogger("ncbiPeriodical");

    @Override
    public void run() {
        
        try {

            Configuration conf = Cache.getConfig();
    
            GlobalDAO dao = null;
            List<BlastQuery> queries = null;
            try{
                dao = new GlobalDAO();
                queries = dao.getBlastQueriesForSearch(SearchLocation.NCBI);
            }
            finally{
                if (dao != null) dao.close();
            }
    
            if (queries.size() > 0)
                log.info("ncbi search queries=" + queries);
            
            QBlastExecutor qblastExecutor = new QBlastExecutor();
            JobsManager.executeQueriesAndSaveResults(qblastExecutor, queries, conf, log);
            
        }
        catch(Throwable t){
            log.error("unknown exception", t);
        }

    }

}
