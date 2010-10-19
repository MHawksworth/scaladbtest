package scaladbtest.model

import scaladbtest.SpecSupport
import java.util.{Date, GregorianCalendar}
import scaladbtest.model.value.Value

/*
* Copyright 2010 Ken Egervari
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

class ValueSpec extends SpecSupport {

	describe("A Value Companion Object") {
		it("should create string values") {
			Value.string("value1").text.get should equal ("value1")
		}

		it("should create a None Value if string is null") {
			Value.string(null).text should equal (None)
		}

		it("should create a None value if asked to create none") {
			Value.none().text should equal (None)
		}

		it("should format a date and time") {
			val date = new GregorianCalendar(2010, 4, 15, 8, 30, 20).getTime

			Value.formatDate(date) should equal ("2010-05-15 08:30:20")
		}

		it("should return a value of today's date") {
			Value.now().text.get should startWith (Value.formatDate(new Date()).substring(0, 17))
		}

		describe("when asked to parse") {
			val record = new Record("record1")

			it("should create the value as a string if normal text") {
				Value.parse("null").text.get should equal ("null")
			}

			it("should create a None value if parses $null") {
				Value.parse("$null").text should equal (None)
			}

			it("should create a None value if parses a null value") {
				Value.parse(null).text should equal (None)
			}

			it("should create today's date if parses $now") {
				Value.parse("$now").text.get should startWith (Value.formatDate(new Date()).substring(0, 17))
			}

			it("should trim text argument") {
				Value.parse(" $null ").text should equal (None)
			}
			
			it("should ignore case when matching strings with $") {
				Value.parse("$NULL").text should equal (None)
			}
		}
	}

	describe("A Value") {
		describe("When it contains a string") {
			val value = new Value(Some("value1"))

			it("should return the value with single-quotes when asked for it's sql value")	{
				value.sqlValue should equal ("'value1'")
			}

			it("should have a string representation") {
				value.toString should equal ("Value(Some(value1))")
			}

			it("should be equal to a value with the same values") {
				value should equal (Value(Some("value1")))
				value should not equal (Value(Some("Hey")))
				value should not equal (Value(None))
				//todo: Find a way to handle recursive relationships and do it properly. 
				//value should not equal (Value(Some("value1"), Some(Column("col1", Value(Some("value1"))))))
			}
		}

		describe("When None") {
			val value = new Value(None)

			it("should return the value with single-quotes when asked for it's sql value")	{
				value.sqlValue should equal ("NULL")
			}
		}
		
		describe("when it refers to the label") {
			it("should return the record label's value and not $label") {
				val column = new Column("col1", Value(Some("$label")))
				val record = new Record("sex", List(column))

				column.value.sqlValue should equal ("'sex'")
			}

			it("should return the record label if $label has a different case") {
				val column = new Column("col1", Value(Some("$LABEL")))
				val record = new Record("sex", List(column))

				column.value.sqlValue should equal ("'sex'")
			}

			it("should throw an exception if column is not defined") {
				val value = Value(Some("$label"))

				intercept[IllegalStateException] {
					value.sqlValue
				}
			}

			it("should throw an exception if record is not defined") {
				val column = new Column("col1", Value(Some("$label")))

				intercept[IllegalStateException] {
					column.value.sqlValue
				}
			}
		}
	}

}