package researcher.web;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.InjectState;
import org.apache.tapestry.annotations.InjectStateFlag;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.event.PageValidateListener;

import researcher.beans.User;
import researcher.cache.Cache;
import researcher.web.components.Visit;


public abstract class ResearcherPage extends PersistenceProviderPage implements PageValidateListener,
        PageBeginRenderListener {

    @InjectState("visit")
    public abstract Visit getVisitInfo();

    @InjectState("user")
    public abstract User getUser();

    @InjectStateFlag("user")
    public abstract boolean getUserExists();

    @InjectPage("Login")
    public abstract Login getLoginPage();

    @InjectObject("engine-service:page")
    public abstract IEngineService getPageService();

    protected long getUserId() {
        return getUser().getId();
    }

    public void pageValidate(PageEvent event) {
        String pageName = event.getPage().getPageName();
        if (Cache.getConfig() != null){
            Boolean anu = Cache.getConfig().getAllowUserRegistration();
            if (anu == null) anu = false;
            if (anu && pageName != null && pageName.equals("NewUser")){
                return;
            }
        }
        if (!getUserExists()) {
            Login login = getLoginPage();
            throw new PageRedirectException(login);
        }
        User user = getUser();
        if (!user.hasRightsOnPage(pageName)) {
            Login login = getLoginPage();
            throw new PageRedirectException(login);
        }
    }

    public void pageBeginRender(PageEvent event) {
        Visit visit = getVisitInfo();
        visit.setActivePageName(event.getPage().getPageName());
    }

    @Override
    public void renderPage(IMarkupWriter w, IRequestCycle cycle) {
        super.renderPage(w, cycle);
    }

}
