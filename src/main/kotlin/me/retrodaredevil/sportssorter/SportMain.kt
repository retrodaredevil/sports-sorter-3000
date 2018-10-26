package me.retrodaredevil.sportssorter

import me.retrodaredevil.sportssorter.data.GameRow
import me.retrodaredevil.sportssorter.data.RowProvider
import me.retrodaredevil.sportssorter.data.implementations.spreadsheet.POIRowProvider
import java.lang.Math.abs
import java.util.*
import kotlin.math.roundToInt


fun main(args: Array<String>){
    val filePath = args[0]
    val inputPointTotal = args[1].toDouble()
    val inputPointTotalMoneyLine = args[2].toInt() // for total
    val inputPointSpread = args[3].toDouble()
    val inputPointSpreadMoneyLine = args[4].toInt()
    println("filePath: $filePath\tpoint total: $inputPointTotal\tpoint total money line: $inputPointTotalMoneyLine" +
            "\tpoint spread: $inputPointSpread\tpoint spread money line: $inputPointSpreadMoneyLine")

    val desiredPointTotalOverPercentage = moneyLineToPercentage(inputPointTotalMoneyLine)
    println("desired over/total $desiredPointTotalOverPercentage")
    val desiredHomeTeamPointSpreadWinPercentage = moneyLineToPercentage(inputPointSpreadMoneyLine)
    println("desired home team point spread win percentage: $desiredHomeTeamPointSpreadWinPercentage")
    println()

    val provider: RowProvider = POIRowProvider(filePath)
    if(!provider.initialized){
        println("initializing rows")
        provider.initialize()
        println("done initializing rows")
    }
    val rows = ArrayList<GameRow>(provider.rows)
    println("rows.size: ${rows.size}")
    println("actual over/total: ${calculatePointTotalOverPercentage(inputPointTotal, rows)}")
    println()

    val rowsByOverPercentage = createCollectionWithDesiredPointTotalOverPercentage(inputPointTotal, desiredPointTotalOverPercentage, rows)
    println("size after first chop: ${rowsByOverPercentage.size}")
    println("over/total of new data set: ${calculatePointTotalOverPercentage(inputPointTotal, rowsByOverPercentage)}")
    println()

    val rowsByPointDifference = createCollectionWithDesiredPointDifferencePercentage(inputPointSpread, desiredHomeTeamPointSpreadWinPercentage, rowsByOverPercentage)
    println("size after second chop: ${rowsByPointDifference.size}")
    println("over/total of new data set: ${calculatePointTotalOverPercentage(inputPointTotal, rowsByOverPercentage)}")
    println("home team point spread win percentage of new data set: ${calculateHomeTeamPointSpreadWinPercentage(inputPointSpread, rowsByPointDifference)}")

    var homeWins = 0
    var awayWins = 0
    for(row in rowsByPointDifference){
        when (row.actualWinner){
            Outcome.HOME -> homeWins++
            Outcome.AWAY -> awayWins++
            else -> {}
        }
    }
    val homeTeamWinPercentage = homeWins.toDouble() / (homeWins + awayWins)
    println("home team win percentage: $homeTeamWinPercentage")
    println("money line: ${percentageToMoneyLine(homeTeamWinPercentage)}")
}

/**
 * @returns The percentage of games (excluding ties entirely) where the actualPointTotal > inputPointTotal
 */
fun calculatePointTotalOverPercentage(inputPointTotal: Double, rows: Collection<GameRow>): Double{
    var over = 0
    var under = 0
    for(row in rows){
        val actualPointTotal = row.actualPointTotal
        if(actualPointTotal > inputPointTotal){
            over++
        } else if(actualPointTotal < inputPointTotal){
            under++
        }
    }
    return over.toDouble() / (over + under)
}
fun calculateHomeTeamPointSpreadWinPercentage(inputPointSpread: Double, rows: Collection<GameRow>) : Double {
    var home = 0
    var away = 0
    for(row in rows){
        val pointDifference = row.actualPointDifference
        if(pointDifference < inputPointSpread){ // when the home team wins by more points
            home++
        } else if(pointDifference > inputPointSpread){
            away++
        }
    }
    return home.toDouble() / (home + away)
}
fun moneyLineToPercentage(moneyLine: Int): Double{
    val isPositive = moneyLine > 0
    val absMoneyLine = abs(moneyLine)
    var r = absMoneyLine.toDouble() / (absMoneyLine + 100)
    if(isPositive){
        r = 1.0 - r
    }
    return r
}
fun percentageToMoneyLine(percentage: Double): Int = when {
    // thanks https://sportsprofit.wordpress.com/2010/06/29/how-to-convert-a-money-line-into-a-percentage-and-vice-versa/
    percentage > .5 -> (percentage / (1.0 - percentage) * -100.0).roundToInt()
    else -> (((1.0 - percentage) / percentage) * 100.0).roundToInt()
}
fun createCollectionWithDesiredPointTotalOverPercentage(inputPointTotal: Double, desiredPointTotalOverPercentage: Double,
                                                        rows: Collection<GameRow>): Collection<GameRow> {
    val r = ArrayList<GameRow>(rows)
    r.sortBy { element -> element.estimatedPointTotal } // lowest point totals first

    // true if we need the percentage to go down
    val removeLastRows = desiredPointTotalOverPercentage < calculatePointTotalOverPercentage(inputPointTotal, r)
    while(true){
        if(removeLastRows){ // by removing the last rows, we decrease the percentage
            r.removeAt(r.size - 1)
            if(calculatePointTotalOverPercentage(inputPointTotal, r) <= desiredPointTotalOverPercentage) break // stop if actual is under
        } else { // by removing the first rows, we increase the percentage
            r.removeAt(0)
            if(calculatePointTotalOverPercentage(inputPointTotal, r) >= desiredPointTotalOverPercentage) break // stop if actual is over
        }
    }
    return r
}
fun createCollectionWithDesiredPointDifferencePercentage(inputPointSpread: Double, desiredHomeTeamPointSpreadWinPercentage: Double,
                                                         rows: Collection<GameRow>): Collection<GameRow> {
    val r = ArrayList<GameRow>(rows)
    r.sortBy { element -> element.estimatedPointSpread } // the first rows are when the home team is expected to win

    // true if we want the home team point spread win percentage to go up
    val removeLastRows = desiredHomeTeamPointSpreadWinPercentage > calculateHomeTeamPointSpreadWinPercentage(inputPointSpread, rows) // desired less than current
    while(true){
        if(removeLastRows){ // removing last rows removes away team wins - percentage goes up
            r.removeAt(r.size - 1)
            if(calculateHomeTeamPointSpreadWinPercentage(inputPointSpread, r) >= desiredHomeTeamPointSpreadWinPercentage) break // stop if actual is over
        } else { // removing first rows removes home team wins - percentage goes down
            r.removeAt(0)
            if(calculateHomeTeamPointSpreadWinPercentage(inputPointSpread, r) <= desiredHomeTeamPointSpreadWinPercentage) break // stop if actual is under
        }
    }
    return r
}
