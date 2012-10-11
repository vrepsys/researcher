package researcher.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.xml.sax.SAXException;

import researcher.beans.Hit;
import researcher.beans.Subhit;
import researcher.db.HibernateUtil;


public class AlignmentsRenderer {

    public AlignmentsRenderer(Writer writer) {
        out = new PrintWriter(writer);
    }

    /**
     * The width, in characters, of the sequence alignments
     */
    private int iAlignLen = 50;

    private PrintWriter out;

    private void write(String s) {
        out.print(s);
    }

    private void write(double d) {
        out.print(String.valueOf(d));
        out.print(" ");
    }

    private void writeName(String s) {
        out.print(s);
        out.print(" = ");
    }

    public void renderHit(Hit hit) {
    	
    	String gi = null;
    	String fasta = hit.getFasta();
    	if (fasta != null) {
    		gi = SeqUtil.extractGiNumber(fasta);
    	}
    		
    	
    	
        write("<span style=\"font-weight: bold;\">");
        
        if (gi != null && gi.trim() != "") {
        	write("<a href=\"");
        	write(SeqUtil.NCBI_LINK);
        	write(gi);
        	write("\">");
        }
        
        write(hit.getSubjectId());
        
        if (gi != null && gi.trim() != "") {
        	write("</a>");
        }
        write("</span>");
        write("<br/>");
        write(hit.getSubjectDef());
        write("<br/>");
        writeName("Length");
        write(hit.getSequenceLength());
        write("<br/><br/>");
        renderSubhits(hit.getSubhits());
    }

    private static Comparator<Subhit> byHspNum = new Comparator<Subhit>() {
        public int compare(Subhit o1, Subhit o2) {
            return o1.getHspNum() - o2.getHspNum();
        }
    };

    private static final DecimalFormat percentFormat = new DecimalFormat("###%");

    private void renderSubhits(Set<Subhit> subhits) {
        List<Subhit> sorted = new ArrayList<Subhit>(subhits);
        Collections.sort(sorted, byHspNum);
        for (Subhit subhit : sorted) {
            writeName("&nbsp;&nbsp;Score");
            write(subhit.getBitScore());
            write("bits ");
            write("(" + subhit.getScore() + ")");
            writeName(", Expect");
            write(Formatter.getEvalueFormat().format(subhit.getExpectValue()));
            write("<br/>");

            int asize = subhit.getAlignmentSize();
            int identities = subhit.getNumberOfIdentities();
            double identPerc = (double) identities / asize;
            int positives = subhit.getNumberOfPositives();
            double posPerc = (double) positives / asize;
            int gaps = subhit.getNumberOfGaps();
            double gapsPerc = (double) gaps / asize;
            Format pf = percentFormat;
            writeName("&nbsp;&nbsp;Identities");
            write(identities + "/" + asize + " (" + pf.format(identPerc) + "), ");
            writeName("Positives");
            write(positives + "/" + asize + " (" + pf.format(posPerc) + "), ");
            writeName("Gaps");
            write(gaps + "/" + asize + " (" + pf.format(gapsPerc) + ")");
            write("<br/>");
            drawCurrentAlignment(subhit);
        }
    }

