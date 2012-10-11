package researcher.blast.qblast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.SAXException;

import researcher.blast.BlastTypes;
import researcher.blast.BlastTypes.AlignmentDetails;
import researcher.blast.BlastTypes.HitDetails;
import researcher.blast.BlastTypes.SubHitDetails;
import researcher.utils.SeqUtil;
import researcher.utils.Utils;


public class PsiBlastTextParser {

    // public static boolean noSimilarityFound(String text) {
    //
    // }

    public static void main(String[] args) throws IOException, SAXException {
        String text = Utils.readTextFile("/home/posu/Desktop/get_2.html");
        text = preformat(text);
        parse(text);

    }

    private static SubHitDetails readSubhit(BufferedReader br, String text) throws IOException {
        SubHitDetails subhit = new BlastTypes.SubHitDetails();
        String line = br.readLine();
        line = Utils.removeSpaces(line);
        Pattern p = Pattern.compile("Score=([^b]*)bits\\(([^\\)]*)\\),Expect=([^,$]+)");
        Matcher m = p.matcher(line);

        if (!m.find()) {
            throw new PsiBlastTextParseException("could not parse line: " + line + " nextLine="
                    + br.readLine(), text);
        }

        String s = m.group(1); // bit score
        Double bitScore = new Double(s);
        subhit.setBitScore(bitScore);

        s = m.group(2); // score
        Double score = new Double(s);
        subhit.setScore(score);

        s = m.group(3); // expect
        Double expect = new Double(s);
        subhit.setExpectValue(expect);

        line = br.readLine();
        line = Utils.removeSpaces(line);
        p = Pattern.compile("Identities=([^/]*)/([^\\(]*).*,Positives=([^/]*).*,Gaps=([^/]*)");
        m = p.matcher(line);

        if (!m.find())
            throw new PsiBlastTextParseException("could not parse line: " + line, text);

        s = m.group(1); // identities
        subhit.setNumberOfIdentities(new Integer(s));
        s = m.group(2); // alignment length
        subhit.setAlignmentSize(new Integer(s));
        s = m.group(3); // positives
        subhit.setNumberOfPositives(new Integer(s));
        s = m.group(4); // gaps
        subhit.setNumberOfGaps(new Integer(s));

        readEmptyLine(br, text);

        StringBuffer querySeq = new StringBuffer();
        StringBuffer hitSeq = new StringBuffer();
        StringBuffer consensus = new StringBuffer();

        int hitStart = -1, hitEnd = -1, queryStart = -1, queryEnd = -1;

        while (true) {
            line = br.readLine();
            if (line.trim().equals("")) {
                break;
            }
            String line1 = Utils.removeSpaces(line);
            p = Pattern.compile("(\\d+)(\\D+)(\\d+)");
            m = p.matcher(line1);
            m.find();
            if (queryStart == -1)
                queryStart = new Integer(m.group(1));
            queryEnd = new Integer(m.group(3));
            String q = m.group(2);

            querySeq.append(q);
            int ind = line.indexOf(q);

            line = br.readLine();
            line = line.substring(ind, ind + q.length());
            consensus.append(line);

            line = br.readLine();
            line = Utils.removeSpaces(line);
            p = Pattern.compile("(\\d+)(\\D+)(\\d+)");
            m = p.matcher(line);
            m.find();
            if (hitStart == -1)
                hitStart = new Integer(m.group(1));
            hitEnd = new Integer(m.group(3));
            q = m.group(2);
            hitSeq.append(q);

            readEmptyLine(br, text);
        }        
        AlignmentDetails alignment = new BlastTypes.AlignmentDetails(querySeq.toString(),
                queryStart, queryEnd, hitSeq.toString(), hitStart, hitEnd, consensus.toString());
        subhit.setAlignment(alignment);
        return subhit;
    }

    private static boolean nextLineIsAnotherSubhit(BufferedReader br) throws IOException {
        br.mark(1000);
        String line = br.readLine();
        boolean result;
        if (line.trim().equals("")) {
            result = false;
        } else if (line.trim().startsWith(">")) {
            result = false;
        } else if (line.trim().startsWith("Database:")) {
            result = false;
        } else if (line.trim().startsWith("Score")) {
            result = true;
        } else {
            result = false;
        }
        br.reset();
        return result;
    }

    private static final Pattern noSignSimPattern = Pattern
            .compile("No significant similarity found\\.");

