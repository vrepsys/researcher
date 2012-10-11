package researcher.web;

import java.util.UUID;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.valid.ValidationDelegate;

import researcher.beans.User;
import researcher.exceptions.SystemUnavailableException;
import researcher.utils.Passwords;


public abstract class NewUser extends ResearcherPage implements PageBeginRenderListener {

    @Bean
    public abstract ValidationDelegate getDelegate();

    public abstract String getUsername();

    public abstract void setUsername(String userName);

    public abstract String getPassword();

    public abstract void setPassword(String password);

    public abstract String getEmail();

    public abstract void setEmail(String email);

    public abstract boolean isAdmin();

    public abstract void setAdmin(boolean isAdmin);

    @Persist("client")
    public abstract Long getUpdatableUserId();

    public abstract void setUpdatableUserId(Long userId);
    
    @Asset("images/logo.jpg")
    public abstract IAsset getLogoImage();

    @Override
    public void pageBeginRender(PageEvent event) {
        Long userId = getUpdatableUserId();
        if (userId != null) {
            User u = getPersistenceService().globalDAO().getUser(userId);
            setUsername(u.getUsername());
            setEmail(u.getEmail());
            setAdmin(u.isAdmin());
        }
        super.pageBeginRender(event);
    }

    public ILink onOk(IRequestCycle cycle) throws SystemUnavailableException {
        ValidationDelegate delegate = getDelegate();
        if (delegate.getHasErrors()) {
            cycle.activate("NewUser");
            return null;
        }
        User user;
        if (getUpdatableUserId() != null) {
            user = getPersistenceService().globalDAO().getUser(getUpdatableUserId());
        } else {
        	if (getPersistenceService().globalDAO().getUserByUsername(getUsername()) != null) {
                delegate.setFormComponent((IFormComponent) getComponent("username"));
                delegate.recordFieldInputValue(getUsername());
                delegate.record(getMessages().getMessage("userAlreadyExists"), null);
        	}
            user = new User();
        }
        if (delegate.getHasErrors()) {
            cycle.activate("NewUser");
            return null;
        }
        user.setUsername(getUsername());
        user.setEmail(getEmail());
        if (user.getUuid() == null) user.setUuid(UUID.randomUUID().toString());
        String passwd = getPassword();
        passwd = Passwords.constructEncryptedPassword(getUsername(), passwd);
        user.setPassword(passwd);
        user.setAdmin(isAdmin());
        getPersistenceService().globalDAO().saveOrUpdatetUser(user);        
        return getPageService().getLink(false, "UserList");
    }

}
