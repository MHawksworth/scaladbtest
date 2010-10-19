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
			val record = new Record(null, "record1", List())

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

			it("should use the record's label as the value if parses $label") {
				Value.parse("$label", record).text.get should equal ("record1")
			}

			it("should throw an exception if the record is null when asked to get the label") {
				intercept[IllegalArgumentException] {
					Value.parse("$label", null)
				}
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
		}

		describe("When None") {
			val value = new Value(None)

			it("should return the value with single-quotes when asked for it's sql value")	{
				value.sqlValue should equal ("NULL")
			}
		}
	}

}