package researcher.web;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageAttachListener;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.web.WebRequest;

import researcher.beans.BlastDatabase;
import researcher.beans.BlastQuery;
import researcher.beans.SecondaryBlastQuery;
import researcher.beans.User;
import researcher.blast.SearchLocation;


public abstract class QueryDetails extends ResearcherPage implements PageBeginRenderListener, PageAttachListener, IExternalPage {

	
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
	
    @InjectPage("NewSequence")
    public abstract NewSequence getNewSequencePage();

    @Persist("client")
    public abstract long getQueryId();

    public abstract void setQueryId(long queryId);

    public abstract BlastQuery getQuery();

    public abstract void setQuery(BlastQuery query);

    @Override
    public void pageBeginRender(PageEvent event) {
        setQuery(getPersistenceService().globalDAO().getQuery(getQueryId()));
        super.pageBeginRender(event);
    }

    public NewSequence onClickNewSequence() {
        BlastQuery q = getPersistenceService().globalDAO().getQuery(getQueryId());
        
        NewSequence nsp = getNewSequencePage();
        nsp.setNeedRefreshMaxHits(false);
        nsp.setCommandLineOptions(q.getCommandLineOptions());
        
        nsp.setCompositionalAdjustments(q.getCompositionalAdjustments());
        
        nsp.setCostToExtendAGap(q.getCostToExtendAGap());
        
        nsp.setCostToOpenAGap(q.getCostToOpenAGap());
        
        BlastDatabase db = new BlastDatabase();
        db.setName(q.getDatabaseName());
        db.setPath(q.getDatabasePath());        
        if (q.getSearchLocation().equals(SearchLocation.LOCAL_SERVER)){
            nsp.setDatabase(db);            
        }        
        nsp.setEValue(q.getEvalue());        
        nsp.setEValueMultipass(q.getEvalueMultipass());
        nsp.setLowComplexity(q.isLowComplexityFilter());        
        nsp.setMaskLookup(q.isMaskForLookupTableOnlyFilter());        
        nsp.setMatrix(q.getMatrix());
        if (q.getSearchLocation().equals(SearchLocation.NCBI)){
            nsp.setNcbiDatabase(db);
        }        
        nsp.setNotifyByMail(q.getNotifyByMail());
        nsp.setNumberOfIterations(q.getIterations());
        nsp.setPeriod(q.getPeriod());
        nsp.setPhiPattern(q.getPhiPattern());
        nsp.setPssm(q.getPssm());
        nsp.setRestartFileBlob(q.getInputAlignmentFile());
        nsp.setRestartFileName(q.getInputAlignmentFileName());
        nsp.setSearchLocation(q.getSearchLocation());
        nsp.setSequenceName(q.getSequenceName());
        nsp.setSequenceString(q.getSequence());
        nsp.setWordSize(q.getWordSize());        
        nsp.setMaxNumberOfHits(q.getMaxNumberOfHits());
        
        SecondaryBlastQuery q2 = q.getSecondaryQuery();
        if (q2 != null) {
            nsp.setCommandLineOptions2(q2.getCommandLineOptions());
            nsp.setCompositionalAdjustments2(q2.getCompositionalAdjustments());
            nsp.setCostToExtendAGap2(q2.getCostToExtendAGap());
            nsp.setCostToOpenAGap2(q2.getCostToOpenAGap());
            BlastDatabase db2 = new BlastDatabase();
            db2.setName(q2.getDatabaseName());
            db2.setPath(q2.getDatabasePath());
            if (q.getSearchLocation().equals(SearchLocation.LOCAL_SERVER)){
                nsp.setDatabase2(db2);
            }
            nsp.setEValue2(q2.getEvalue());
            nsp.setLowComplexity2(q2.isLowComplexityFilter());
            nsp.setMaskLookup2(q2.isMaskForLookupTableOnlyFilter());
            nsp.setMatrix2(q2.getMatrix());
            if (q.getSearchLocation().equals(SearchLocation.NCBI)){
                nsp.setNcbiDatabase2(db2);
            }
            nsp.setWordSize2(q2.getWordSize());
        }
        return nsp;
    }

}
