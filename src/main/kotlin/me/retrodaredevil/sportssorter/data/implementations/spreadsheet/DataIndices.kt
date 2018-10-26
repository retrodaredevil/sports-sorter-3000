package me.retrodaredevil.sportssorter.data.implementations.spreadsheet

data class DataIndices(
        val dateIndex: Int=0,
        val estimatedPointSpreadIndex: Int=3,
        val estimatedPointTotalIndex: Int=4,
        val actualPointsRoadIndex: Int=5,
        val actualPointsHomeIndex: Int=6,
        val actualPointTotalIndex: Int?=10
)