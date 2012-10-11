package researcher.test;

import java.util.HashSet;
import java.util.Set;

import researcher.beans.Hit;
import researcher.blast.periodical.JobsManager;
import researcher.test.utils.Stuff;

public class GetNewHitsTest {

    private static void testGetNewHits(Set<Hit> existantHits) {

        Set<Hit> newHits = JobsManager.getNewHits(existantHits, existantHits, Stuff.log);

        if (newHits.size() != 0) {
            Stuff.log.error("newHits size must have been 0");
        }

        Set<Hit> foundHits = new HashSet<Hit>(existantHits);
        Hit hit = foundHits.iterator().next();
        // NOTE: after following operation, both hits (in found hits list and in
        // existantHits list)
        // get fasta == null
        hit.setFastaSequence(null);

        newHits = JobsManager.getNewHits(existantHits, foundHits, Stuff.log);

        if (newHits.size() != 0) {
            Stuff.log.error("newHits size must have been 0");
        }

        existantHits.remove(hit);

        newHits = JobsManager.getNewHits(existantHits, foundHits, Stuff.log);

        if (newHits.size() != 1) {
            Stuff.log.error("error testing new hits, new hits size == 1 expected, now size == "
                    + newHits.size());
        }

    }
    
    public static void main(String[] args) {
        testGetNewHits(Init.getQuery1Hits());
        testGetNewHits(Init.getQuery2Hits());
        
        System.out.println("TEST FINISHED");
    }
    
}
