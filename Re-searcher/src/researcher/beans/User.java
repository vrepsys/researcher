package researcher.beans;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Index;

import researcher.exceptions.SystemUnavailableException;
import researcher.utils.Passwords;
import researcher.web.Rights;


@Entity
@Table(name = "users")
public class User implements Serializable {

    private static final long serialVersionUID = 1858318434177120215L;

    private long id;

    private String username;

    private String password;

    private String email;

    private Set<BlastQuery> queries = new HashSet<BlastQuery>();

    private boolean isAdmin;

    private String uuid;
    
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public User() {
    }

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User(long id, String username, String password) {
        this.username = username;
        this.password = password;
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Index(name = "user_username_ind")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Index(name = "user_email_ind")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @OneToMany(mappedBy = "user", cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    public Set<BlastQuery> getQueries() {
        return queries;
    }

    public void setQueries(Set<BlastQuery> queries) {
        this.queries = queries;
    }

    public void addQuery() {
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    @Transient
    public boolean authenticate(String username, String password) throws SystemUnavailableException {
        String pwd = Passwords.constructEncryptedPassword(username, password);
        return this.username.equals(username) && this.password.equals(pwd);
    }

    @Transient
    public void copyFrom(User user) {        
        this.username = user.username;
        this.password = user.password;
        this.id = user.id;
        this.isAdmin = user.isAdmin;
        this.email = user.email;
    }

    @Transient
    public boolean hasRightsOnPage(String pageName) {
        String[] validPageNames;
        if (this.isAdmin()) {
            validPageNames = Rights.getAdminPages();
        } else {
            validPageNames = Rights.getUserPages();
        }
        if (Arrays.asList(validPageNames).contains(pageName)) {
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {     
        return "USER id="+id+" email="+email;
    }

}
