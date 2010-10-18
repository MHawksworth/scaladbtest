package scaladbunit.model

import scaladbunit.DataSourceSpecSupport
import value.StringValue

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

class TestDataSpec extends DataSourceSpecSupport {

	createTables("hsqldb.sql")

	override def afterEach() {
		jdbcTemplate.update("delete from single_id_table")
	}
	
	describe("Test Data") {
		val testData = new TestData(dataSource, "somefile.dbu")
		val table = new Table(testData, "single_id_table")

		describe("when constructed") {
			it("should have a data source, a jdbc template and a filename") {
				testData.dataSource should equal (dataSource)
				testData.jdbcTemplate.getDataSource should equal (dataSource)
				testData.filename should equal ("somefile.dbu")
				testData.records should have size (0)
			}

			it("should not be loaded") {
				testData.isLoaded should be (false)
			}

			it("should create tables with default values specified") {
				val defaultColumns = Set(Column("id", StringValue(Some("1"))))
				val table = testData.createTable("name", defaultColumns)

				table.name should equal ("name")
				table.defaultColumns should equal (defaultColumns)
				table.testData should equal (testData)

				testData.tables should have size (1)
				testData.tables should contain (table)
			}

			it("should not re-add the same table twice") {
				val defaultColumns = Set(Column("id", StringValue(Some("1"))))
				testData.createTable("name", defaultColumns)
				testData.createTable("name", defaultColumns)

				testData.tables should have size (1)
			}

			it("should be able to add records and also link the table if not already added") {
				val record = new Record(table, "label", Set())

				testData + record

				testData.records should have size (1)
				testData.records should contain (record)

				testData.tables should have size (1)
				testData.tables should contain (table)
			}

			it("should load from a file") {
				pending
			}
		}
	}
	
	describe("when contains a few records from the same table") {
		val testData = new TestData(dataSource)
		val table = new Table(testData, "single_id_table")

		testData + table.createRecord("record1", Set(Column("id", StringValue(Some("1")))))
		testData + table.createRecord("record2", Set(Column("id", StringValue(Some("2")))))
		testData + table.createRecord("record3", Set(Column("id", StringValue(Some("3")))))

		it("should be able to insert and delete all records into the database") {
			testData.insertAll()

			jdbcTemplate.queryForInt("select count(*) from single_id_table") should equal (3)

			testData.deleteAll()

			jdbcTemplate.queryForInt("select count(*) from single_id_table") should equal (0)
		}
	}

	describe("when contains a few records from different tables") {
		val testData = new TestData(dataSource)
		val table1 = new Table(testData, "single_id_table")
		val table2 = new Table(testData, "two_string_table")

		testData + table1.createRecord("record1", Set(Column("id", StringValue(Some("1")))))
		testData + table2.createRecord("record2", Set(Column("col1", StringValue(Some("val2")))))

		it("should be able to insert and delete all records into the database") {
			testData.insertAll()

			jdbcTemplate.queryForInt("select count(*) from single_id_table") should equal (1)
			jdbcTemplate.queryForInt("select count(*) from two_string_table") should equal (1)

			testData.deleteAll()

			jdbcTemplate.queryForInt("select count(*) from single_id_table") should equal (0)
			jdbcTemplate.queryForInt("select count(*) from two_string_table") should equal (0)
		}
	}

}