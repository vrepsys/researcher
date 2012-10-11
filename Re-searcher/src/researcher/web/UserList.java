package researcher.web;

import java.sql.SQLException;
import java.util.List;

import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.engine.ILink;

import researcher.beans.User;


public abstract class UserList extends ResearcherPage {

    @InjectPage("NewUser")
    public abstract NewUser getNewUserPage();

    public List<User> getUsers() {
        System.out.println("GET USERS!!!!");
        return getPersistenceService().globalDAO().getUsers();
    }

    public ILink onClickDelete(long userId) throws SQLException {
        getPersistenceService().globalDAO().deleteUser(userId);
        return getPageService().getLink(false, "UserList");
    }

    public NewUser onClickEdit(long userId) throws SQLException {
        NewUser nupage = getNewUserPage();
        nupage.setUpdatableUserId(userId);
        return nupage;
    }

}
