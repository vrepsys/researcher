package researcher.web;

import java.sql.SQLException;
import java.util.List;

import org.apache.tapestry.annotations.InjectPage;

import researcher.beans.BlastQuery;


public abstract class SequenceList extends ResearcherPage {

    @InjectPage("HitList")
    public abstract HitList getHitListPage();

    @InjectPage("QueryDetails")
    public abstract QueryDetails getQueryDetailsPage();

    public abstract BlastQuery getQuery();

    public List<BlastQuery> getQueries() throws SQLException {
        return getPersistenceService().globalDAO().getQueries(getUser());
    }

    public void onClickDelete(long queryId) throws SQLException {
        getPersistenceService().globalDAO().deleteQuery(queryId);
    }

    public void onClickSearchNow(long queryId) throws SQLException {
        BlastQuery q = getPersistenceService().globalDAO().getQuery(queryId);
        q.setAskingForSearch(true);
        q.setBlastErrors(null);
    }

    public QueryDetails onClickView(long queryId) {
        getQueryDetailsPage().setQueryId(queryId);
        return getQueryDetailsPage();
    }

    public HitList onClickSequence(long queryId) {
        getHitListPage().setQueryId(queryId);
        return getHitListPage();
    }

}