    /**
     * Draws a full alignment block.
     */
    void drawCurrentAlignment(Subhit subhit) {

        int i = 0;

        int iQueryStart = subhit.getQuerySeqFrom();
        int iHitStart = subhit.getHitSeqFrom();

        int iQueryLen = subhit.getQuerySeqTo() - iQueryStart;
        int iHitLen = subhit.getHitSeqTo() - iHitStart;

        int iNumberOfQueryGaps = 0;
        int iNumberOfHitGaps = 0;
        int index = -1;
        while ((index = subhit.getQuerySeq().indexOf('-', index + 1)) != -1) {
            iNumberOfQueryGaps++;
        }
        index = -1;
        while ((index = subhit.getHitSeq().indexOf('-', index + 1)) != -1) {
            iNumberOfHitGaps++;
        }

        int iQStop = subhit.getQuerySeqTo();
        int iHStop = subhit.getHitSeqTo();

        int queryDirection = 1;
        int hitDirection = 1;

        if (iQStop < iQueryStart) {
            queryDirection = -1;
        }
        if (iHStop < iHitStart) {
            hitDirection = -1;
        }

        int iQueryMultiplier = ((iQStop - iQueryStart) + queryDirection)
                / (iQueryLen - iNumberOfQueryGaps);

        int iHitMultiplier = ((iHStop - iHitStart) + hitDirection) / (iHitLen - iNumberOfHitGaps);

        int iCurrentQueryEnd = 0;
        int iCurrentHitEnd = 0;

        //
        //
        // Substring ( i*iAlignLen, (i+1)*iAlignLen )
        //
        // Increment the end number by ( (iAlign-numberofgaps)* multiplier )
        //
        // The end check should be the current end number
        //

        while (((i + 1) * iAlignLen) < iQueryLen) {

            String oCurrentQueryString = subhit.getQuerySeq().substring(i * iAlignLen,
                    (i + 1) * iAlignLen);
            String oCurrentHitString = subhit.getHitSeq().substring(i * iAlignLen,
                    (i + 1) * iAlignLen);

            iNumberOfQueryGaps = this.countNumberOfGaps(oCurrentQueryString);
            iNumberOfHitGaps = this.countNumberOfGaps(oCurrentHitString);

            iCurrentQueryEnd = iQueryStart + ((iAlignLen - iNumberOfQueryGaps) * iQueryMultiplier);
            iCurrentHitEnd = iHitStart + ((iAlignLen - iNumberOfHitGaps) * iHitMultiplier);

            drawSubAlignment(iQueryStart + "", (iCurrentQueryEnd - queryDirection) + "", iHitStart
                    + "", (iCurrentHitEnd - hitDirection) + "", oCurrentQueryString, subhit
                    .getConsensus().substring(i * iAlignLen, (i + 1) * iAlignLen),
                    oCurrentHitString);
            i++;
            iQueryStart += ((iAlignLen - iNumberOfQueryGaps) * iQueryMultiplier);
            iHitStart += ((iAlignLen - iNumberOfHitGaps) * iHitMultiplier);

        } // end while

        if (iQStop != iCurrentQueryEnd) {

            iCurrentQueryEnd = iQStop;
            iCurrentHitEnd = iHStop;

            drawSubAlignment(iQueryStart + "", iCurrentQueryEnd + "", iHitStart + "",
                    iCurrentHitEnd + "", subhit.getQuerySeq().substring(i * iAlignLen), subhit
                            .getConsensus().substring(i * iAlignLen), subhit.getHitSeq().substring(
                            i * iAlignLen));
        }

    }

    /**
     * Makes assumption about the gap character.
     * 
     */
    int countNumberOfGaps(String poString) {

        int index = -1;
        int iNumberOfGaps = 0;
        while ((index = poString.indexOf('-', index + 1)) != -1) {
            iNumberOfGaps++;
        }
        return iNumberOfGaps;
    }

    /**
     * Draws one block of the alignment.
     */
    void drawSubAlignment(String piQueryStart, String piQueryStop, String piHitStart,
            String piHitStop, String poQuery, String poConsensus, String poHit) {

        int iMax = 4;

        if (piQueryStart.length() > iMax) {
            iMax = piQueryStart.length();
        }
        if (piHitStart.length() > iMax) {
            iMax = piHitStart.length();
        }
        iMax = iMax + 2;

        String[] oFormattedSeq = new String[] { poQuery, poHit, poConsensus };
        String oConsensusPad = "       ";
        oConsensusPad = oConsensusPad.concat(this.padTo("", iMax));

        out.print("\n");
        out.print("<pre style=\"font-size: medium;\">Query: ");
        out.print(this.padTo(piQueryStart, iMax));

        out.print(oFormattedSeq[0]);
        out.print("  ");
        out.print(this.padTo(piQueryStop, iMax));

        out.print("\n");

        out.print(oConsensusPad);

        out.print(oFormattedSeq[2]);
        out.print("\n");
        out.print("Hit  : ");
        out.print(this.padTo(piHitStart, iMax));

        out.print(oFormattedSeq[1]);
        out.print("  ");
        out.print(this.padTo(piHitStop, iMax));
        out.print("</pre> ");
        out.print("\n");

    }

    private char[] padding = new char[100];
    {
        Arrays.fill(padding, 0, 100, ' ');
    }

    /**
     * Ensures the given string is the correct length.
     * 
     */
    private String padTo(String poString, int iNumberOfChars) {

        int iLen = iNumberOfChars - poString.length();

        if (iLen > padding.length) {
            padding = new char[iLen];
            Arrays.fill(padding, 0, iLen, ' ');
        }
        return poString.concat(String.copyValueOf(padding, 0, iLen));
    }

    public static void main(String[] args) throws SAXException, IOException {
        Session sess = HibernateUtil.openSession();
        sess.beginTransaction();

        Hit hit = (Hit) sess.load(Hit.class, new Long(2162845));
        Subhit subhit = hit.getSubhits().iterator().next();
        System.out.println(subhit);

        FileWriter writer = new FileWriter("/home/posu/Desktop/alignment.html");
        AlignmentsRenderer rendered = new AlignmentsRenderer(writer);
        rendered.drawCurrentAlignment(subhit);
        writer.flush();
        sess.getTransaction().commit();
        sess.close();

    }

}
