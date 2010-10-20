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
		describe("when has no default columns or records") {
			val column = Column("name", Value(Some("Value")))
			val table = new Table(testData, "a_table", List(),
				List(Record(Some("label"), List(column)))
			)

			it("should create records that only contain their specified columns") {
				table.records(0).table.get should equal (table)
				table.records(0).label.get should equal ("label")
				table.records(0).columns should equal (List(column))
			}
		}

		describe("when has default columns") {
			val table = new Table(testData, "two_string_table", List(
				DefaultColumn("col1", Value(Some("value1"))),
				DefaultColumn("col2", Value(Some("value2")))
			))

			it("should link the default columns to the table definiton") {
				table.defaultColumns(0).table.get should equal (table)
				table.defaultColumns(1).table.get should equal (table)
			}
		}
		
		describe("when has records") {
			val table = new Table(testData, "two_string_table", List(), List(
				Record(Some("label1")), Record(Some("label2"))
			))

			it("should link each record to the table when constructed") {
				table.records(0).table.get should equal (table)
				table.records(1).table.get should equal (table)
			}

			it("should be able to delete all records from table") {
				jdbcTemplate.update("insert into two_string_table(col1, col2) values('value1', 'value2')")

				jdbcTemplate.queryForInt("select count(*) from two_string_table") should equal (1)

				table.delete()

				jdbcTemplate.queryForInt("select count(*) from two_string_table") should equal (0)
			}
		}
		
		describe("when has default values AND empty records") {
			val table = new Table(testData, "two_string_table",
				List(DefaultColumn("col1", Value(Some("value1"))), DefaultColumn("col2", Value(Some("value2"))))	,
				List(Record(Some("label1")), Record(Some("label2")))
			)

			it("should merge default values when table is constructed") {
				table.records(0).columns should have size (2)
				table.records(0).columns should contain (
					Column("col1", Value(Some("value1")), Some(table.records(0))))
				table.records(0).columns should contain (
					Column("col2", Value(Some("value2")), Some(table.records(0))))

				table.records(1).columns should have size (2)
				table.records(1).columns should contain (
					Column("col1", Value(Some("value1")), Some(table.records(1))))
				table.records(1).columns should contain (
					Column("col2", Value(Some("value2")), Some(table.records(1))))
			}
		}

		describe("when has default values AND records that are partially populated") {
			val table = new Table(testData, "two_string_table",
				List(DefaultColumn("col1", Value(Some("value1"))), DefaultColumn("col2", Value(Some("value2"))))	,
				List(Record(Some("label1"), List(Column("col1", Value.string("spooked")))))
			)
		
			it("should merge only the unspecified default values when creating a record") {
				table.records(0).columns should have size (2)
				table.records(0).columns should contain (Column("col1", Value(Some("spooked")), Some(table.records(0))))
				table.records(0).columns should contain (Column("col2", Value(Some("value2")), Some(table.records(0))))
			}
		}
	}

}