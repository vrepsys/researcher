package researcher.web;

import researcher.utils.SeqUtil;

public abstract class ViewSequence extends ResearcherPage {

	
	
    public abstract String getSequence();

    public abstract void setSequence(String sequence);
    
    public String getLinkedSequence() {
    	String seq = getSequence();
    	if (seq == null) return "";
    	String gi = SeqUtil.extractGiNumber(seq);
    	if (gi != null) {
	    	StringBuffer bf = new StringBuffer();
	    	bf.append("<a href=\"");
	    	bf.append(SeqUtil.NCBI_LINK);
	    	bf.append(gi);
	    	bf.append("\">");
	    	bf.append(gi);
	    	bf.append("</a>");
	    	seq = seq.replace(gi, bf.toString());
    	}
    	seq = SeqUtil.formatFasta(seq, 80);
    	return seq;
    }

}
