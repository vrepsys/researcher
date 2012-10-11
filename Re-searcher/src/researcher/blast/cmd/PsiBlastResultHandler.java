package researcher.blast.cmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import researcher.blast.BlastTypes;
import researcher.blast.BlastTypes.AlignmentDetails;
import researcher.blast.BlastTypes.HitDetails;
import researcher.blast.BlastTypes.SubHitDetails;


public class PsiBlastResultHandler implements ContentHandler {

    private List<HitDetails> hitList = new LinkedList<HitDetails>();

    private String m_queryDef = null;

    private StringBuffer m_hitAccession = new StringBuffer();

    private StringBuffer m_hitId = new StringBuffer();

    private StringBuffer m_hitDesc = new StringBuffer();

    private StringBuffer m_hitLen = new StringBuffer();

    private String m_tag = null;

    private HashMap<String, String> hitstats = new HashMap<String, String>();

    private StringBuffer m_idstr = new StringBuffer();

    private StringBuffer m_progname = new StringBuffer();

    private PsiBlastResult result;

    private List<SubHitDetails> m_subHitList = new LinkedList<SubHitDetails>();

    private static final Comparator<SubHitDetails> subHitDetailsComparator = new Comparator<SubHitDetails>() {
        public int compare(SubHitDetails o1, SubHitDetails o2) {
            if (o1 == o2)
                return 0;
            double sub = o1.getScore() - o2.getScore();
            if (sub > 0)
                return 1;
            if (sub < 0)
                return -1;
            return 0;
        }
    };

    private StringBuffer m_iteration = new StringBuffer();

    /**
     * @param values
     * @param start
     * @param length
     * @throws SAXException
     */
    public void characters(char[] values, int start, int length) throws org.xml.sax.SAXException {
        if (m_tag != null) {
            String newstuff;
            if (m_tag.equals("BlastOutput_query-def")) {
                newstuff = new String(values, start, length);
                m_queryDef = (m_queryDef != null) ? m_queryDef + newstuff : newstuff;
            } else if (m_tag.equals("Hit_accession")) {
                m_hitAccession.append(values, start, length);
            } else if (m_tag.equals("Hit_id")) {
                m_hitId.append(values, start, length);
            } else if (m_tag.equals("Hit_def")) {
                m_hitDesc.append(values, start, length);
            } else if (m_tag.equals("Hit_len")) {
                m_hitLen.append(values, start, length);
            } else if (m_tag.startsWith("Hsp_")) {
                String oldval = (String) hitstats.get(m_tag);
                newstuff = new String(values, start, length);
                String newval = (oldval != null) ? oldval + newstuff : newstuff;
                hitstats.put(m_tag, newval);
            } else if (m_tag.equals("BlastOutput_program")) {
                m_progname.append(values, start, length);
            } else if (m_tag.equals("BlastOutput_query-ID")) {
                m_idstr.append(values, start, length);
            } else if (m_tag.equals("Iteration_iter-num")) {
                m_iteration.append(values, start, length);
            }
        }
    }

    /**
     * @throws SAXException
     */
    public void endDocument() throws org.xml.sax.SAXException {
    }

    /**
     * @param uri
     * @param localname
     * @param qname
     * @param attributes
     * @throws SAXException
     */
    public void startElement(String uri, String localname, String qname,
            org.xml.sax.Attributes attributes) throws org.xml.sax.SAXException {
        m_tag = localname;
        if (m_tag.equals("BlastOutput_query-def")) {

        }
    }

    /**
     * @param uri
     * @param localname
     * @param qname
     * @throws SAXException
     */
    public void endElement(String uri, String localname, String qname)
            throws org.xml.sax.SAXException {
        if (localname.equals("Hit")) {
            processHit();
            m_hitAccession = new StringBuffer();
            m_hitId = new StringBuffer();
            m_hitDesc = new StringBuffer();
            m_hitLen = new StringBuffer();
        } else if (localname.equals("Hsp")) {
            processSubHit();
            hitstats.clear();
        } else if (localname.equals("Iteration")) {
            m_iteration = new StringBuffer();
        }
        m_tag = null;
    }

    /**
     * @param prefix
     * @throws SAXException
     */
    public void endPrefixMapping(String prefix) throws org.xml.sax.SAXException {
    }

