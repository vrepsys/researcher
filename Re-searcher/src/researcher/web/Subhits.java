package researcher.web;

import org.apache.tapestry.annotations.Persist;

import researcher.beans.Hit;


public abstract class Subhits extends ResearcherPage {

    @Persist("client")
    public abstract long getHitId();

    public abstract void setHitId(long hitId);

    public Hit getHit() {
        return getPersistenceService().globalDAO().getHit(getHitId());
    }

}
