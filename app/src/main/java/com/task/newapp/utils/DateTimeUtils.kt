package com.task.newapp.utils

import android.text.TextUtils
import android.text.format.DateFormat
import android.text.format.Time
import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DateTimeUtils private constructor() {
    /**
     * enum - date & time formats
     */
    enum class DateFormats(val label: String) {
        dd("dd"),
        MM("MM"),
        MMM("MMM"),
        MMMM("MMMM"),
        yy("yy"),
        yyyy("yyyy"),
        HH("HH"),
        mm("mm"),
        ss("ss"),
        EEEE("EEEE"),
        HHmmsssss("HH:mm:ss.SSS'Z'"),
        HHmmss("HH:mm:ss"),
        hmm("h:mm"),
        hmma("h:mm a"),
        a("a"),
        yyyyMMdd("yyyy-MM-dd"),
        MMMMddyyyy("MMMM dd, yyyy"),
        MMMdyyyy("MMM d yyyy"),
        MMMdyyyyComma("MMM d, yyyy"),
        MMMd("MMM d"),
        hmmaMMMdyyyy(hmma.label + ", " + MMMdyyyy.label),
        yyyyMMddhhmma(yyyyMMdd.label + " " + hmma.label),
        yyyyMMddHHmmss(yyyyMMdd.label + " " + HHmmss.label),
        EEEEMMMdyyyyhhmma(EEEE.label + ", " + MMMdyyyy.label + " " + hmma.label),
        yyyyMMddTHHmmsssss(yyyyMMdd.label + "'T'" + HHmmsssss.label);

    }

    /**
     * convert date&time to UTC
     *
     * @param sourceString
     * @param sourceDateFormat
     * @param targetDateFormat
     * @param timeZone
     * @return
     */
    fun formatDateTimeToUTC(
        sourceString: String?,
        sourceDateFormat: String?,
        targetDateFormat: String?,
        timeZone: String?
    ): String {
        return if (TextUtils.isEmpty(sourceString) || TextUtils.isEmpty(sourceDateFormat) || TextUtils.isEmpty(
                targetDateFormat
            ) || TextUtils.isEmpty(timeZone)
        ) "" else try {
            val sourceFormat =
                SimpleDateFormat(sourceDateFormat, Locale.US)
            sourceFormat.timeZone = TimeZone.getTimeZone(timeZone)
            val date = sourceFormat.parse(sourceString)
            val targetFormat =
                SimpleDateFormat(targetDateFormat, Locale.US)
            targetFormat.timeZone =
                TimeZone.getTimeZone(Time.TIMEZONE_UTC)
            targetFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * convert date&time to UTC
     *
     * @param sourceString
     * @param sourceDateFormat
     * @param targetDateFormat
     * @return
     */
    fun formatDateTimeToUTC(
        sourceString: String?,
        sourceDateFormat: String?,
        targetDateFormat: String?
    ): String {
        return if (TextUtils.isEmpty(sourceString) || TextUtils.isEmpty(sourceDateFormat) || TextUtils.isEmpty(
                targetDateFormat
            )
        ) "" else try {
            val sourceFormat =
                SimpleDateFormat(sourceDateFormat, Locale.US)
            sourceFormat.timeZone = TimeZone.getDefault()
            val date = sourceFormat.parse(sourceString)
            val targetFormat =
                SimpleDateFormat(targetDateFormat, Locale.US)
            targetFormat.timeZone =
                TimeZone.getTimeZone(Time.TIMEZONE_UTC)
            targetFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * convert date&time to UTC
     *
     * @param sourceDate
     * @param targetDateFormat
     * @return
     */
    fun formatDateTimeToUTC(sourceDate: Date?, targetDateFormat: String?): String {
        return if (TextUtils.isEmpty(targetDateFormat)) "" else try {
            val targetFormat =
                SimpleDateFormat(targetDateFormat, Locale.US)
            targetFormat.timeZone =
                TimeZone.getTimeZone(Time.TIMEZONE_UTC)
            targetFormat.format(sourceDate)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * convert UTC into date
     *
     * @param sourceString
     * @param sourceDateFormat
     * @param targetDateFormat
     * @param timeZone
     * @return
     */
    fun formatUTCToDateTime(
        sourceString: String?,
        sourceDateFormat: String?,
        targetDateFormat: String?,
        timeZone: String?
    ): String {
        return if (TextUtils.isEmpty(sourceString) || TextUtils.isEmpty(sourceDateFormat) || TextUtils.isEmpty(
                targetDateFormat
            ) || TextUtils.isEmpty(timeZone)
        ) "" else try {
            val sourceFormat =
                SimpleDateFormat(sourceDateFormat, Locale.US)
            sourceFormat.timeZone =
                TimeZone.getTimeZone(Time.TIMEZONE_UTC)
            val date = sourceFormat.parse(sourceString)
            val targetFormat =
                SimpleDateFormat(targetDateFormat, Locale.US)
            targetFormat.timeZone = TimeZone.getTimeZone(timeZone)
            targetFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * convert UTC into date
     *
     * @param sourceString
     * @param sourceDateFormat
     * @param targetDateFormat
     * @return
     */
    fun formatUTCToDateTime(
        sourceString: String?,
        sourceDateFormat: String?,
        targetDateFormat: String?
    ): String {
        return if (TextUtils.isEmpty(sourceString) || TextUtils.isEmpty(sourceDateFormat) || TextUtils.isEmpty(
                targetDateFormat
            )
        ) "" else try {
            val sourceFormat =
                SimpleDateFormat(sourceDateFormat, Locale.US)
            sourceFormat.timeZone =
                TimeZone.getTimeZone(Time.TIMEZONE_UTC)
            val date = sourceFormat.parse(sourceString)
            val targetFormat =
                SimpleDateFormat(targetDateFormat, Locale.US)
            targetFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * convert string into particular date&time format
     *
     * @param sourceString
     * @param sourceDateFormat
     * @param targetDateFormat
     * @return
     */
    fun formatDateTime(
        sourceString: String?,
        sourceDateFormat: String?,
        targetDateFormat: String?
    ): String {
        if (TextUtils.isEmpty(sourceString) || TextUtils.isEmpty(sourceDateFormat) || TextUtils.isEmpty(
                targetDateFormat
            )
        ) {
            return ""
        }
        val sourceFormat =
            SimpleDateFormat(sourceDateFormat, Locale.US)
        var date: Date? = null
        try {
            date = sourceFormat.parse(sourceString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val targetFormat =
            SimpleDateFormat(targetDateFormat, Locale.US)
        return targetFormat.format(date)
    }

    /**
     * convert date into particular date&time format
     *
     * @param sourceDate
     * @param targetDateFormat
     * @return
     */
    fun formatDateTime(
        sourceDate: Date?,
        targetDateFormat: String?
    ): String {
        if (sourceDate == null || TextUtils.isEmpty(targetDateFormat)) {
            return ""
        }
        val targetFormat =
            SimpleDateFormat(targetDateFormat, Locale.US)
        return targetFormat.format(sourceDate)
    }

    /**
     * convert string into date
     *
     * @param sourceString
     * @param sourceDateFormat
     * @return
     */
    fun formatDateTime(sourceString: String?, sourceDateFormat: String?): Date? {
        if (TextUtils.isEmpty(sourceString) || TextUtils.isEmpty(sourceDateFormat)) {
            return null
        }
        val targetFormat = SimpleDateFormat(sourceDateFormat, Locale.US)
        var targetDate: Date? = null
        try {
            targetDate = targetFormat.parse(sourceString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return targetDate
    }

    /**
     * check datetime string is in given format
     *
     * @param sourceString
     * @param sourceDateFormat
     * @return
     */
    fun validFormat(sourceString: String?, sourceDateFormat: String?): Boolean {
        if (TextUtils.isEmpty(sourceString) || TextUtils.isEmpty(sourceDateFormat)) {
            return false
        }
        val targetFormat = SimpleDateFormat(sourceDateFormat, Locale.US)
        try {
            targetFormat.parse(sourceString)
        } catch (e: ParseException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    /**
     * get time difference into minutes
     *
     * @param sourceDate
     * @param targetDate
     * @return
     */
    fun timeDifferenceInMinutes(sourceDate: Date, targetDate: Date): Long {
        val difference = sourceDate.time - targetDate.time
        Log.e(TAG, "Time difference " + TimeUnit.MILLISECONDS.toMinutes(difference))
        return TimeUnit.MILLISECONDS.toMinutes(difference)
    }

    /**
     * get time difference into seconds
     *
     * @param sourceDate
     * @param targetDate
     * @return
     */
    fun timeDifferenceInSeconds(sourceDate: Date, targetDate: Date): Long {
        val difference = sourceDate.time - targetDate.time
        Log.e(TAG, "Time difference " + TimeUnit.MILLISECONDS.toSeconds(difference))
        return TimeUnit.MILLISECONDS.toSeconds(difference)
    }

    /**
     * get time difference into hours
     *
     * @param sourceDate
     * @param targetDate
     * @return
     */
    fun timeDifferenceInHours(sourceDate: Date, targetDate: Date): Long {
        val difference = sourceDate.time - targetDate.time
        Log.e(TAG, "Time difference " + TimeUnit.MILLISECONDS.toHours(difference))
        return TimeUnit.MILLISECONDS.toHours(difference)
    }

    /**
     * get time difference into days
     *
     * @param sourceDate
     * @param targetDate
     * @return
     */
    fun timeDifferenceInDays(sourceDate: Date, targetDate: Date): Long {
        val difference = sourceDate.time - targetDate.time
        Log.e(TAG, "Time difference " + TimeUnit.MILLISECONDS.toDays(difference))
        return TimeUnit.MILLISECONDS.toDays(difference)
    }

    /**
     * check given date to today
     *
     * @param sourceDate
     * @return
     */
    fun isToday(sourceDate: Date?): Boolean {
        if (sourceDate == null) {
            return false
        }
        val calendar = Calendar.getInstance()
        calendar.time = sourceDate
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        val todayCalendar = Calendar.getInstance()
        todayCalendar[Calendar.HOUR_OF_DAY] = 0
        todayCalendar[Calendar.MINUTE] = 0
        todayCalendar[Calendar.SECOND] = 0
        todayCalendar[Calendar.MILLISECOND] = 0
        return if (calendar.time == todayCalendar.time) {
            true
        } else {
            false
        }
    }

    /**
     * check given date to today
     *
     * @param sourceDate
     * @return
     */
    fun isTomorrow(sourceDate: Date?): Boolean {
        if (sourceDate == null) {
            return false
        }
        val calendar = Calendar.getInstance()
        calendar.time = sourceDate
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        val tomorrowCalendar = Calendar.getInstance()
        tomorrowCalendar.add(Calendar.DAY_OF_YEAR, 1)
        tomorrowCalendar[Calendar.HOUR_OF_DAY] = 0
        tomorrowCalendar[Calendar.MINUTE] = 0
        tomorrowCalendar[Calendar.SECOND] = 0
        tomorrowCalendar[Calendar.MILLISECOND] = 0
        return if (calendar.time == tomorrowCalendar.time) {
            true
        } else {
            false
        }
    }

    /**
     * check given date's year is current year
     *
     * @param date
     * @return
     */
    fun isCurrentYear(date: String?, sourceDateFormat: String?): Boolean {
        if (date == null) {
            return false
        }
        val sourceDate = formatDateTime(date, sourceDateFormat)
        val calendar = Calendar.getInstance()
        calendar.time = sourceDate
        val todayCalendar = Calendar.getInstance()
        return if (calendar[Calendar.YEAR] == todayCalendar[Calendar.YEAR]) {
            true
        } else {
            false
        }
    }

    /**
     * get day from date
     *
     * @param sourceDate
     * @return
     */
    fun getDay(sourceDate: Date?): String {
        return DateFormat.format(DateFormats.dd.label, sourceDate) as String
    }

    /**
     * get month from date
     *
     * @param sourceDate
     * @return
     */
    fun getMonth(sourceDate: Date?): String {
        return DateFormat.format(DateFormats.MM.label, sourceDate) as String
    }

    /**
     * get year from date
     *
     * @param sourceDate
     * @return
     */
    fun getYear(sourceDate: Date?): String {
        return DateFormat.format(DateFormats.yyyy.label, sourceDate) as String
    }

    /**
     * get year from date
     *
     * @param sourceDate
     * @return
     */
    fun getYearinTwoDigit(sourceDate: Date?): String {
        return DateFormat.format(DateFormats.yy.label, sourceDate) as String
    }

    /**
     * get weekday from date
     *
     * @param sourceDate
     * @return
     */
    fun getWeekDay(sourceDate: Date?): String {
        return DateFormat.format(DateFormats.EEEE.label, sourceDate) as String
    }

    /**
     * get time from date
     *
     * @param sourceDate
     * @return
     */
    fun getTime(sourceDate: Date?): String {
        return DateFormat.format(DateFormats.hmma.label, sourceDate) as String
    }

    /**
     * get hour from date
     *
     * @param sourceDate
     * @return
     */
    fun getHour(sourceDate: Date?): String {
        return DateFormat.format(DateFormats.HH.label, sourceDate) as String
    }

    /**
     * get minute from date
     *
     * @param sourceDate
     * @return
     */
    fun getMinute(sourceDate: Date?): String {
        return DateFormat.format(DateFormats.mm.label, sourceDate) as String
    }

    /**
     * get second from date
     *
     * @param sourceDate
     * @return
     */
    fun getSecond(sourceDate: Date?): String {
        return DateFormat.format(DateFormats.ss.label, sourceDate) as String
    }

    /**
     * get minute - with in 15 minute time slot
     *
     * @param minute
     * @return
     */
    fun getRoundedMinute(minute: Int): Int {
        return if (minute >= 0 && minute < 15) {
            15
        } else if (minute >= 15 && minute < 30) {
            30
        } else if (minute >= 30 && minute < 45) {
            45
        } else {
            0
        }
    }

    /**
     * get timestamp with particular timezone
     *
     * @param sourceString
     * @param sourceDateFormat
     * @return
     */
    fun getTimeStampWithTimezone(
        sourceString: String,
        sourceDateFormat: String?
    ): String {
        if (TextUtils.isEmpty(sourceString) || TextUtils.isEmpty(sourceDateFormat)) {
            return ""
        }
        val pickUpDateTime =
            formatDateTime(sourceString.trim { it <= ' ' }, sourceDateFormat)
        return "/Date(" + pickUpDateTime!!.time + "+0530" + ")/"
    }

    companion object {
        private const val TAG = "DateTimeUtils"
        var instance: DateTimeUtils? = null
            get() {
                if (field == null) {
                    field = DateTimeUtils()
                }
                return field
            }
            private set
    }
}