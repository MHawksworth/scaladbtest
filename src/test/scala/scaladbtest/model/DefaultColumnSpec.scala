package scaladbtest.model

import scaladbtest.SpecSupport
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

class DefaultColumnSpec extends SpecSupport {

	describe("A Default Column") {
		val defaultColumn = DefaultColumn("first_name", Value.string("Ken"))

		it("should create regular columns") {
			defaultColumn.create() should equal (Column("first_name", Value.string("Ken")))

			val record = Record(None)

			defaultColumn.create(Some(record)) should equal (
				Column("first_name", Value.string("Ken"), Some(record)))
		}

		it("should have a string representation") {
			defaultColumn.toString should equal ("DefaultColumn(first_name,Value(Some(Ken)))")
		}

		it("should be equal to a column with the same values") {
			defaultColumn should equal (DefaultColumn("first_name", Value(Some("Ken"))))
			defaultColumn should not equal (DefaultColumn("last_name", Value(Some("Ken"))))
			defaultColumn should not equal (DefaultColumn("first_name", Value(Some("Dave"))))
		}

	}

}