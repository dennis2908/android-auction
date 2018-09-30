package com.appschef.baseproject.util

import android.util.Log
import java.text.ParseException

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Created by Alvin Rusli on 10/7/2016.
 *
 * A helper class for date/time format.
 */
object DateTimeHelper {

    /**
     * Convert time and date to millis.
     * @param dateTime The string to parse
     * @return time in millis
     */
    @Throws(ParseException::class)
    fun convertToMillis(dateTime: String, dateFormat: String): Long {
        val formatter = SimpleDateFormat(dateFormat, Locale.US)
        return formatter.parse(dateTime).time
    }

    /**
     * Obtain a [Calendar] object from a String.
     * @return the [Calendar] object
     */
    fun getCalendarFromMillis(millis: Long): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        return calendar
    }

    /**
     * Obtain a [Calendar] object from a String.
     * @return the [Calendar] object
     */
    fun getCalendarFromDate(dateTime: String, dateFormat: String): Calendar? {
        try {
            val formatter = SimpleDateFormat(dateFormat, Locale.US)
            val date = formatter.parse(dateTime)
            val calendar = Calendar.getInstance()
            calendar.time = date
            return calendar
        } catch (e: ParseException) {
            Common.printStackTrace(e)
            return null
        }
    }
}
