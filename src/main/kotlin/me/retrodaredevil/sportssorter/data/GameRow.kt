package me.retrodaredevil.sportssorter.data

import me.retrodaredevil.sportssorter.Outcome
import java.time.LocalDate

interface GameRow {
    /** The day of the game */
    val date: LocalDate
    /**
     * The estimated point spread.
     * A negative number favors the home team. A positive number favors away team
     */
    val estimatedPointSpread: Double
    /** The estimated total number of points. (The sum of each teams' score) (estimated) */
    val estimatedPointTotal: Double
    /** The actual number of points the road team scored */
    val actualPointsRoad: Int
    /** The actual number of points the home team scored */
    val actualPointsHome: Int


    /** The estimated winner or the "favored" winner*/
    val estimatedWinnerFavored: Outcome
        get(){
            val estimatedPointSpread = this.estimatedPointSpread
            return when {
                estimatedPointSpread == 0.0 -> Outcome.TIE
                estimatedPointSpread > 0 -> Outcome.AWAY
                else -> Outcome.HOME
            }
        }
    val actualPointTotal: Int
        get() = actualPointsRoad + actualPointsHome
    /** The difference between [actualPointsRoad] and [actualPointsHome]
     *
     * A positive number means away team won, negative number means home team won*/
    val actualPointDifference: Int
        get() = actualPointsRoad - actualPointsHome
    val actualPointDifferenceWinner: Outcome
        get() {
            val pointDifference = this.actualPointDifference.toDouble()
            val estimatedPointSpread = this.estimatedPointSpread
            return when {
                pointDifference == estimatedPointSpread -> Outcome.TIE
                pointDifference > estimatedPointSpread -> Outcome.AWAY // -2 > -5 : away lost by 2 instead of 5
                else -> Outcome.HOME
            }
        }
    val actualWinner: Outcome
        get(){
            val actualPointSpread = this.actualPointDifference
            return when {
                actualPointSpread == 0 -> Outcome.TIE
                actualPointSpread > 0 -> Outcome.AWAY
                else -> Outcome.HOME
            }
        }

}