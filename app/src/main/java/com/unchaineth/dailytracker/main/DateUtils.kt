package com.unchaineth.dailytracker.main

import java.util.Calendar
import java.util.Date

object DateUtils {
    fun yearProgressCalculation(date: Date): Double {
        val calendar = Calendar.getInstance().apply { time = date }
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val year = calendar.get(Calendar.YEAR)

        val isLeapYear = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0
        val totalHoursInYear = if (isLeapYear) 366 * 24 else 365 * 24

        val totalHourPassed = (dayOfYear - 1) * 24 + hourOfDay

        return (totalHourPassed.toDouble() / totalHoursInYear) * 100
    }
}