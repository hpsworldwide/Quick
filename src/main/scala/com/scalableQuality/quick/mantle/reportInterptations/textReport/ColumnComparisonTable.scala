package com.scalableQuality.quick.mantle.reportInterptations.textReport

import com.scalableQuality.quick.core.Reporting.InvalidColumns.{BothColumnsFailedChecks, LeftColumnFailedChecks, RightColumnFailedChecks}
import com.scalableQuality.quick.core.Reporting._

import scala.annotation.tailrec

object ColumnComparisonTable {

  def apply(
      differenceBetweenMatchedRows: DifferenceBetweenMatchedRows
  ): List[String] = {
    val tableHeaderRowComponent = ColumnComparisonTableRow(
      "Label",
      "Position",
      differenceBetweenMatchedRows.metaData.leftFileLabel,
      differenceBetweenMatchedRows.metaData.rightFileLabel
    )
    val tableHeaderRow = normalColumnsRowFormatter(tableHeaderRowComponent)

    val columnsComparisonRows = interpretColumnComparisons(
      differenceBetweenMatchedRows.columnComparisons,
      dataRowSizes,
      invalidColumnsRowFormatter,
      reportingColumnsRowFormatter,
      bothColumnsFailedChecksColumnsRowFormatter,
      leftColumnFailedChecksRowFormatter,
      rightColumnFailedChecksRowFormatter,
      softHorizontalDivider
    )
    strongHorizontalDivider :: tableHeaderRow :: strongHorizontalDivider :: columnsComparisonRows ::: strongHorizontalDivider :: invisibleHorizontalDivider :: Nil
  }

  private val dataRowSizes = ColumnComparisonTableColumnSizes(15, 10, 30)

  private val invalidColumnsRowBorders =
    ColumnComparisonTableColumnsBorders("# ", " | ", " |")
  private val botchColumnsFailedChecksRowBorders =
    ColumnComparisonTableColumnsBorders("~ ", " | ", " ~ ", " ~ ", " |")
  private val leftColumnFailedChecksRowBorders =
    ColumnComparisonTableColumnsBorders("~ ", " | ", " ~ ", " | ", " |")
  private val rightColumnFailedChecksRowBorders =
    ColumnComparisonTableColumnsBorders("~ ", " | ", " | ", " ~ ", " |")
  private val reportingColumnsRowBorders =
    ColumnComparisonTableColumnsBorders("! ", " | ", " |")
  private val normalRowBorders =
    ColumnComparisonTableColumnsBorders("| ", " | ", " |")

  private val bothColumnsFailedChecksColumnsRowFormatter =
    ColumnComparisonTableRowFormat(
      botchColumnsFailedChecksRowBorders,
      dataRowSizes
    )

  private val leftColumnFailedChecksRowFormatter =
    ColumnComparisonTableRowFormat(
      leftColumnFailedChecksRowBorders,
      dataRowSizes
    )

  private val rightColumnFailedChecksRowFormatter =
    ColumnComparisonTableRowFormat(
      rightColumnFailedChecksRowBorders,
      dataRowSizes
    )

  private val invalidColumnsRowFormatter = ColumnComparisonTableRowFormat(
    invalidColumnsRowBorders,
    dataRowSizes
  )

  private val reportingColumnsRowFormatter = ColumnComparisonTableRowFormat(
    reportingColumnsRowBorders,
    dataRowSizes
  )

  private val normalColumnsRowFormatter = ColumnComparisonTableRowFormat(
    normalRowBorders,
    dataRowSizes
  )

  private val softHorizontalDivider = normalColumnsRowFormatter(
    ColumnComparisonTableRow("", "", "", "")
  )

