package researcher.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeqUtil {

    /**
     * returns fasta representation of the sequence, no validation
     * 
     * @param sequenceName
     * @param sequence
     * @return
     */
    public static String getFasta(String sequenceName, String sequence) {
        String seq = sequenceName + "\n" + sequence;
        if (!seq.startsWith(">")) {
            seq = ">" + seq;
        }
        return seq;
    }

    private static final Pattern PROTEIN_SEQUENCE_PATTERN = Pattern
            .compile("[ARNDCQEGHILKMFPSTWYVarndcqeghilkmfpstwyv\n\r\f]*");

    /**
     * validates protein sequence
     * 
     * @param sequence
     * @return true if sequence is valid protein sequence, otherwise false
     */
    public static boolean proteinSequenceIsValid(String sequence) {
        if (sequence == null) {
            throw new NullPointerException();
        }
        if (PROTEIN_SEQUENCE_PATTERN.matcher(sequence).matches()) {
            return true;
        } else {
            Pattern p = Pattern.compile("[^ARNDCQEGHILKMFPSTWYV\n\r\f]");
            Matcher m = p.matcher(sequence);
            m.find();
            String ch = m.group();
            int c = ch.getBytes()[0];
            System.out.println("INVALID PROTEIN SEQUENCE");
            System.out.println("SEQ=" + sequence);
            System.out.println("LEN=" + ch.getBytes().length);
            System.out.println("DOES NOT MATCH='" + ch + "' = " + Integer.toHexString(c));
            return false;
        }
    }

    public static String removeEndingDashIfExists(String text) {
        if (text.endsWith("|")) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }


    
    private static final String[] prefixes = { "gi", "gnl", "pir", "prf", "sp", "pdb", "pat", "bbs", "gnl", "ref",
    "lcl", "gb", "emb", "dbj" };
    
    public static Set<String> extractFastaDescriptions(String text) {
        StringBuffer regex = new StringBuffer();
        regex.append("(?:");
        for (String prefix : prefixes) {
            regex.append(prefix);
            regex.append("|");            
        }        
        regex.deleteCharAt(regex.length() - 1);
        regex.append(")");
        regex.append("\\|\\S*");
        Pattern p = Pattern.compile(regex.toString());
        Matcher m = p.matcher(text);
        Set<String> result = new HashSet<String>();
        while (m.find()) {
            String fd = m.group(0);            
            fd = removeEndingDashIfExists(fd);
            result.add(fd);
        }
        return result;
    }
    
    public static final Pattern giPattern = Pattern.compile("([^ ]*)");
    
    public static String extractFastaId(String text) {
       if (text.startsWith(">")) text = text.substring(1);
       Matcher m = giPattern.matcher(text);
       if (m.find()) {
         return m.group(1);
       }
       return null;
    }
    
    public static final Pattern fastaidPattern = Pattern.compile("gi\\|([0-9]*)\\|");
    
    public static String extractGiNumber(String text) {
        if (text.startsWith(">")) text = text.substring(1);
        Matcher m = fastaidPattern.matcher(text);
        if (m.find()) {
          return m.group(1);
        }
        return null;
     }

    private static final Pattern seqPattern = Pattern.compile("[ARNDCQEGHILKMFPSTWYVX\\*\\-]*$");

    private static final Pattern nextLinePattern = Pattern.compile("[\n\r\f]");
    
    public static String extractSequence(String fasta) {        
        Matcher m = nextLinePattern.matcher(fasta);
        fasta = fasta.trim();
        fasta = m.replaceAll("");
        m = seqPattern.matcher(fasta);
        if (m.find())
            return m.group();
        
        else
            return null;
    }

    
    private static final Pattern fastaPattern = Pattern.compile("(.*[\n\r\f])([ARNDCQEGHILKMFPSTWYVX\\*\\-\n\r\f]+)$");
    
    public static String formatFasta(String fasta, int length){
    	if (fasta == null) throw new NullPointerException("fasta was null");
    	fasta = fasta.trim();
        Matcher m = fastaPattern.matcher(fasta);
        if (m.find()){
            StringBuffer s = new StringBuffer();
            s.append(m.group(1));
            String seq = m.group(2).replaceAll("[\n\r\f]", "");
            seq = Formatter.breakString(seq, "\n", length);
            s.append(seq);
            return s.toString();
        }
        else {
            System.err.println("unable to format fasta, returning not formatted");
            return fasta;
        }        
    }
    
    

    public static void main(String[] args) {
        String s = ">gi|3288499|emb|CAA75881.1|";        
        System.out.println(extractGiNumber(s));
    }
    
    public static final String NCBI_LINK = "http://www.ncbi.nlm.nih.gov/entrez/viewer.fcgi?db=protein&id=";

}
