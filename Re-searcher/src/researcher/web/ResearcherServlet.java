package researcher.web;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.hivemind.Registry;
import org.apache.tapestry.ApplicationServlet;

import researcher.blast.periodical.JobsManager;


public class ResearcherServlet extends ApplicationServlet {

    private static final long serialVersionUID = -8834887872261414711L;

    private static Registry hivemindRegistry = null;

    public static Registry getHivemindRegistry() {
        return hivemindRegistry;
    }      

    @Override
    public void init(ServletConfig arg0) throws ServletException {
        super.init(arg0);
        try {
            Rights.createAdminIfNoUsersExist();
            JobsManager.startPeriodical();
        } catch (Throwable e) {
            e.printStackTrace();
        }        
    }

    
    
    @Override
    protected Registry constructRegistry(ServletConfig arg0) {
        hivemindRegistry = super.constructRegistry(arg0);        
        return hivemindRegistry;
    }

    @Override
    public void destroy() {
        JobsManager.stopPeriodical();
        super.destroy();
    }

}
