package cn.wolfcode.business.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateUtils {
    //将日期字符串转换为Date类型
    public static Date parseDate(String strDate, String regex) {
        if (strDate != null && regex != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(regex);
            try {
                return sdf.parse(strDate);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return null;


    }

    //将传递时间+1天 -1秒,变为当天的23:59:59
    public static Date getEndTime(Date date) {
        if (date == null) {
            return null;
        }
        //日期处理
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //+1天
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        //-1秒
        calendar.add(Calendar.SECOND, -1);
        return calendar.getTime();
    }


    //将Date 类型转换为字符串类型
    public static String formatDate(Date date, String regex) {
        if (date == null || regex == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(regex);
        return sdf.format(date);

    }
}
