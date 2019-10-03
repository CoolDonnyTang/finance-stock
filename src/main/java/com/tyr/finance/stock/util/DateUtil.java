package com.tyr.finance.stock.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {

    public static Date fomatToyyyy_MM_dd(Date d) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        return sdf.parse(sdf.format(d));
    }

    public static String fomatToyyyy_MM_ddStr(Date d) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        return sdf.format(d);
    }

    public static Date add(Date d, int model, int amount) {
        Calendar c = GregorianCalendar.getInstance();
        c.setTime(d);
        c.add(model, amount);
        return c.getTime();
    }
}
