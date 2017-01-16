package org.loofer.weread.app.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * ============================================================
 * 版权： xx有限公司 版权所有（c）2016
 * <p>
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2017/1/2 21:43.
 * 描述：
 * <p>
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 * <p>
 * ==========================================================
 */

public class TimeUtils {

    public static long getCurrentSeconds() {
        long ls = System.currentTimeMillis() / 1000;
        return ls;
    }

    public static String[] getCalendarShowTime(long paramLong) {
        String[] localObject;
        String str = new SimpleDateFormat("yyyy:MMM:d", Locale.ENGLISH).format(new Date(paramLong));
        try {
            String[] arrayOfString = str.split(":");
            localObject = arrayOfString;
            if ((localObject != null) && (localObject.length == 3)) ;
            return localObject;
        } catch (Exception localException) {
            while (true)
                localException.printStackTrace();
        }
    }

    public static String[] getCalendarShowTime(String paramString) {
        try {
            long l = Long.valueOf(paramString);
            Calendar localCalendar = Calendar.getInstance();
            localCalendar.setTimeInMillis(1000L * l);
            return getCalendarShowTime(localCalendar.getTimeInMillis());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDate(String formate) {
        String str = new SimpleDateFormat(formate, Locale.ENGLISH).format(new Date());
        return str;
    }

    /**
     * Parse the time in milliseconds into String with the format: hh:mm:ss or mm:ss
     *
     * @param duration The time needs to be parsed.
     */
    @SuppressLint("DefaultLocale")
    public static String formatDuration(int duration) {
        duration /= 1000; // milliseconds into seconds
        int minute = duration / 60;
        int hour = minute / 60;
        minute %= 60;
        int second = duration % 60;
        if (hour != 0)
            return String.format("%2d:%02d:%02d", hour, minute, second);
        else
            return String.format("%02d:%02d", minute, second);
    }


}
