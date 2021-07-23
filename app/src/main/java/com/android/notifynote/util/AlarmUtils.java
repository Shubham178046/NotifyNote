package com.android.notifynote.util;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.util.SparseBooleanArray;

import androidx.core.app.ActivityCompat;

import com.android.notifynote.data.DatabaseHelper;
import com.android.notifynote.model.Alarm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static com.android.notifynote.data.DatabaseHelper.COL_DATE;
import static com.android.notifynote.data.DatabaseHelper.COL_DESCRIPTION;
import static com.android.notifynote.data.DatabaseHelper.COL_IS_ENABLED;
import static com.android.notifynote.data.DatabaseHelper.COL_LABEL;
import static com.android.notifynote.data.DatabaseHelper.COL_TIME;
import static com.android.notifynote.data.DatabaseHelper.COL_TIMES;
import static com.android.notifynote.data.DatabaseHelper._ID;

public final class AlarmUtils {

    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("h:mm", Locale.getDefault());
    private static final SimpleDateFormat AM_PM_FORMAT =
            new SimpleDateFormat("a", Locale.getDefault());

    private static final int REQUEST_ALARM = 1;
    private static final String[] PERMISSIONS_ALARM = {
            Manifest.permission.VIBRATE
    };

    private AlarmUtils() {
        throw new AssertionError();
    }

    public static void checkAlarmPermissions(Activity activity) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        final int permission = ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.VIBRATE
        );

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_ALARM,
                    REQUEST_ALARM
            );
        }

    }

    public static ContentValues toContentValues(Alarm alarm) {

        final ContentValues cv = new ContentValues(10);
        cv.put(COL_TIME, alarm.getTime());
        cv.put(COL_LABEL, alarm.getLabel());
        cv.put(COL_DESCRIPTION, alarm.getDescription());
        cv.put(COL_DATE, alarm.getDate());
        cv.put(COL_TIMES, alarm.getTimeString());
        cv.put(COL_IS_ENABLED, alarm.isEnabled());

        return cv;

    }

    public static ArrayList<Alarm> buildAlarmList(Cursor c) {

        if (c == null) return new ArrayList<>();

        final int size = c.getCount();

        final ArrayList<Alarm> alarms = new ArrayList<>(size);

        if (c.moveToFirst()) {
            do {

                final long id = c.getLong(c.getColumnIndex(_ID));
                final long time = c.getLong(c.getColumnIndex(COL_TIME));
                final String label = c.getString(c.getColumnIndex(COL_LABEL));
                final String description = c.getString(c.getColumnIndex(COL_DESCRIPTION));
                final String date = c.getString(c.getColumnIndex(COL_DATE));
                final String timeString = c.getString(c.getColumnIndex(COL_TIMES));
                final boolean isEnabled = c.getInt(c.getColumnIndex(COL_IS_ENABLED)) == 1;

                final Alarm alarm = new Alarm(id, time, label, description, date, timeString);
                alarm.setIsEnabled(isEnabled);

                alarms.add(alarm);

            } while (c.moveToNext());
        }

        return alarms;

    }

    public static String getReadableTime(long time) {
        return TIME_FORMAT.format(time);
    }

    public static String getAmPm(long time) {
        return AM_PM_FORMAT.format(time);
    }

    public static boolean isAlarmActive(Alarm alarm) {

       /* final SparseBooleanArray days = alarm.getDays();

        boolean isActive = false;
        int count = 0;

        while (count < days.size() && !isActive) {
            isActive = days.valueAt(count);
            count++;
        }

        return isActive;*/
        return true;
    }
}
