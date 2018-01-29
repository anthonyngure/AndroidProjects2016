package ke.co.elmaxdevelopers.eventskenya.utils;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Tosh on 1/11/2016.
 */
public class DateUtils {

    /**
     * @param dateString in the format yyyy-mm-dd
     * @return
     */
    public  static String formatDateForDisplay(String dateString) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        int year = Integer.parseInt(dateString.substring(0, 4));
        int month = Integer.parseInt(dateString.substring(5, 7));
        int day = Integer.parseInt(dateString.substring(8, 10));
        cal.set(year, month-1, day);
        Date date = cal.getTime();
        return DateFormat.getDateInstance().format(date);
    }

    public  static String formatDateForDisplay(long integerDate) {
        String dateString = integerToStringDate(integerDate);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        int year = Integer.parseInt(dateString.substring(0, 4));
        int month = Integer.parseInt(dateString.substring(5, 7));
        int day = Integer.parseInt(dateString.substring(8, 10));
        cal.set(year, month-1, day);
        Date date = cal.getTime();
        return DateFormat.getDateInstance().format(date); // FEB 2, 2016
    }
    /**
     * @param dateString in the format yyyy-mm-dd
     * @return
     */
    public  static long getIntegerDate(String dateString) {
        return ((long) Integer.parseInt((dateString.toString().replace("-",""))));
    }

    public  static String integerToStringDate(long integerDate) {
        String year = String.valueOf(integerDate).substring(0, 4);
        String month = String.valueOf(integerDate).substring(4, 6);
        String day = String.valueOf(integerDate).substring(6, 8);
        String result = year+"-"+month+"-"+day;
        return String.valueOf(result);
    }


    public static String sortDates(String startDate, String endDate){
        if (startDate.equalsIgnoreCase(endDate)){
            return formatDateForDisplay(startDate);
        } else {
            return formatDateForDisplay(startDate)+" to "+ formatDateForDisplay(endDate);
        }
    }


    public static String formatTime(int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        String am_pm = "";
        if (c.get(Calendar.AM_PM) == Calendar.AM){
            am_pm = "AM";
        } else if (c.get(Calendar.AM_PM) == Calendar.PM){
            am_pm = "PM";
        }

        String hrsToShow = (c.get(Calendar.HOUR) == 0) ? "12" : c.get(Calendar.HOUR)+"";
        return hrsToShow+":"+c.get(Calendar.MINUTE)+" "+am_pm;
        //return (str.substring(0, str.length()-6)+" "+str.substring(str.length()-2, str.length()));
    }
}
