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

class TestDataSpec extends DataSourceSpecSupport {

	createTables("hsqldb.sql")

	override def afterEach() {
		jdbcTemplate.update("delete from single_id_table")
	}
	
	describe("Test Data") {
		val testData = new TestData(dataSource)
		val table = new Table(testData, "single_id_table")

		describe("when constructed") {
			it("should have a data source, a jdbc template and a filename") {
				testData.dataSource should equal (dataSource)
				testData.jdbcTemplate.getDataSource should equal (dataSource)
				testData.records should have size (0)
			}

			it("should create tables with default values specified") {
				val defaultColumns = List(Column("id", Value(Some("1"))))
				val table = testData.createOrMergeTable("name", defaultColumns)

				table.name should equal ("name")
				table.defaultColumns should equal (defaultColumns)
				table.testData should equal (testData)

				testData.tables should have size (1)
				testData.tables should contain (table)

				testData.records should have size (0)
			}

			it("should add all the previous records from the table") {
				val record = new Record("label", List())
				val table = testData.createOrMergeTable("name", List(), List(record))

				testData.records should have size (1)
				testData.records(0) should equal (record)
			}

			it("should not re-add the same table twice") {
				val defaultColumns = List(Column("id", Value(Some("1"))))
				testData.createOrMergeTable("name", defaultColumns)
				testData.createOrMergeTable("name", defaultColumns)

				testData.tables should have size (1)
			}

			it("should not re-add the same table, but should merge its records if it has any with the same name") {
				val record = new Record("record1")

				val defaultColumns = List(Column("id", Value(Some("1"))))
				testData.createOrMergeTable("name", defaultColumns)
				testData.createOrMergeTable("name", defaultColumns, List(record))

				testData.tables should have size (1)
				testData.records should have size (1)
				testData.records(0) should equal (record)
				testData.records(0).table.name should equal ("name") 
			}

			it("should be able to add records and also link the table if not already added") {
				val record = new Record(table, "label", List())

				testData addRecord record

				testData.records should have size (1)
				testData.records should contain (record)

				testData.tables should have size (1)
				testData.tables should contain (table)
			}

			it("should not add null table if the record doesn't have a table") {
				val record = new Record(null, "label", List())

				testData addRecord record

				testData.records should have size (1)
				testData.records should contain (record)

				testData.tables should have size (0)
			}
		}
	}
	
	describe("when contains a few records from the same table") {
		val testData = new TestData(dataSource)
		val table = new Table(testData, "single_id_table")

		testData addRecord table.createRecord("record1", List(Column("id", Value(Some("1")))))
		testData addRecord table.createRecord("record2", List(Column("id", Value(Some("2")))))
		testData addRecord table.createRecord("record3", List(Column("id", Value(Some("3")))))

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

		testData addRecord table1.createRecord("record1", List(Column("id", Value(Some("1")))))
		testData addRecord table2.createRecord("record2", List(Column("col1", Value(Some("val2")))))

		it("should be able to insert and delete all records into the database") {
			testData.insertAll()

			jdbcTemplate.queryForInt("select count(*) from single_id_table") should equal (1)
			jdbcTemplate.queryForInt("select count(*) from two_string_table") should equal (1)

			testData.deleteAll()

			jdbcTemplate.queryForInt("select count(*) from single_id_table") should equal (0)
			jdbcTemplate.queryForInt("select count(*) from two_string_table") should equal (0)
		}
	}

	describe("when has a dsl to load") {
		val testData = new TestData(dataSource)

		it("should load all the records from the file") {
			testData.load(resourceDir + "dsl/two_string_table.dbt")

			testData.tables should have size (1)
			testData.records should have size (2)

			testData.insertAll()

			jdbcTemplate.queryForInt("select count(*) from two_string_table") should equal (2)

			testData.deleteAll()

			jdbcTemplate.queryForInt("select count(*) from two_string_table") should equal (0)
		}
	}

}