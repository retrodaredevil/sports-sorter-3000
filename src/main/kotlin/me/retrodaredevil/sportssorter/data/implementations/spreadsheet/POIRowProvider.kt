package me.retrodaredevil.sportssorter.data.implementations.spreadsheet

import me.retrodaredevil.sportssorter.data.BasicGameRow
import me.retrodaredevil.sportssorter.data.GameRow
import me.retrodaredevil.sportssorter.data.RowProvider
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.time.ZoneId

val UTC_ZONE = ZoneId.of("UTC") ?: throw NullPointerException("Unable to find UTC Zone")

class POIRowProvider(
        private val stream: InputStream
) : RowProvider{
    constructor(file: File) : this(FileInputStream(file))
    constructor(filePath: String) : this(File(filePath))

    override var initialized: Boolean = false
    override lateinit var rows: Collection<GameRow>

    override fun initialize() {
        initialized = true
        val workbook = XSSFWorkbook(stream)
        val sheet = workbook.getSheetAt(0)

        val indices = DataIndices()
        val rows = mutableListOf<GameRow>()
        var checkSumErrors = 0
        for((i, row) in sheet.withIndex()) {
            if (i == 0) continue
            val localDate = row.getCell(indices.dateIndex).dateCellValue.toInstant().atZone(UTC_ZONE).toLocalDate()

            val estimatedPointSpread = row.getCell(indices.estimatedPointSpreadIndex).numericCellValue
            val estimatedPointTotal = row.getCell(indices.estimatedPointTotalIndex).numericCellValue

            val actualPointsRoad = row.getCell(indices.actualPointsRoadIndex).numericCellValue.toInt()
            val actualPointsHome = row.getCell(indices.actualPointsHomeIndex).numericCellValue.toInt()

            val actualPointTotalIndex = indices.actualPointTotalIndex
            if (actualPointTotalIndex != null) {
                val actualPointsTotal = row.getCell(actualPointTotalIndex).numericCellValue.toInt()
                if (actualPointsTotal != actualPointsRoad + actualPointsHome) {
                    println("Checksum error! index: $i road: $actualPointsRoad home: $actualPointsHome total: $actualPointsTotal date: $localDate")
                    checkSumErrors++
                    continue
                }
            }

            val gameRow = BasicGameRow(localDate, estimatedPointSpread, estimatedPointTotal, actualPointsRoad, actualPointsHome)
            rows.add(gameRow)
        }
        this.rows = rows
    }
}