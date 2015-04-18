package com.lazerpower.facecheck.utils;

import com.lazerpower.facecheck.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Niraj on 4/5/2015.
 */
public class TimeUtils {

    /**
     *
     * @return The closest 5 minute mark that is at least 5 minutes before from now in unix time.
     * For example if the current time was 10:32 A.M. the result would be the the unix time for 10:25 A.M.
     * 10:30 AM -> 10:25 AM
     */
    public static long getLastFullFiveMinEpoch() {
        try {
            Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
            //Set to current time
            calendar.setTime(new Date());
            int currentMin = calendar.get(Calendar.MINUTE);

            int currentMinuteOverExtend = calendar.get(Calendar.MINUTE) % 5;
            if (currentMinuteOverExtend != 0)
            {
                //Turn it into a 5 minute mark right before the current time
                //by subtracting the number of minutes over extended from the 5 minute mark
                calendar.add(Calendar.MINUTE, -currentMinuteOverExtend);
            }
            //Subtract 5 minutes to get the last full
            //five minute mark
            calendar.add(Calendar.DAY_OF_MONTH, -9);
            calendar.add(Calendar.MINUTE, -5);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            currentMin = calendar.get(Calendar.MINUTE);

            //Return the epoch time of the the 5 minute mark
            return calendar.getTime().getTime()/1000;
        }
        catch (Exception e) {
            Log.d("Could not get last five minute epoch time", e);
        }

        //By default just subtract 5 minutes from the current time
        //Might not be the right thing to do but returning any other number
        //like 0, would not accomplish any more than this.
        return (new Date().getTime()/1000) - 300;
    }

    public static String getTimeElapsedString(int numMilliseconds) {
        int timeRemaining = numMilliseconds;

        int numHours = timeRemaining/3600000;
        timeRemaining -= numHours*3600000;

        int numMins = timeRemaining/60000;
        timeRemaining -= numMins*60000;

        int numSeconds = timeRemaining/1000;

        return (numHours > 0 ? String.format("%02d:", numHours) : "")
                + String.format("%02d:", numMins)
                + String.format("%02d", numSeconds);
    }
}
