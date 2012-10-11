package researcher.web;

import java.util.List;

import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.valid.ValidationDelegate;

import researcher.beans.BlastDatabase;
import researcher.cache.Cache;
import researcher.db.GlobalDAO;
import researcher.utils.Utils;


public abstract class Configuration extends ResearcherPage {

    @Bean
    public abstract ValidationDelegate getDelegate();

    public abstract String getDatabaseName();

    public abstract String getDatabasePath();

    public abstract String getBlastHostname();

    public abstract void setBlastHostname(String blastHostname);

    public abstract String getBlastUsername();

    public abstract void setBlastUsername(String blastUsername);

    public abstract String getPathToPrivateKey();

    public abstract void setPathToPrivateKey(String pathToPrivateKey);

    public abstract String getPassphrase();

    public abstract void setPassphrase(String passphrase);

    public abstract String getPsiBlastCommand();

    public abstract void setPsiBlastCommand(String psiBlastCommand);

    public abstract String getFastaCmdCommand();

    public abstract void setFastaCmdCommand(String fastaCmdCommand);

    public abstract String getPathToTmp();

    public abstract void setPathToTmp(String pathToTmp);

    public abstract Integer getProcessorsToUse();

    public abstract void setProcessorsToUse(Integer processorsToUse);
    
    public abstract String getSmtpHostname();
    
    public abstract void setSmtpHostname(String smtpHostname);
    
    public abstract String getEmailFrom();
    
    public abstract void setEmailFrom(String emailFrom);
    
    public abstract String getApplicationLink();
    
    public abstract void setApplicationLink(String emailFrom);
    
    public abstract boolean getAllowRegistration();
    
    public abstract String getSshPassword();
    
    public abstract void setSshPassword(String sshPassword);
    
    public abstract void setAllowRegistration(boolean a);

    @Override
    public void pageBeginRender(PageEvent event) {
        researcher.beans.Configuration config = getPersistenceService().globalDAO().getConfiguration();
        if (config != null) {
            setBlastHostname(config.getBlastHostName());
            setBlastUsername(config.getBlastUserName());
            setPathToPrivateKey(config.getPathToPrivateKey());
            setPassphrase(config.getPassphrase());
            setPathToTmp(config.getPathToTmp());
            setPsiBlastCommand(config.getPsiBlastCommand());
            setProcessorsToUse(config.getProcessorsToUse());
            setFastaCmdCommand(config.getFastaCmdCommand());
            setEmailFrom(config.getEmailFrom());
            setSmtpHostname(config.getSmtpHostname());
            setApplicationLink(config.getAppLink());
            Boolean allow = config.getAllowUserRegistration();
            if (allow == null) allow = false;
            setAllowRegistration(allow);
            setSshPassword(config.getSshPassword());
        }
        super.pageBeginRender(event);
    }

    public List<BlastDatabase> getBlastDatabases() {
        return Cache.getLocalPsiblastDatabases();
    }

    public ILink onOk() {
        BlastDatabase blastdb = new BlastDatabase();
        blastdb.setName(getDatabaseName());
        blastdb.setPath(getDatabasePath());
        getPersistenceService().globalDAO().insertBlastDatabase(blastdb);
        Cache.refreshLocalPsiblastDatabases();
        return getPageService().getLink(false, "Configuration");
    }
    
    public void onSaveParameters() {
        researcher.beans.Configuration config = new researcher.beans.Configuration();
        config.setBlastHostName(getBlastHostname());
        config.setBlastUserName(getBlastUsername());
        String passphrase = getPassphrase();
        if (passphrase == null)
            passphrase = "";
        config.setPassphrase(passphrase);
        config.setPathToPrivateKey(getPathToPrivateKey());
        String path = getPathToTmp();
        if (path != null)
        	path = Utils.appendSlashIfNoSlash(path);
        config.setPathToTmp(path);        
        config.setPsiBlastCommand(getPsiBlastCommand());
        config.setProcessorsToUse(getProcessorsToUse());
        config.setFastaCmdCommand(getFastaCmdCommand());
        config.setSmtpHostname(getSmtpHostname());
        config.setEmailFrom(getEmailFrom());
        config.setAppLink(getApplicationLink());
        config.setAllowUserRegistration(getAllowRegistration());
        config.setSshPassword(getSshPassword());
        getPersistenceService().globalDAO().setConfiguration(config);
        Cache.refreshConfig();
    }

    public ILink onClickDelete(long databaseId) {
        getPersistenceService().globalDAO().deleteBlastDatabase(databaseId);
        Cache.refreshLocalPsiblastDatabases();
        return getPageService().getLink(false, "Configuration");
    }

}
