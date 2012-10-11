package researcher.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class Utils {

    public static String removeSpaces(String s) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != ' ')
                b.append(c);
        }
        return b.toString();
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return cal.getTime();
    }

    public static void saveToDesktop(String fname, String content) {
//         try {
//         fname = "/home/posu/Desktop/" + fname;
//         java.io.FileWriter fw = new java.io.FileWriter(fname);
//         fw.append(content);
//         fw.flush();
//         fw.close();
//         } catch (IOException e) {
//            e.printStackTrace();
//         }
    }

    public static String readTextFile(String fullPathFilename) throws IOException {
        StringBuffer sb = new StringBuffer(1024);
        BufferedReader reader = new BufferedReader(new FileReader(fullPathFilename));

        char[] chars = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(chars)) > -1) {
            sb.append(String.valueOf(chars, 0, numRead));
        }
        reader.close();
        return sb.toString();
    }

    public static String htmlToText(String html) {
        html = html.replaceAll("&nbsp;", " ");
        html = html.replaceAll("&lt;", "<");
        html = html.replaceAll("&gt;", ">");
        html = html.replaceAll("&amp;", "&");
        html = html.replaceAll("&quot;", "\"");
        // &nbsp; non-breaking space
        // &lt; < less-than symbol
        // &gt; > greater-than symbol
        // &amp; & ampersand
        // &quot; " quotation mark
        return html;
    }
    
    public static void main(String[] args) {
        String s  = "fdsf as'sd'ff";
        System.out.println(escapeQuotes(s));
    }

    
    public static String escapeDashes(String cmd) {
        cmd = cmd.replaceAll("\\|", "\\\\|");
        return cmd;
    }
    
    public static String appendSlashIfNoSlash(String s){
    	if (s == null) throw new NullPointerException();
    	if (!s.endsWith("/") && !s.endsWith("\\")){
    		s+="/";
    	}
    	return s;
    }
    
    public static String escapeQuotes(String s){
    	return s.replaceAll("'", "\\\\'");
    }

}
