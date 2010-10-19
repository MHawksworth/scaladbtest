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

class RecordSpec extends DataSourceSpecSupport {

	createTables("hsqldb.sql");

	override def afterEach() {
		jdbcTemplate.update("delete from two_string_table")
		jdbcTemplate.update("delete from single_id_table")
		jdbcTemplate.update("delete from date_table")
	}
	
	val testData = new TestData(dataSource)

	describe("A record") {
		describe("when it has two columns with string values") {
			val table = new Table(testData, "two_string_table")
			val record = table.createRecord("record", Set(
	      new Column("col1", Value(Some("value1"))),
				new Column("col2", Value(Some("value2")))
			))

			it("should construct itself properly") {
				record.table should equal (table)
				record.label should equal ("record")
				record.columns should have size (2)
				record.columns should contain (new Column("col1", Value(Some("value1"))))
				record.columns should contain (new Column("col2", Value(Some("value2"))))
			}

			it("should produce a comma-seperated string of column names") {
				record.commaSeparatedColumnNames should equal ("col1, col2")
			}

			it("should produce a comma-seperated string of values") {
				record.commaSeparatedColumnValues should equal ("'value1', 'value2'")
			}

			it("should build an sql insert string") {
				record.insertSql should equal ("INSERT INTO two_string_table(col1, col2) VALUES('value1', 'value2');")
			}
			
			it("should insert its values as a new record into the table") {
				record.insert()

				val map = jdbcTemplate.queryForMap("select * from two_string_table where col1 = ?", "value1")

				map.get("col1") should equal ("value1")
				map.get("col2") should equal ("value2")
			}
		}

		describe("when it has None in the option values") {
			val table = new Table(testData, "two_string_table")
			val record = table.createRecord("record", Set(
	      new Column("col1", Value(None)),
				new Column("col2", Value(None))
			))

			it("should insert NULL values") {
				record.insert()

				val map = jdbcTemplate.queryForMap("select * from two_string_table where col1 is null")
				
				map.get("col1") should be (null)
				map.get("col2") should be (null)
			}
		}

		describe("when it has one integer id column") {
			val table = new Table(testData, "single_id_table")
			val record = table.createRecord("record", Set(
	      new Column("id", Value(Some("1")))
			))

			it("should insert") {
				record.insert()

				val map = jdbcTemplate.queryForMap("select * from single_id_table where id = ?", new java.lang.Integer(1))

				map.get("id") should equal (1)
			}
		}

		describe("when it has a date value") {
			val table = new Table(testData, "date_table")
			val record = table.createRecord("record", Set(
	      new Column("id", Value(Some("1"))),
				new Column("creation_date", Value(Some("2010-05-15 04:20:11")))
			))

			it("should insert") {
				record.insert()

				val map = jdbcTemplate.queryForMap("select * from date_table where id = ?", new java.lang.Integer(1))

				map.get("id") should equal (1)
				map.get("creation_date").toString should equal ("2010-05-15 04:20:11.0")
			}
		}

		describe("when the has default values defined") {
			val table = new Table(testData, "two_string_table", Set(
				new Column("col1", Value(Some("value1"))),
				new Column("col2", Value(Some("value2")))
			), Set())
			
			it("should insert all the default values if no values in the record are defined") {
				val record = table.createRecord("record")
				record.insert()

				val map = jdbcTemplate.queryForMap("select * from two_string_table where col1 = ?", "value1")

				map.get("col1") should equal ("value1")
				map.get("col2") should equal ("value2")
			}
		}
	}

}