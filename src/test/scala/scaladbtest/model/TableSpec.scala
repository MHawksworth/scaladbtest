package scaladbtest.model

import scaladbtest.DataSourceSpecSupport
import value.Value
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

class TableSpec extends DataSourceSpecSupport {

	val testData = new TestData(dataSource)

	createTables("hsqldb.sql")

	describe("A Table") {
		describe("when has no default columns") {
			val table = new Table(testData, "a_table")

			it("should create records that only contain their specified columns") {
				val values = List(Column("name", Value(Some("Value"))))
				val record = table.createRecord(Some("label"), values)

				record.table.get should equal (table)
				record.label.get should equal ("label")
				record.columns should equal (values)
			}
		}

		describe("when has default values") {
			val table = new Table(testData, "two_string_table", List(
				Column("col1", Value(Some("value1"))),
				Column("col2", Value(Some("value2")))
			))

			it("should copy all default values when creating a blank record") {
				val record = table.createRecord(Some("label"))

				record.columns should have size (2)
				record.columns should contain (Column("col1", Value(Some("value1")), Some(record)))
				record.columns should contain (Column("col2", Value(Some("value2")), Some(record)))
			}
			
			it("should copy only the unspecified default values when creating a record") {
				val record = table.createRecord(Some("label"), List(
					Column("col1", Value(Some("spooked")))
				))

				record.columns should have size (2)
				record.columns should contain (Column("col1", Value(Some("spooked")), Some(record)))
				record.columns should contain (Column("col2", Value(Some("value2")), Some(record)))
			}

			it("should be able to delete all records from table") {
				jdbcTemplate.update("insert into two_string_table(col1, col2) values('value1', 'value2')")

				jdbcTemplate.queryForInt("select count(*) from two_string_table") should equal (1)

				table.delete()

				jdbcTemplate.queryForInt("select count(*) from two_string_table") should equal (0)
			}
		}
		
		describe("when has records passed to the constructor") {
			val table = new Table(testData, "a_table", List(), List(
				Record(Some("label1")), Record(Some("label2"))
			))

			it("should link each record to the table when constructed") {
				table.records(0).table.get should equal (table)
				table.records(1).table.get should equal (table)
			}
		}
	}

}