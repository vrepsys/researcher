package researcher.web;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageAttachListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.web.WebRequest;

import researcher.beans.BlastQuery;
import researcher.beans.Hit;
import researcher.beans.User;


public abstract class HitList extends ResearcherPage implements PageAttachListener, IExternalPage {  
    
    @InjectObject("service:tapestry.globals.WebRequest")
    public abstract WebRequest getRequest();

    public void pageAttached(PageEvent event) {
         WebRequest req = getRequest();
         String uid = req.getParameterValue("uid");                 
         if (uid == null) return;         
         User user = getPersistenceService().globalDAO().getUserByUuid(uid);         
         if (user == null) return;
         getUser().copyFrom(user);         
         
    }
    
    public void activateExternalPage(Object[] arg0, IRequestCycle arg1) {     
        WebRequest req = getRequest();
        String queryid = req.getParameterValue("queryid");
        if (queryid != null)            
            setQueryId(new Long(queryid));                
    }

    @Persist("client")
    public abstract long getQueryId();

    public abstract void setQueryId(long queryId);

    public abstract Date getFromDate();

    public abstract void setFromDate(Date date);

    public abstract Date getTillDate();

    public abstract void setTillDate(Date date);

    @InjectPage("ViewSequence")
    public abstract ViewSequence getViewSequencePage();

    @InjectPage("Subhits")
    public abstract Subhits getSubhitsPage();

    @InjectPage("QueryDetails")
    public abstract QueryDetails getQueryDetailsPage();
    
    @InjectPage("FastaList")
    public abstract FastaList getFastaList();

    public BlastQuery getQuery() {
        return getPersistenceService().globalDAO().getQuery(getQueryId());
    }

    public List<Hit> getHits() throws SQLException {
        List<Hit> hits = getPersistenceService().globalDAO().getHits(getQueryId(), getFromDate(),
                getTillDate());
        return hits;
    }

    public void onClickMarkAsRead() {
        getPersistenceService().globalDAO().updateHitsIsNew(getQueryId(), false);
        getPersistenceService().globalDAO().getQuery(getQueryId()).setNumberOfNewHits(0);
    }

    public Subhits onClickAlignment(long hitId) {
        getSubhitsPage().setHitId(hitId);
        return getSubhitsPage();
    }

    public QueryDetails onClickView(long queryId) {
        getQueryDetailsPage().setQueryId(queryId);
        return getQueryDetailsPage();
    }

    public ViewSequence onClickSequenceName(long hitId) {
        Hit hit = getPersistenceService().globalDAO().getHit(hitId);
        getViewSequencePage().setSequence(hit.getFasta());
        return getViewSequencePage();
    }
    
    public FastaList onClickFastaList(){
        List<Hit> hits = getPersistenceService().globalDAO().getHitsWithFastas(getQuery());
        getFastaList().setHitList(hits);
        return getFastaList();
    }
    
    public FastaList onClickFastaListNew(){
        List<Hit> hits = getPersistenceService().globalDAO().getNewHitsWithFastas(getQuery());
        getFastaList().setHitList(hits);
        return getFastaList();
    }

}
