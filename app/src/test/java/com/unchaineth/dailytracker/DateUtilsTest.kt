package com.unchaineth.dailytracker.utils

import com.unchaineth.dailytracker.main.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class DateUtilsTest {

    @Test
    fun testYearProgressCalculation_NewYear() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2024)
            set(Calendar.MONTH, Calendar.JANUARY)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val date = calendar.time

        val result = DateUtils.yearProgressCalculation(date)

        assertEquals(0.0, result, 0.01)
    }

    @Test
    fun testYearProgressCalculation_MidYear() {
        // Configurar una fecha específica: 1 de julio, 12:00
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2024)
            set(Calendar.MONTH, Calendar.JULY)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val date = calendar.time

        val result = DateUtils.yearProgressCalculation(date)

        // Compara el resultado con el valor esperado
        val expected = (182.5 * 24 / (366 * 24)) * 100
        assertEquals(expected, result, 0.01)
    }

    @Test
    fun testYearProgressCalculation_EndYear() {
        // Configurar una fecha específica: 31 de diciembre, 23:00
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2024)
            set(Calendar.MONTH, Calendar.DECEMBER)
            set(Calendar.DAY_OF_MONTH, 31)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val date = calendar.time

        val result = DateUtils.yearProgressCalculation(date)

        // Compara el resultado con el valor esperado
        val expected = (365 * 24 + 23) / (366 * 24.0) * 100
        assertEquals(expected, result, 0.01)
    }
}