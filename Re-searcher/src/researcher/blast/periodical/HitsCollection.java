package researcher.blast.periodical;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import researcher.beans.FastaSequence;
import researcher.beans.Hit;
import researcher.utils.SeqUtil;

public class HitsCollection {
    
    private Collection<Hit> hits;
    
    private Set<String> sequences;
    
    private Set<String> hitsIds;
    
    private boolean allHitsHaveFastas = true;
    
    public HitsCollection(Collection<Hit> hits){
        this.hits = hits;
        this.sequences = new HashSet<String>();
        this.hitsIds = new HashSet<String>();
        for (Hit hit : hits) {
            final String fasta = hit.getFasta();
            if (fasta != null) {
                final String seq = SeqUtil.extractSequence(hit.getFasta());
                sequences.add(seq);
            }       
            final Set<String> ids = getHitIds(hit);
            hitsIds.addAll(ids);            
        }
        if (hits.size() !=  sequences.size()) allHitsHaveFastas = false;        
    }
    
    private static Set<String> getHitIds(Hit hit){
        StringBuffer s = new StringBuffer();
        if (hit.getSubjectId() == null) throw new NullPointerException("hits subject id must not be null");
        s.append(hit.getSubjectId());
        String def = hit.getSubjectDef();
        if ( def != null && !def.trim().equals("") ){
            s.append(" ");
            s.append(def);
        }
        return SeqUtil.extractFastaDescriptions(s.toString());
    }
    
    public boolean contains(final Hit hit) {
        final String fasta = hit.getFasta();
        if (fasta != null) {
            String seq = SeqUtil.extractSequence(hit.getFasta());
            boolean res = sequences.contains(seq);           
            if (res || allHitsHaveFastas) return res;
        }
        final Set<String> ids = getHitIds(hit);
        final String hitid = SeqUtil.removeEndingDashIfExists(hit.getSubjectId());
        ids.add(hitid);
        for (String id : ids) {
            if (hitsIds.contains(id)) {
                return true;
            }
        }
        return false;
    }

    public Collection<Hit> getHits() {
        return hits;
    }
    
    public int size(){
        return hits.size();
    }

}
