package researcher.beans;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import researcher.cache.Cache;

@Entity
@Table(name = "configuration")
public class Configuration {

    private long id;

    private String blastHostName;

    private String blastUserName;

    private String pathToPrivateKey;

    private String passphrase;

    private String psiBlastCommand;

    private String pathToTmp;

    private Integer processorsToUse;

    private String fastaCmdCommand;

    private String smtpHostname;

    private String emailFrom;

    private String appLink;

    private Boolean allowUserRegistration;
    
    private String sshPassword;

    public String getSshPassword() {
        return sshPassword;
    }

    public void setSshPassword(String sshPassword) {
        this.sshPassword = sshPassword;
    }

    public Boolean getAllowUserRegistration() {
        return allowUserRegistration;
    }

    public void setAllowUserRegistration(Boolean allowUserRegistration) {
        this.allowUserRegistration = allowUserRegistration;
    }

    public String getAppLink() {
        return appLink;
    }

    public void setAppLink(String appLink) {
        this.appLink = appLink;
    }

    public String getSmtpHostname() {
        return smtpHostname;
    }

    public void setSmtpHostname(String smtpHostname) {
        this.smtpHostname = smtpHostname;
    }

    public String getFastaCmdCommand() {
        return fastaCmdCommand;
    }

    public void setFastaCmdCommand(String fastaCmdCommand) {
        this.fastaCmdCommand = fastaCmdCommand;
    }

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBlastHostName() {
        return blastHostName;
    }

    public void setBlastHostName(String blastHostName) {
        this.blastHostName = blastHostName;
    }

    public String getBlastUserName() {
        return blastUserName;
    }

    public void setBlastUserName(String blastUserName) {
        this.blastUserName = blastUserName;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public String getPathToPrivateKey() {
        return pathToPrivateKey;
    }

    public void setPathToPrivateKey(String pathToPrivateKey) {
        this.pathToPrivateKey = pathToPrivateKey;
    }

    public String getPathToTmp() {
        return pathToTmp;
    }

    public void setPathToTmp(String pathToTmp) {
        this.pathToTmp = pathToTmp;
    }

    public String getPsiBlastCommand() {
        return psiBlastCommand;
    }

    public void setPsiBlastCommand(String psiBlastCommand) {
        this.psiBlastCommand = psiBlastCommand;
    }

    public Integer getProcessorsToUse() {
        return processorsToUse;
    }

    public void setProcessorsToUse(Integer processorsToUse) {
        this.processorsToUse = processorsToUse;
    }

    /*
     * Generates and returns a file name
     */
    @Transient
    public String generateInputAlignmentFileName() {
        return UUID.randomUUID() + ".tmp";
    }
    /*
     * returns a directory to store the input alignment file
     */
    @Transient
    public String getInputAlignmentDirectory() {
        return getPathToTmp();
    }
        
    /*
     * Generates a file name and returns full path to the file
     */
    @Transient
    public String generateCheckPointFilePathName() {
        return getPathToTmp() + UUID.randomUUID() + ".tmp";
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }
    
    public boolean emailConfigured(){
        if (emailFrom != null && smtpHostname != null) return true;
        return false;
    }

    @Transient
    public boolean localServerIsReady() {
        if (fastaCmdCommand != null && blastHostName != null && blastUserName != null
                && (pathToPrivateKey != null || sshPassword != null) && pathToTmp != null && processorsToUse != null
                && psiBlastCommand != null && Cache.getLocalPsiblastDatabases() != null)
            return true;
        return false;
    }

}