  private val strongHorizontalDivider = {
    val borderChar = "+"
    val fillerChar = "-"
    val labelColumnSize = 17
    val positionColumnSize = 12
    val valueColumnSize = 32
    val strongHorizontalDividerBorders =
      ColumnComparisonTableColumnsBorders(borderChar, borderChar, borderChar)
    val strongHorizontalDividerSizes = ColumnComparisonTableColumnSizes(
      labelColumnSize,
      positionColumnSize,
      labelColumnSize)
    val strongHorizontalDividerFormat = ColumnComparisonTableRowFormat(
      strongHorizontalDividerBorders,
      strongHorizontalDividerSizes)
    val strongHorizontalDividerComponents = ColumnComparisonTableRow(
      fillerChar * labelColumnSize,
      fillerChar * positionColumnSize,
      fillerChar * valueColumnSize,
      fillerChar * valueColumnSize
    )
    strongHorizontalDividerFormat(strongHorizontalDividerComponents)
  }

  private val invisibleHorizontalDivider = "\n\n"
  private def interpretColumnComparisons(
      comparisonBetweenTwoColumnsList: List[ComparisonBetweenTwoColumns],
      sizes: ColumnComparisonTableColumnSizes,
      invalidColumnsFormatter: ColumnComparisonTableRowFormat,
      reportingColumnsFormatter: ColumnComparisonTableRowFormat,
      bothColumnsFailedChecksFormatter: ColumnComparisonTableRowFormat,
      leftColumnFailedChecksFormatter: ColumnComparisonTableRowFormat,
      rightColumnFailedChecksFormatter: ColumnComparisonTableRowFormat,
      rowsDivider: String
  ): List[String] = {
    @tailrec def loop(
        comparisonBetweenTwoColumnsList: List[ComparisonBetweenTwoColumns],
        sizes: ColumnComparisonTableColumnSizes,
        invalidColumnsFormatter: ColumnComparisonTableRowFormat,
        reportingColumnsFormatter: ColumnComparisonTableRowFormat,
        bothColumnsFailedChecksFormatter: ColumnComparisonTableRowFormat,
        leftColumnFailedChecksFormatter: ColumnComparisonTableRowFormat,
        rightColumnFailedChecksFormatter: ColumnComparisonTableRowFormat,
        rowsDivider: String,
        accumulator: List[List[String]]
    ): List[String] = comparisonBetweenTwoColumnsList match {
      case Nil => accumulator.flatten

      case (ValidColumns | IrrelevantColumns) :: restOfColumnComparisons =>
        loop(
          restOfColumnComparisons,
          sizes,
          invalidColumnsFormatter,
          reportingColumnsFormatter,
          bothColumnsFailedChecksFormatter,
          leftColumnFailedChecksFormatter: ColumnComparisonTableRowFormat,
          rightColumnFailedChecksFormatter: ColumnComparisonTableRowFormat,
          rowsDivider,
          accumulator
        )
      case (reportingColumns: ReportingColumns) :: restOfColumnComparisons =>
        val columnComparisonRows = ColumnComparisonTableRow(
          sizes,
          reportingColumns.columnLabel,
          reportingColumns.columnPosition.toString,
          reportingColumns.leftFileColumnValue,
          reportingColumns.rightFileColumnValue
        )
        val columnComparisonTableRowText = rowsDivider :: columnComparisonRows
          .map(reportingColumnsFormatter(_))
        loop(
          restOfColumnComparisons,
          sizes,
          invalidColumnsFormatter,
          reportingColumnsFormatter,
          bothColumnsFailedChecksFormatter,
          leftColumnFailedChecksFormatter: ColumnComparisonTableRowFormat,
          rightColumnFailedChecksFormatter: ColumnComparisonTableRowFormat,
          rowsDivider,
          columnComparisonTableRowText :: accumulator
        )
      case (invalidColumns: InvalidColumns) :: restOfColumnComparisons =>
        val columnComparisonRows = ColumnComparisonTableRow(
          sizes,
          invalidColumns.columnLabel,
          invalidColumns.columnPosition.toString,
          invalidColumns.leftFileColumnValue,
          invalidColumns.rightFileColumnValue
        )
        val columnComparisonTableRowText = rowsDivider :: columnComparisonRows
          .map(invalidColumnsFormatter(_))
        loop(
          restOfColumnComparisons,
          sizes,
          invalidColumnsFormatter,
          reportingColumnsFormatter,
          bothColumnsFailedChecksFormatter,
          leftColumnFailedChecksFormatter: ColumnComparisonTableRowFormat,
          rightColumnFailedChecksFormatter: ColumnComparisonTableRowFormat,
          rowsDivider,
          columnComparisonTableRowText :: accumulator
        )
      case (bothColumnsFailedChecks: BothColumnsFailedChecks) :: restOfColumnComparisons =>
        val columnComparisonRows = ColumnComparisonTableRow(
          sizes,
          bothColumnsFailedChecks.columnLabel,
          bothColumnsFailedChecks.columnPosition.toString,
          bothColumnsFailedChecks.leftFileColumnValue,
          bothColumnsFailedChecks.rightFileColumnValue
        )
        val columnComparisonTableRowText = rowsDivider :: columnComparisonRows
          .map(bothColumnsFailedChecksFormatter(_))
        loop(
          restOfColumnComparisons,
          sizes,
          invalidColumnsFormatter,
          reportingColumnsFormatter,
          bothColumnsFailedChecksFormatter,
          leftColumnFailedChecksFormatter: ColumnComparisonTableRowFormat,
          rightColumnFailedChecksFormatter: ColumnComparisonTableRowFormat,
          rowsDivider,
          columnComparisonTableRowText :: accumulator
        )
      case (leftColumnFailedChecks: LeftColumnFailedChecks) :: restOfColumnComparisons =>
        val columnComparisonRows = ColumnComparisonTableRow(
          sizes,
          leftColumnFailedChecks.columnLabel,
          leftColumnFailedChecks.columnPosition.toString,
          leftColumnFailedChecks.leftFileColumnValue,
          leftColumnFailedChecks.rightFileColumnValue
        )
        val columnComparisonTableRowText = rowsDivider :: columnComparisonRows
          .map(leftColumnFailedChecksFormatter(_))
        loop(
          restOfColumnComparisons,
          sizes,
          invalidColumnsFormatter,
          reportingColumnsFormatter,
          bothColumnsFailedChecksFormatter,
          leftColumnFailedChecksFormatter: ColumnComparisonTableRowFormat,
          rightColumnFailedChecksFormatter: ColumnComparisonTableRowFormat,
          rowsDivider,
          columnComparisonTableRowText :: accumulator
        )
      case (rightColumnFailedChecks: RightColumnFailedChecks) :: restOfColumnComparisons =>
        val columnComparisonRows = ColumnComparisonTableRow(
          sizes,
          rightColumnFailedChecks.columnLabel,
          rightColumnFailedChecks.columnPosition.toString,
          rightColumnFailedChecks.leftFileColumnValue,
          rightColumnFailedChecks.rightFileColumnValue
        )
        val columnComparisonTableRowText = rowsDivider :: columnComparisonRows
          .map(rightColumnFailedChecksFormatter(_))
        loop(
          restOfColumnComparisons,
          sizes,
          invalidColumnsFormatter,
          reportingColumnsFormatter,
          bothColumnsFailedChecksFormatter,
          leftColumnFailedChecksFormatter: ColumnComparisonTableRowFormat,
          rightColumnFailedChecksFormatter: ColumnComparisonTableRowFormat,
          rowsDivider,
          columnComparisonTableRowText :: accumulator
        )
    }
    loop(
      comparisonBetweenTwoColumnsList,
      sizes,
      invalidColumnsFormatter,
      reportingColumnsFormatter,
      bothColumnsFailedChecksFormatter,
      leftColumnFailedChecksFormatter: ColumnComparisonTableRowFormat,
      rightColumnFailedChecksFormatter: ColumnComparisonTableRowFormat,
      rowsDivider,
      Nil
    )
  }
}
