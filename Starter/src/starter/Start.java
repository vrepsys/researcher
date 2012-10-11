package starter;

import java.io.File;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class Start {  
    
    public static void main(String[] args) throws Exception {    	
        try {
        	
            if (args.length < 1) {
                System.err.println("The first argument of the Starter must be a researchers home dir");
                return;
            }
            String home = args[0];
            if (home.endsWith("/") || home.endsWith("\\")){
                home = home.substring(0, home.length()-1);
            }
            File file = new File(home);
            if (!file.exists()){
                System.err.println(home + " does not exist!");
                return;
            }
            Config c = new Config(home);            
            Server server = new Server();
            Connector connector = new SelectChannelConnector();
            connector.setPort (c.getPort());       
            connector.setHost (c.getHost());  
            server.addConnector (connector);
            WebAppContext wac = new WebAppContext();        
            wac.setContextPath (c.getContextPath());        
            wac.setWar (c.getResearcherPath());
            server.setHandler (wac);       
            server.start();            
            server.join();
        }
        catch(Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
        
    }
    

}
