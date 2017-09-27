package com.scalableQuality.quick.core.fileComponentDescripts

import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

class ColumnDescriptionTest extends FlatSpec with Matchers with TableDrivenPropertyChecks {
  "ColumnDescription.apply(MetaData)" should
    "return Right[ErrorMessage, ColumnDescription] even if only the label, the startsAt and endsAt  attributes are present" in {
    val columnDescriptionElem = <ColumnDescription
       label="this is a column in russian accent"
       startsAt="1"
       endsAt="3"
      />
    val columnDescription = ColumnDescription(columnDescriptionElem.attributes)
    columnDescription shouldBe a [Right[_,_]]
  }

  it should
    "return Right[ErrorMessage, ColumnDescription] even if only the label, the startsAt and length  attributes are present" in {
    val columnDescriptionElem = <ColumnDescription
      label="this is a column in russian accent"
      startsAt="1"
      length="3"
      />
    val columnDescription = ColumnDescription(columnDescriptionElem.attributes)
    columnDescription shouldBe a [Right[_,_]]
  }

  it should
    "accepts all the following attributes, label, startsAt, endsAt, useDuringValidation, useDuringMatching, useDuringReporting, trimValue, ignoreValueCase" in {
    val columnDescriptionElem = <ColumnDescription
    label="this is a column in russian accent"
    startsAt="1"
    endsAt="3"
    length="3"
    useDuringValidation="true"
    useDuringMatching="false"
    useDuringReporting="true"
    trimValue="true"
    ignoreValueCase="false" />
    val columnDescription = ColumnDescription(columnDescriptionElem.attributes)
    columnDescription shouldBe a [Right[_,_]]
  }

  it should "return Left[ErrorMessage, ColumnDescription] when it encounters an unknown attribute" in {
    val columnDescriptionElem = <ColumnDescription
      label="this is a column in russian accent"
      startsAt="1"
      endsAt="3"
      length="3"
      useDuringValidation="true"
      useDuringMatching="false"
      useDuringReporting="true"
      trimValue="true"
      ignoreValueCase="false"
      misspelledAttribute="value" />
    val columnDescription = ColumnDescription(columnDescriptionElem.attributes)
    columnDescription shouldBe a [Left[_,_]]
  }

  val invalidAttributeValue = Table(
    ("startsAt","endsAt","length","useDuringValidation","useDuringMatching","useDuringReporting","trimValue","ignoreValueCase"),
    ("Z","3","3","true","false","true","false","true"),
    ("1","Z","3","true","false","true","false","true"),
    ("1","3","Z","true","false","true","false","true"),
    ("1","3","3","BlaBla","false","true","false","true"),
    ("1","3","3","true","BlaBla","true","false","true"),
    ("1","3","3","true","false","BlaBla","false","true"),
    ("1","3","3","true","false","true","BlaBla","true"),
    ("1","3","3","true","false","true","false","BlaBla")
  )

  it should "return return Left[ErrorMessage, ColumnDescription] if any of the attributes have an invalid value" in
    forAll(invalidAttributeValue) {
      (startsAt:String,
      endsAt:String,
      length:String,
      useDuringValidation:String,
      useDuringMatching:String,
      useDuringReporting:String,
      trimValue:String,
      ignoreValueCase:String) =>

    val columnDescriptionElem = <ColumnDescription
      label="this is a column in russian accent"
      startsAt={startsAt}
      endsAt={endsAt}
      length={length}
      useDuringValidation={useDuringValidation}
      useDuringMatching={useDuringMatching}
      useDuringReporting={useDuringReporting}
      trimValue={trimValue}
      ignoreValueCase={ignoreValueCase}
      />
    val columnDescription = ColumnDescription(columnDescriptionElem.attributes)
    columnDescription shouldBe a [Left[_,_]]
  }
}