    public static boolean noSignificantSimilarytiFound(String text) {
        Matcher m = noSignSimPattern.matcher(text);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static List<HitDetails> parse(String text) {
        try {
            List<HitDetails> hits = new ArrayList<HitDetails>();

            if (noSignificantSimilarytiFound(text)) {
                return hits;
            }

            Pattern p = Pattern.compile("Results of PSI-Blast iteration (\\d+)");
            Matcher m = p.matcher(text);
            if (!m.find())
                throw new PsiBlastTextParseException(
                        "could not find 'Results of PSI-Blast iteration'", text);

            Integer iter = new Integer(m.group(1));

            // we cut the beginning of the text
            // file that is not important to us
            int i = text.indexOf("ALIGNMENTS");
            i += 11;
            text = text.substring(i);

            BufferedReader br = new BufferedReader(new StringReader(text));
            while (true) {

                String line = br.readLine();
                if (line == null)
                    throw new PsiBlastTextParseException("unexpected end of file!", text);
                if (line.trim().startsWith("Database:"))
                    break;

                StringBuffer id = new StringBuffer();
                StringBuffer desc = new StringBuffer();
                StringBuffer len = new StringBuffer();

                if (!line.startsWith(">"))
                    throw new PsiBlastTextParseException(
                            "line does not start with > as expected, line: " + line, text);
                id.append(line.trim());
                boolean idCompleted = false;
                while (true) {
                    line = br.readLine();
                    if (line.contains("Length=")) {
                        line = line.trim();
                        len.append(line.substring(7, line.length()));
                        break;
                    }
                    if (line.startsWith(" ")) {
                        idCompleted = true;
                    }
                    if (!idCompleted) {
                        id.append(line.trim() + " ");
                    } else {
                        desc.append(line.trim() + " ");
                    }
                }

                HitDetails hit = new BlastTypes.HitDetails(id.toString(), desc.toString(), iter);
                hit.setSequenceLength(len.toString());

                hit.setAccession("");                

                readEmptyLine(br, text);

                List<SubHitDetails> subhits = new ArrayList<SubHitDetails>();
                int hspnum = 1;
                while (nextLineIsAnotherSubhit(br)) {
                    SubHitDetails subhit = readSubhit(br, text);
                    subhit.setHspNum(hspnum);
                    subhits.add(subhit);
                    hspnum++;
                    skipEmptyLines(br);                    
                }
                SubHitDetails sh = subhits.get(0);
                hit.setBitScore(sh.getBitScore());
                hit.setScore(sh.getScore());
                hit.setEValue(sh.getExpectValue());
                hit.setSubhits(subhits);
                hits.add(hit);
            }

            return hits;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static void skipEmptyLines(BufferedReader br) throws IOException {
        String line = "";
        while (line.trim().equals("")) {
            br.mark(1000);
            line = br.readLine();
        }
        br.reset();
    }

    private static void readEmptyLine(BufferedReader br, String text) throws IOException {
        String line = br.readLine();
        if (!line.trim().equals(""))
            throw new PsiBlastTextParseException("line is not empty as expected, line: " + line,
                    text);
    }

    private static String preformat(String text) {
        Pattern p = Pattern.compile("<html>.*<pre>", Pattern.DOTALL);
        text = p.matcher(text).replaceAll("");
        p = Pattern.compile("<a href=.*>&nbsp;&nbsp;", Pattern.DOTALL);
        text = p.matcher(text).replaceAll("");
        p = Pattern.compile("&gt;", Pattern.DOTALL);
        text = p.matcher(text).replaceAll(">");
        p = Pattern.compile("</pre>.*</html>", Pattern.DOTALL);
        text = p.matcher(text).replaceAll("");
        return text;
    }

    // public static void main(String[] args) throws IOException, SAXException {
    // BlastLikeSAXParser oParser = new BlastLikeSAXParser();
    // oParser.setModeLazy();
    // ArrayList oDatabaseIdList = new ArrayList();
    // ContentHandler oHandler =
    // (ContentHandler) new ParseHandler(oDatabaseIdList);
    // oParser.setContentHandler(oHandler);
    // InputStream oInputFileStream = new
    // FileInputStream("/home/posu/Desktop/blasttext.txt");
    // oParser.parse(new InputSource(oInputFileStream));
    // System.out.println("Results of parsing");
    // System.out.println("==================");
    // for (int i = 0; i < oDatabaseIdList.size();i++) {
    // System.out.println(oDatabaseIdList.get(i));
    // }
    // }

}
