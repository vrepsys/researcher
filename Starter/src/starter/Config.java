package starter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class Config {

    private String home;
    
    Properties props;
    
    
    
    public Config(String home) throws FileNotFoundException, IOException {
        if (!home.endsWith("/") && !home.endsWith("\\")){
            home = home + "/";
        }        
        this.home = home;
        props = new Properties();        
        props.load(new FileInputStream(home+"conf/config.properties"));
    }
    
    public String getDatabasePath(){
        return home + "db";
    }
    
    public String getLogFilePath() throws IOException{
    	String l4jfile = home+"conf/log4j.template";
        String s = Utils.readTextFile(l4jfile);
        String logFolder = home.replaceAll("[\\\\]", "/")+"log/";      
        s = s.replaceAll("\\$\\{LOGPATH\\}", logFolder);        
        String path = home+"conf/log4j.properties";
        FileWriter fw = new FileWriter(new File(path));
        fw.write(s); fw.flush(); fw.close(); 
        path = new File(path).toURI().toString();
        return path;
    }
    
    public int getPort(){
        String portS = props.getProperty("port");
        if (portS == null) return 8080;
        return new Integer(portS);
    }
    
    public String getHost(){
        String host = props.getProperty("host");
        if (host == null) return "127.0.0.1";
        return host;
    }
    
    public String getContextPath(){
        String contextPath = props.getProperty("context-path");
        if (contextPath == null) return "/Researcher";
        return contextPath;
    }
    
    public String getResearcherPath(){
        String researcherPath = props.getProperty("researcher-path");
        if (researcherPath == null) return home+"researcher.jar";
        return researcherPath;
    }
    
    public int getStopPort(){
        String port = props.getProperty("stop-port");
        if (port == null) return 9999;
        return Integer.valueOf(port);
    }
    
    public static void main(String[] args) {

    }

}
