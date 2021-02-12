package de.minicraft;

import java.util.concurrent.TimeUnit;

public class Utils {
    public static String convertTimestamp(long value1, long value2) {
        long min = Math.min(value1, value2),
                max = Math.max(value1, value2),
                result = max % min;

        if (result < 60000) return TimeUnit.MILLISECONDS.toSeconds(result) + " Sekunde(n)";
        else if (result < 3600000) return TimeUnit.MILLISECONDS.toMinutes(result) + " Minute(n)";
        else if (result < 86400000) return TimeUnit.MILLISECONDS.toHours(result) + " Stunde(n)";

        return TimeUnit.MILLISECONDS.toDays(result) + " Tag(e)";
    }
}
