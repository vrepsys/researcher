package researcher.db.service;

import researcher.db.GlobalDAO;

import com.javaforge.honeycomb.service.PersistenceService;

public interface ResearcherPersistenceService extends PersistenceService {
    public GlobalDAO globalDAO();
}
