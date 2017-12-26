package com.scalableQuality.quick.core.fileComponentDescriptions

import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.prop.{
  GeneratorDrivenPropertyChecks,
  TableDrivenPropertyChecks
}

class DelimitedColumnDescriptionTest
    extends FlatSpec
    with Matchers
    with GeneratorDrivenPropertyChecks
    with TableDrivenPropertyChecks {

  "DelimitedColumnDescription.apply(MetaData)" should "return Right when only the label and position is present" in {
    val columnDescriptionElem = <ColumnDescription
      label="this is a column in russian accent"
      position="1"
      />
    val columnDescription =
      DelimitedColumnDescription(columnDescriptionElem.attributes)
    columnDescription shouldBe a[Right[_, _]]
  }

  it should
    "accepts all the following attributes, label, startsAt, endsAt, useDuringValidation, useDuringMatching, useDuringReporting,  trimComparisonValue, ignoreComparisonValueCase" in {
    val columnDescriptionElem = <ColumnDescription
      label="this is a column in russian accent"
      position="1"
      useDuringValidation="true"
      useDuringMatching="false"
      useDuringReporting="true"
       trimComparisonValue="true"
      ignoreComparisonValueCase="false" />
    val columnDescription =
      DelimitedColumnDescription(columnDescriptionElem.attributes)
    columnDescription shouldBe a[Right[_, _]]
  }

  val invalidAttributeValue = Table(
    ("position",
     "useDuringValidation",
     "useDuringMatching",
     "useDuringReporting",
     " trimComparisonValue",
     "ignoreComparisonValueCase"),
    ("P", "true", "false", "true", "false", "true"),
    ("1", "BlaBla", "false", "true", "false", "true"),
    ("1", "true", "BlaBla", "true", "false", "true"),
    ("1", "true", "false", "BlaBla", "false", "true"),
    ("1", "true", "false", "true", "BlaBla", "true"),
    ("1", "true", "false", "true", "false", "BlaBla")
  )

  it should " return Left[ErrorMessage, FixedColumnDescription] if any of the attributes have an invalid value" in
    forAll(invalidAttributeValue) {
      (position: String,
       useDuringValidation: String,
       useDuringMatching: String,
       useDuringReporting: String,
        trimComparisonValue: String,
       ignoreComparisonValueCase: String) =>
        val columnDescriptionElem = <ColumnDescription
          label="this is a column in russian accent"
          position={position}
          useDuringValidation={useDuringValidation}
          useDuringMatching={useDuringMatching}
          useDuringReporting={useDuringReporting}
           trimComparisonValue={ trimComparisonValue}
          ignoreComparisonValueCase={ignoreComparisonValueCase}
          />
        val columnDescription =
          DelimitedColumnDescription(columnDescriptionElem.attributes)
        columnDescription shouldBe a[Left[_, _]]
    }

  it should "return Left(errorMessage) if label attribute is missing" in {
    val columnDescriptionElem = <ColumnDescription
      position="1"
      />
    val columnDescription =
      DelimitedColumnDescription(columnDescriptionElem.attributes)
    columnDescription shouldBe a[Left[_, _]]
  }

  it should "return Left(errorMessage) if position attribute is missing" in {
    val columnDescriptionElem = <ColumnDescription
      label="la be l"
      />
    val columnDescription =
      DelimitedColumnDescription(columnDescriptionElem.attributes)
    columnDescription shouldBe a[Left[_, _]]
  }

  it should
    "return Left if a misspelled attribute is present" in {
    val columnDescriptionElem = <ColumnDescription
      label="this is a column in russian accent"
      position="1"
      useDuringValidation="true"
      useDuringMatching="false"
      useDuringReporting="true"
       trimComparisonValue="true"
      misspelled="false"
      />
    val columnDescription =
      DelimitedColumnDescription(columnDescriptionElem.attributes)
    columnDescription shouldBe a[Left[_, _]]
  }

  it should
    "return Left if an unknown attribute is present" in {
    val columnDescriptionElem = <ColumnDescription
      label="this is a column in russian accent"
      position="1"
      useDuringValidation="true"
      useDuringMatching="false"
      useDuringReporting="true"
       trimComparisonValue="true"
      ignoreComparisonValueCase="false"
      unknown="present"
      />
    val columnDescription =
      DelimitedColumnDescription(columnDescriptionElem.attributes)
    columnDescription shouldBe a[Left[_, _]]
  }
}