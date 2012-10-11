package researcher.utils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Formatter {

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static String formatDate(Date date) {
        if (date == null)
            return null;
        return dateFormat.format(date);
    }

    public static Format getDateFormat() {
        return dateFormat;
    }

    public static String shortenTheSequence(String sequence) {
        if (sequence.length() > 77) {
            sequence = sequence.substring(0, 77) + "...";
        }
        return sequence;
    }

    public static String breakString(String sequence, String breakSymbol, int interval) {
        if (sequence == null)
            return null;
        StringBuffer result = new StringBuffer();
        int len = sequence.length();
        int from = -interval;
        int to = 0;
        do {
            from += interval;
            to += interval;
            if (to > len)
                to = len;
            result.append(sequence.substring(from, to));
            if (to != len) {
                result.append(breakSymbol);
            }
        } while (to < len);
        return result.toString();
    }

    public static Format getPercentFormat() {
        DecimalFormat f = new DecimalFormat("###%");
        return f;
    }

    public static Format getEvalueFormat() {
        DecimalFormat f = new DecimalFormat("#.##E0");
        return f;
    }

}
