package researcher.web;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageEvent;

import researcher.beans.Hit;

public abstract class FastaList extends ResearcherPage {

    public abstract List<Hit> getHitList();
    
    public abstract void setHitList(List<Hit> hits);
    
    @InjectObject("service:tapestry.globals.HttpServletResponse")
    public abstract HttpServletResponse getResponse();
    
    @Override
    public void pageBeginRender(PageEvent event) {
        getResponse().setContentType("text/plain");
        super.pageBeginRender(event);
    }
    
}
