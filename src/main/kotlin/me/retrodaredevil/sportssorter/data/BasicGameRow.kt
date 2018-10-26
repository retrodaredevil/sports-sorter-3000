package me.retrodaredevil.sportssorter.data

import java.time.LocalDate

data class BasicGameRow(
        override val date: LocalDate,
        override val estimatedPointSpread: Double,
        override val estimatedPointTotal: Double,
        override val actualPointsRoad: Int,
        override val actualPointsHome: Int
) : GameRow
