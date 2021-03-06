package com.edu.uni.augsburg.uniatron.domain.util;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * This is a helper class for date specific actions.
 *
 * @author Fabio Hellmann
 */
public final class DateUtils {

    private static final int HOUR_OF_DAY_MAX = 23;
    private static final int MINUTE_MAX = 59;
    private static final int SECOND_MAX = 59;
    private static final int MILLISECOND_MAX = 999;

    private DateUtils() {
    }

    /**
     * Get the earliest time of the specified date.
     *
     * @param date The date to get the earliest time.
     * @return The date.
     */
    @NonNull
    public static Date extractMinTimeOfDate(@NonNull final Date date) {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * Get the latest time of the specified date.
     *
     * @param date The date to get the latest time.
     * @return The date.
     */
    @NonNull
    public static Date extractMaxDate(@NonNull final Date date) {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY_MAX);
        calendar.set(Calendar.MINUTE, MINUTE_MAX);
        calendar.set(Calendar.SECOND, SECOND_MAX);
        calendar.set(Calendar.MILLISECOND, MILLISECOND_MAX);
        return calendar.getTime();
    }
}
