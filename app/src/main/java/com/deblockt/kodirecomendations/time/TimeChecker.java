package com.deblockt.kodirecomendations.time;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by thomas on 10/04/16.
 */
public class TimeChecker {

    public static final String TAG = "TimeChecker";
    public static Date lastDate = null;

    public static void check(String action) {
        if (lastDate == null) {
            Log.d(TAG, "Initialisation : " + action);
        }

        Date currentDate = new Date();

        if (lastDate != null) {
            Log.d(TAG, action + computeDiff(currentDate, lastDate));
        }

        lastDate = currentDate;
    }

    private static Map<TimeUnit,Long> computeDiff(Date date1, Date date2) {
        long diffInMillies = date2.getTime() - date1.getTime();
        List<TimeUnit> units = new ArrayList<TimeUnit>(EnumSet.allOf(TimeUnit.class));
        Collections.reverse(units);
        Map<TimeUnit,Long> result = new LinkedHashMap<TimeUnit,Long>();
        long milliesRest = diffInMillies;
        for ( TimeUnit unit : units ) {
            long diff = unit.convert(milliesRest,TimeUnit.MILLISECONDS);
            long diffInMilliesForUnit = unit.toMillis(diff);
            milliesRest = milliesRest - diffInMilliesForUnit;
            result.put(unit,diff);
        }
        return result;
    }
}
