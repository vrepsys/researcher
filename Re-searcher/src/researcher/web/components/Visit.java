package researcher.web.components;

import java.io.Serializable;

public class Visit implements Serializable {

    private static final long serialVersionUID = -7093771620429009491L;

    private String activePageName = "Home";

    public String getActivePageName() {
        return activePageName;
    }

    public void setActivePageName(String value) {
        activePageName = value;
    }
}