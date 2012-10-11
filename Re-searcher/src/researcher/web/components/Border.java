package researcher.web.components;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.TapestryUtils;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.InjectState;
import org.apache.tapestry.annotations.InjectStateFlag;
import org.apache.tapestry.annotations.Message;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;

import researcher.beans.User;


public abstract class Border extends BaseComponent implements PageBeginRenderListener {

    @InjectState("visit")
    public abstract Visit getVisit();

    @Message
    public abstract String getUserTabOrder();

    @Message
    public abstract String getAdminTabOrder();

    @InjectState("user")
    public abstract User getUser();

    @InjectStateFlag("user")
    public abstract boolean getUserExists();

    private static String[] userTabOrder;

    private static String[] adminTabOrder;
    
    @Asset("images/logo.jpg")
    public abstract IAsset getLogoImage();

    public void pageBeginRender(PageEvent event) {
        if (userTabOrder == null) {
            userTabOrder = TapestryUtils.split(getUserTabOrder(), ' ');
        }
        if (adminTabOrder == null) {
            adminTabOrder = TapestryUtils.split(getAdminTabOrder(), ' ');
        }
    }

    public String[] getPageTabNames() {
        if (!getUserExists())
            return null;
        if (getUser().isAdmin()) {
            return adminTabOrder;
        } else {
            return userTabOrder;
        }
    }

    public abstract void setPageName(String value);

    public abstract String getPageName();

    public boolean isActivePage() {
        return getPageName().equals(getVisit().getActivePageName());
    }

    public String getPageTitle() {
        return getMessages().getMessage(getPageName());
    }

    public void selectPage(IRequestCycle cycle, String newPageName) {
        Visit visit = getVisit();
        visit.setActivePageName(newPageName);
        cycle.activate(newPageName);
    }

}