    /**
     * @param values
     * @param param
     * @param param2
     * @throws SAXException
     */
    public void ignorableWhitespace(char[] values, int param, int param2)
            throws org.xml.sax.SAXException {
    }

    /**
     * @param str
     * @param str1
     * @throws SAXException
     */
    public void processingInstruction(String str, String str1) throws org.xml.sax.SAXException {
    }

    /**
     * @param locator
     */
    public void setDocumentLocator(org.xml.sax.Locator locator) {
    }

    /**
     * @param name
     * @throws SAXException
     */
    public void skippedEntity(String name) throws org.xml.sax.SAXException {
    }

    /**
     * @throws SAXException
     */
    public void startDocument() throws org.xml.sax.SAXException {
    }

    /**
     * @param str
     * @param str1
     * @throws SAXException
     */
    public void startPrefixMapping(String str, String str1) throws org.xml.sax.SAXException {
    }

    /**
     * Process a single hit by adding its targets to the
     * SeqSimilaritySearchResult.
     */
    void processHit() {
        SubHitDetails maxSubHit = Collections.max(m_subHitList, subHitDetailsComparator);
        double eValue = maxSubHit.getExpectValue();
        double score = maxSubHit.getScore();
        double bitScore = maxSubHit.getBitScore();
        int iteration = new Integer(m_iteration.toString());

        HitDetails hit = new BlastTypes.HitDetails(m_hitId.toString(), m_hitDesc.toString(),
                iteration);
        hit.setEValue(eValue);
        hit.setSequenceLength(m_hitLen.toString());
        hit.setScore(score);
        hit.setBitScore(bitScore);
        hit.setSubhits(new ArrayList(m_subHitList));
        hit.setAccession(m_hitAccession.toString());
        System.out.println("hit accession = " + hit.getAccession());
        hitList.add(hit);
        m_subHitList.clear();
    }

    /**
     * A 'subhit' is an individual HSP.
     */
    void processSubHit() {
        double score = new Double(hitstats.get("Hsp_score"));
        double bitScore = new Double(hitstats.get("Hsp_bit-score"));
        double eValue = new Double(hitstats.get("Hsp_evalue"));
        String qseq = hitstats.get("Hsp_qseq");
        String hseq = hitstats.get("Hsp_hseq");
        String midline = hitstats.get("Hsp_midline");
        int queryFrom = new Integer(hitstats.get("Hsp_query-from"));
        int queryTo = new Integer(hitstats.get("Hsp_query-to"));
        int hitFrom = new Integer(hitstats.get("Hsp_hit-from"));
        int hitTo = new Integer(hitstats.get("Hsp_hit-to"));
        String hspNum = hitstats.get("Hsp_num");
        String queryFrame = hitstats.get("Hsp_query-frame");
        String hitFrame = hitstats.get("Hsp_hit-frame");
        String alignLen = hitstats.get("Hsp_align-len");
        String identities = hitstats.get("Hsp_identity");
        String positives = hitstats.get("Hsp_positive");
        String gaps = hitstats.get("Hsp_gaps");
        AlignmentDetails alignment = new AlignmentDetails(qseq, queryFrom, queryTo, hseq, hitFrom,
                hitTo, midline);
        SubHitDetails subhit = new BlastTypes.SubHitDetails(score, eValue, bitScore);
        subhit.setAlignment(alignment);
        subhit.setAlignmentSize(Integer.valueOf(alignLen));
        subhit.setHitFrame(Integer.valueOf(hitFrame));
        subhit.setQueryFrame(Integer.valueOf(queryFrame));
        subhit.setHspNum(Integer.valueOf(hspNum));
        subhit.setNumberOfIdentities(Integer.valueOf(identities));
        subhit.setNumberOfPositives(Integer.valueOf(positives));
        if (gaps != null) {
            subhit.setNumberOfGaps(Integer.valueOf(gaps));
        } else {
            subhit.setNumberOfGaps(0);
        }
        m_subHitList.add(subhit);
    }

    /**
     * @return result of the search
     */
    public PsiBlastResult getResult() {
        if (result == null) {
            result = new PsiBlastResult(hitList);
        }
        return result;
    }
}
