package researcher.web;

import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.html.BasePage;

public abstract class Home extends BasePage implements PageBeginRenderListener {

    @InjectPage("Login")
    public abstract Login getLoginPage();

    public void pageBeginRender(PageEvent event) {
        throw new PageRedirectException(getLoginPage());
    }

}
