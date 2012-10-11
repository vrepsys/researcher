package researcher.db.service;

import org.hibernate.Session;

import researcher.db.GlobalDAO;


import com.javaforge.honeycomb.service.AbstractPersistenceService;

public class ResearcherPersistenceServiceImpl extends AbstractPersistenceService implements
        ResearcherPersistenceService {

    public ResearcherPersistenceServiceImpl(Session hibernateSession) {
        super(hibernateSession);
    }

    public GlobalDAO globalDAO() {
        return new GlobalDAO(session);
    }

}
