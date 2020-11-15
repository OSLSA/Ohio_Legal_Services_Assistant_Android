package org.seols.ohiolegalservicesassistant;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.Date;

public class DateCalculator {

    int numberOfDays;
    boolean excludeWeekends;
    Date startDate;
    Context context;

    public DateCalculator (int numberOfDays, boolean excludeWeekends, Date startDate, Context context) {

        this.excludeWeekends = excludeWeekends;
        this.numberOfDays = numberOfDays;
        this.startDate = startDate;
        this.context = context;

    }

    public String getNewDate() {

        String result;
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
        // String formattedDate = startDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL));
        String formattedDate = sdf.format(startDate);
        result = "" + numberOfDays + " days from " + formattedDate;

        if (excludeWeekends) {
            return result + " without counting weekends is " + datesWithoutWeekends();
        } else {
            return result + " is " + datesWithWeekends();
        }

    }

    private String datesWithWeekends() {

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.add(Calendar.DAY_OF_MONTH, numberOfDays);
        Date newDate = c.getTime();
        String formattedDate = sdf.format(newDate);
        return formattedDate;
    }

    private String datesWithoutWeekends() {
        Date result = startDate;
        int daysAdded = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
        while (daysAdded < numberOfDays) {
            Calendar c = Calendar.getInstance();
            c.setTime(result);
            c.add(Calendar.DAY_OF_MONTH, 1);
            result = c.getTime();
            if (!(result.getDay() == 0 || result.getDay() == 6)) {
                ++daysAdded;
            };

            //if (!(result.getDayOfWeek() == DayOfWeek.SATURDAY || result.getDayOfWeek() == DayOfWeek.SUNDAY)) {
            //    ++daysAdded;
            //}
        }
        String formattedDate = sdf.format(result);
        return formattedDate;
    }

}
