package me.retrodaredevil.sportssorter

import me.retrodaredevil.sportssorter.data.BasicGameRow
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.Month

class SportTest {
    @Test
    fun moneyLineTest(){
        assertEquals(.5, moneyLineToPercentage(100), 0.0)
        assertEquals(.75, moneyLineToPercentage(-300), 0.0)
        assertEquals(.25, moneyLineToPercentage(300), 0.0)

        assertEquals(100, percentageToMoneyLine(.5))
        assertEquals(-300, percentageToMoneyLine(.75))
        assertEquals(300, percentageToMoneyLine(.25))
    }
    @Test
    fun testOverPercentage(){
        val date = LocalDate.of(2018, Month.JANUARY, 1)
        val rows = listOf(
                BasicGameRow(date, -22.0, 201.0, 90, 110), // 200
                BasicGameRow(date, -5.0, 198.0, 99, 105), // 204
                BasicGameRow(date, 4.0, 205.0, 102, 99), // 201
                BasicGameRow(date, 7.0, 215.0, 115, 110) // 225
        )
        assertEquals(0.0, calculatePointTotalOverPercentage(300.0, rows), 0.0)
        assertEquals(1.0, calculatePointTotalOverPercentage(100.0, rows), 0.0)
        assertEquals(.25, calculatePointTotalOverPercentage(210.0, rows), 0.0)
        assertEquals(.5, calculatePointTotalOverPercentage(202.0, rows), 0.0)
        assertEquals(1.0, calculatePointTotalOverPercentage(199.5, rows), 0.0)
        assertEquals(1.0, calculatePointTotalOverPercentage(200.0, rows), 0.0) // because index 0 is a tie, it will still be 1.0

        assertEquals(0.0, calculatePointTotalOverPercentage(225.0, rows), 0.0)
        assertTrue(rows[0].actualPointTotal == 200)
        assertEquals(
                Double.NaN,
                calculatePointTotalOverPercentage(200.0, listOf(
                        BasicGameRow(date, -22.0, 201.0, 90, 110)
                )),
                0.0
        )

    }
}