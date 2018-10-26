package me.retrodaredevil.sportssorter.data

interface RowProvider{
    val initialized: Boolean
    val rows : Collection<GameRow>
    fun initialize()
}