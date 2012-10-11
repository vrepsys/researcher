package researcher.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import researcher.beans.BlastDatabase;
import researcher.beans.Configuration;
import researcher.db.GlobalDAO;


public class Cache {

    private static Configuration config;

    public static synchronized Configuration getConfig() {
        if (config != null)
            return config;
        GlobalDAO dao = null;
        try{
            dao = new GlobalDAO();
            config = dao.getConfiguration();
            if (config == null) {
            	config = new Configuration();
            	config.setAllowUserRegistration(true);
            	dao.setConfiguration(config);
            }
        }
        finally{
            if (dao != null)
                dao.close();
        }
        return config;
    }

    public static void refreshConfig() {
        config = null;
    }

    private static List<BlastDatabase> ncbiDatabases;

    public static List<BlastDatabase> getNcbiDatabases() {
        if (ncbiDatabases != null)
            return new ArrayList(ncbiDatabases);
        ResourceBundle rb = ResourceBundle.getBundle("researcher");
        String dbs = rb.getString("ncbi-databases");
        String[] dbases = dbs.split(" ");
        List<BlastDatabase> databases = new ArrayList<BlastDatabase>();
        for (String db : dbases) {
            BlastDatabase blastdb = new BlastDatabase();
            blastdb.setName(db);
            blastdb.setPath(db);
            databases.add(blastdb);
        }
        ncbiDatabases = databases;
        return ncbiDatabases;
    }

    private static List<BlastDatabase> localPsiblastDatabases;

    public static List<BlastDatabase> getLocalPsiblastDatabases() {
        if (localPsiblastDatabases != null)
            return new ArrayList(localPsiblastDatabases);
        GlobalDAO dao = null;
        try{
            dao = new GlobalDAO();
            localPsiblastDatabases = dao.getBlastDatabases();
        }
        finally{
            if (dao != null) dao.close();
        }
        return localPsiblastDatabases;
    }

    public static void refreshLocalPsiblastDatabases() {
        localPsiblastDatabases = null;
    }
    
    public static void main(String[] args) throws Throwable {
        try {
            String s = null;
            System.out.println("before exception");
            throw new Throwable();            
        }
        finally{
            System.out.println("finally");
        }
    }

}
