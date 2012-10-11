package researcher.web;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.html.BasePage;

import researcher.db.service.ResearcherPersistenceService;


public abstract class PersistenceProviderPage extends BasePage {

    @InjectObject(value = "service:honey.ResearcherPersistenceService")
    public abstract ResearcherPersistenceService getPersistenceService();

}
