package edu.omur.nifirestapi.utility;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class DateTimeUtility {
    private static final long MILLISECONDS_IN_ONE_MINUTE = 60000;
    private static final long MILLISECONDS_IN_ONE_HOUR = MILLISECONDS_IN_ONE_MINUTE * 60;
    private static final long MILLISECONDS_IN_ONE_DAY = MILLISECONDS_IN_ONE_HOUR * 24;

    public static ZonedDateTime getCurrentTimestampAsZoneDate() {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.of("Z"));
    }

    public static String getCurrentTimestampAsZoneDateString() {
        return getCurrentTimestampAsZoneDate().toString();
    }

    public static long convertMillisecondsToMinutes(long milliSeconds) {
        return milliSeconds / MILLISECONDS_IN_ONE_MINUTE;
    }

    public static String convertMillisecondsToDayHourMinuteText(long milliSeconds) {
        long days = milliSeconds / MILLISECONDS_IN_ONE_DAY;

        milliSeconds = milliSeconds % MILLISECONDS_IN_ONE_DAY;
        long hours = milliSeconds / MILLISECONDS_IN_ONE_HOUR;

        milliSeconds = milliSeconds % MILLISECONDS_IN_ONE_HOUR;
        long minutes = milliSeconds / MILLISECONDS_IN_ONE_MINUTE;

        StringBuilder sb = new StringBuilder();
        sb.append(days + " days, ");
        sb.append(hours + " hours, ");
        sb.append(minutes + " minutes");

        return sb.toString();
    }
}
