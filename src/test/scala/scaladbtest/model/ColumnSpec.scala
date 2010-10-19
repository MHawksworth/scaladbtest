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

class ColumnSpec extends SpecSupport {

	describe("A Column") {
		describe("when belongs to no record") {
			val column = Column("col1", Value(Some("Hey")))

			it("should link value's column to this one") {
				column.value.column.get should equal (column)
			}

			it("should have a string representation") {
				column.toString should equal ("Column(col1,Value(Some(Hey)))")
			}

			it("should be equal to a column with the same values") {
				column should equal (Column("col1", Value(Some("Hey"))))
				column should not equal (Column("col2", Value(Some("Hey"))))
				column should not equal (Column("col1", Value(Some("Hey1"))))
				column should not equal (Column("col1", Value(Some("Hey")), Some(Record("label"))))
			}
		}

		describe("when belongs to a record") {
			val column = Column("col1", Value(Some("Hey")), Some(Record("label")))

			it("should be equal to a column with the same values") {
				column should equal (Column("col1", Value(Some("Hey")), Some(Record("label"))))
				column should not equal (Column("col2", Value(Some("Hey")), Some(Record("label"))))
				column should not equal (Column("col1", Value(Some("Hey1")), Some(Record("label"))))
				column should not equal (Column("col1", Value(Some("Hey")), Some(Record("anotherLabel"))))
			}
		}
	}

}