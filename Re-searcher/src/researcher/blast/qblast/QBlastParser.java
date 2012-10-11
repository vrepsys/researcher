package researcher.blast.qblast;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QBlastParser {

    public static Set<String> parseGoodGis(String s) {
        s = s.toLowerCase();
        Set<String> set = new HashSet<String>();
        // <INPUT TYPE="hidden" NAME ="good_GI" VALUE = "13591878">
        String regexp = "<input[ ]+type[ ]?+=[ ]?+\"hidden\"[ ]+name[ ]?+=[ ]?+\"good_gi\"[ ]+value[ ]?+=[ ]?+\"([^\"]+)\"";
        Pattern p = Pattern.compile(regexp);
        Matcher m = p.matcher(s);
        while (m.find()) {
            set.add(m.group(1));
        }
        return set;
    }

    public static Set<String> parseCheckedGis(String s) {
        s = s.toLowerCase();
        Set<String> set = new HashSet<String>();
        // <INPUT TYPE="checkbox" NAME="checked_GI" VALUE="386994" CHECKED>
        Pattern p = Pattern
                .compile("<input[ ]+type[ ]?+=[ ]?+\"checkbox\"[ ]+name[ ]?+=[ ]?+\"checked_gi\"[ ]+value[ ]?+=[ ]?+\"([^\"]+)\"[ ]+checked");
        Matcher m = p.matcher(s);
        while (m.find()) {
            set.add(m.group(1));
        }
        return set;
    }

    /**
     * get request time of execution
     * 
     * @param html
     *            html for parsing
     * @return rtoe or null if could not parse out
     */
    public static Integer parseRtoe(String html) {
        Pattern p = Pattern.compile("name=\"RTOE\" type=\"hidden\" value=\"([^\"]+)\"");
        Matcher m = p.matcher(html);
        if (!m.find())
            return null;

        int rtoe = Integer.valueOf(m.group(1));

        return rtoe;
    }

}
