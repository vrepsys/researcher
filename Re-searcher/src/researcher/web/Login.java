package researcher.web;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.InjectState;
import org.apache.tapestry.valid.ValidationDelegate;

import researcher.beans.User;
import researcher.cache.Cache;
import researcher.exceptions.SystemUnavailableException;
import researcher.utils.Passwords;

public abstract class Login extends PersistenceProviderPage {

    abstract public String getUsername();

    abstract public String getPassword();
    
    @Asset("images/logo.jpg")
    public abstract IAsset getLogoImage();

    @InjectState("user")
    public abstract User getUser();

    public boolean getAllowRegistration() {
        if (Cache.getConfig() == null)
            return false;
        Boolean anu = Cache.getConfig().getAllowUserRegistration();
        if (anu == null)
            anu = false;
        return anu;
    }

    @Bean
    public abstract ValidationDelegate getDelegate();

    public void onLogin(IRequestCycle cycle) {

        User user = null;
        try {
            String pwd = Passwords.constructEncryptedPassword(getUsername(), getPassword());
            user = getPersistenceService().globalDAO().getUser(pwd);
        } catch (SystemUnavailableException e) {
            e.printStackTrace();
        }

        if (user == null) {
            ValidationDelegate delegate = getDelegate();
            delegate.setFormComponent(null);
            delegate.record("Login failed", null);
            return;
        }
        getUser().copyFrom(user);
        if (user.isAdmin()) {
            cycle.activate("UserList");
        } else {
            cycle.activate("SequenceList");
        }
    }
    
    
    public void onNewUser(IRequestCycle cycle) {
           cycle.activate("NewUser");
   }
    
}