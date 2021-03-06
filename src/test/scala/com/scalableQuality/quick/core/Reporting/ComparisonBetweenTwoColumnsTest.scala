package com.scalableQuality.quick.core.Reporting

import com.scalableQuality.quick.core.Reporting.InvalidColumns.BothColumnsFailedChecks
import com.scalableQuality.quick.core.fileComponentDescriptions.FixedColumnDescription
import com.scalableQuality.quick.mantle.parsing.RawRow
import org.scalatest.prop.{GeneratorDrivenPropertyChecks, TableDrivenPropertyChecks}
import org.scalatest.{FlatSpec, Matchers}

class ComparisonBetweenTwoColumnsTest
    extends FlatSpec
    with Matchers
    with TableDrivenPropertyChecks
    with GeneratorDrivenPropertyChecks {
  val validColumnsTable = Table(
    "useDuringMatching",
    "true",
    "false"
  )
  "ComparisonBetweenTwoColumns.apply" should
    "return ValidColumns when useDuringValidation = true  && useDuringReporting = false && leftColumn == rightColumn" in
    forAll(validColumnsTable) { (useDuringMatching: String) =>
      val leftRawRow = Some(RawRow("FirstColumn", 1))
      val rightRawRow = Some(RawRow("FirstColumn", 1))
      val columnDescriptionElem = <ColumnDescription
      label="testColumn"
      startsAt="1"
      length="11"
      useDuringValidation="true"
      useDuringReporting="false"
      useDuringMatching={useDuringMatching}
      />
      val columnDescriptionEither =
        FixedColumnDescription(columnDescriptionElem.attributes)
      columnDescriptionEither match {
        case Right(columnDescription) =>
          val comparisonBetweenTwoColumns =
            columnDescription.compareTwoColumns(leftRawRow, rightRawRow)
          comparisonBetweenTwoColumns shouldBe ValidColumns
        case Left(_) =>
          columnDescriptionEither shouldBe a[Right[_, _]]
      }
    }

  val irrelevantColumnsTable = Table(
    ("leftRow", "rightRow", "useDuringMatching"),
    ("FirstColumn", "FirstColumn", "true"),
    ("FirstColumn", "FirstColumn", "false"),
    ("FirstColumn", "SecondColumn", "true"),
    ("FirstColumn", "SecondColumn", "false")
  )
  it should
    "return IrrelevantColumns when useDuringValidation = false  && useDuringReporting = false" in
    forAll(irrelevantColumnsTable) {
      (leftRow: String, rightRow: String, useDuringMatching: String) =>
        val leftRawRow = Some(RawRow(leftRow, 1))
        val rightRawRow = Some(RawRow(rightRow, 1))
        val columnDescriptionElem = <ColumnDescription
        label="testColumn"
        startsAt="1"
        length="11"
        useDuringValidation="false"
        useDuringReporting="false"
        useDuringMatching={useDuringMatching}
        />
        val columnDescriptionEither =
          FixedColumnDescription(columnDescriptionElem.attributes)
        columnDescriptionEither match {
          case Right(columnDescription) =>
            val comparisonBetweenTwoColumns =
              columnDescription.compareTwoColumns(leftRawRow, rightRawRow)
            comparisonBetweenTwoColumns shouldBe IrrelevantColumns
          case Left(_) =>
            columnDescriptionEither shouldBe a[Right[_, _]]
        }
    }

  val reportingColumnsTable = Table(
    ("useDuringValidation", "leftRow", "rightRow", "useDuringMatching"),
    ("false", "FirstColumn", "FirstColumn", "true"),
    ("false", "FirstColumn", "FirstColumn", "false"),
    ("false", "FirstColumn", "BecondColum", "true"),
    ("false", "FirstColumn", "BecondColum", "false"),
    ("true", "FirstColumn", "FirstColumn", "false"),
    ("true", "FirstColumn", "FirstColumn", "true")
  )
  it should
    "return a ReportingColumns when useDuringReporting = true && (useDuringValidation = false || (useDuringValidation = true && leftColumn == rightColumn)) " in
    forAll(reportingColumnsTable) {
      (useDuringValidation: String,
       leftRow: String,
       rightRow: String,
       useDuringMatching: String) =>
        val leftRawRow = Some(RawRow(leftRow, 1))
        val rightRawRow = Some(RawRow(rightRow, 1))
        val columnDescriptionElem = <ColumnDescription
      label="testColumn"
      startsAt="1"
      length="11"
      useDuringValidation={useDuringValidation}
      useDuringReporting="true"
      useDuringMatching={useDuringMatching}
      />
        val columnDescriptionEither =
          FixedColumnDescription(columnDescriptionElem.attributes)
        columnDescriptionEither match {
          case Right(columnDescription) =>
            val comparisonBetweenTwoColumns =
              columnDescription.compareTwoColumns(leftRawRow, rightRawRow)

            val expectedReportingColumn =
              ReportingColumns(Some(leftRow),
                               Some(rightRow),
                               columnDescription.metaData.position,
                               columnDescription.metaData.label)

            comparisonBetweenTwoColumns shouldBe expectedReportingColumn
          case Left(_) =>
            columnDescriptionEither shouldBe a[Right[_, _]]
        }
    }

  val invalidColumns = Table(
    ("useDuringReporting", "useDuringMatching"),
    ("true", "false"),
    ("true", "true"),
    ("false", "false"),
    ("false", "true")
  )
  it should "return a InvalidColumns when useDuringValidation = true && leftColumn == rightColumn" in
    forAll(invalidColumns) {
      (useDuringReporting: String, useDuringMatching: String) =>
        val firstColumnValue = "FirstColumn"
        val misspelledFirstColumnValue = "BecondColum"
        val leftRawRow = Some(RawRow(firstColumnValue, 1))
        val rightRawRow = Some(RawRow(misspelledFirstColumnValue, 1))
        val columnDescriptionElem = <ColumnDescription
        label="testColumn"
        startsAt="1"
        length="11"
        useDuringValidation="true"
        useDuringReporting={useDuringReporting}
        useDuringMatching={useDuringMatching}
        />
        val columnDescriptionEither =
          FixedColumnDescription(columnDescriptionElem.attributes)
        columnDescriptionEither match {
          case Right(columnDescription) =>
            val comparisonBetweenTwoColumns =
              columnDescription.compareTwoColumns(leftRawRow, rightRawRow)
            val expectedInvalidColumn = InvalidColumns(
              Some(firstColumnValue),
              Some(misspelledFirstColumnValue),
              columnDescription.metaData.position,
              columnDescription.metaData.label
            )
            comparisonBetweenTwoColumns shouldBe expectedInvalidColumn
          case Left(_) =>
            columnDescriptionEither shouldBe a[Right[_, _]]
        }
    }


  it should "return FailedChecksColumns if any of the left row's columns failed the checks" in forAll {
    (useDuringValidation: Boolean,
     useDuringMatching: Boolean,
     useDuringReporting: Boolean) =>
      {
        val leftRawRow = Some(RawRow("FirstColumn", 1))
        val rightRawRow = Some(RawRow("FFFstColumn", 1))
        val columnDescriptionElem = <ColumnDescription
        label="testColumn"
        startsAt="1"
        length="11"
        useDuringValidation={useDuringValidation.toString}
        useDuringReporting={useDuringMatching.toString}
        useDuringMatching={useDuringReporting.toString}
        checkColumnValueMatches="[1-9]+"
        />
        val columnDescriptionEither =
          FixedColumnDescription(columnDescriptionElem.attributes)
        columnDescriptionEither match {
          case Right(columnDescription) =>
            val comparisonBetweenTwoColumns =
              columnDescription.compareTwoColumns(leftRawRow, rightRawRow)
            comparisonBetweenTwoColumns shouldBe a[BothColumnsFailedChecks]
          case Left(_) =>
            columnDescriptionEither shouldBe a[Right[_, _]]
        }
      }
  }
}
