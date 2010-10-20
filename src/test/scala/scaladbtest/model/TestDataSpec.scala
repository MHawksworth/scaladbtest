package scaladbtest.model

import scaladbtest.DataSourceSpecSupport
import value.Value
import javax.sql.DataSource

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

	var testData: TestData = _
	var table: Table = _

	override protected def initializeDataSourceReferences(dataSource: DataSource) {
		testData = new TestData(dataSource)
		table = new Table(testData, "single_id_table")
	}

	describe("Test Data") {
		describe("when constructed") {
			it("should have a data source, a jdbc template and a filename") {
				testData.dataSource should equal (dataSource)
				testData.jdbcTemplate.getDataSource should equal (dataSource)
				testData.tables should have size (0)
			}

			it("should create tables with default values specified") {
				val defaultColumns = List(DefaultColumn("id", Value(Some("1"))))
				val table = testData.createTable("name", defaultColumns)

				table.name should equal ("name")
				table.defaultColumns should equal (defaultColumns)
				table.testData should equal (testData)

				testData.tables should have size (1)
				testData.tables should contain (table)

				testData.tables(0).records should have size (0)
			}

			it("should add all the previous records from the table") {
				val record = new Record(Some("label"))
				val table = testData.createTable("name", List(), List(record))

				testData.tables(0).records should have size (1)
				testData.tables(0).records(0) should equal (record)
			}

			it("should be able to create a table with the same name") {
				val defaultColumns = List(DefaultColumn("id", Value(Some("1"))))
				testData.createTable("name", defaultColumns)
				testData.createTable("name", defaultColumns)

				testData.tables should have size (2)
			}

			it("should be able to create a table with the same name and keep records with correct table reference") {
				val record = new Record(Some("record1"))

				val defaultColumns = List(DefaultColumn("id", Value(Some("1"))))
				testData.createTable("name", defaultColumns)
				testData.createTable("name", defaultColumns, List(record))

				testData.tables should have size (2)
				testData.tables(0).records should have size (0)
				testData.tables(1).records should have size (1)
				testData.tables(1).records(0) should equal (record)
				testData.tables(1).records(0).table.get should equal (testData.tables(1))
				testData.tables(1).records(0).table.get.name should equal ("name")
			}
		}

		describe("when contains a few records from the same table") {
			it("should be able to insert and delete all records into the database") {
				val table = testData.createTable("single_id_table", List(), List(
					Record(None, List(Column("id", Value(Some("1"))))),
					Record(None, List(Column("id", Value(Some("2"))))),
					Record(None, List(Column("id", Value(Some("3")))))
				))

				testData.insertAll()

				jdbcTemplate.queryForInt("select count(*) from single_id_table") should equal (3)

				testData.deleteAll()

				jdbcTemplate.queryForInt("select count(*) from single_id_table") should equal (0)
			}
		}

		describe("when contains a few records from different tables") {
			it("should be able to insert and delete all records into the database") {
				val table1 = testData.createTable("single_id_table", List(),
					List(Record(Some("record1"), List(Column("id", Value(Some("1")))))))
				val table2 = testData.createTable("two_string_table", List(),
					List(Record(Some("record2"), List(Column("col1", Value(Some("val2")))))))

				testData.insertAll()

				jdbcTemplate.queryForInt("select count(*) from single_id_table") should equal (1)
				jdbcTemplate.queryForInt("select count(*) from two_string_table") should equal (1)

				testData.deleteAll()

				jdbcTemplate.queryForInt("select count(*) from single_id_table") should equal (0)
				jdbcTemplate.queryForInt("select count(*) from two_string_table") should equal (0)
			}
		}

		describe("when has a dsl to load") {
			it("should load all the records from the file") {
				testData.load(resourceDir + "dsl/two_string_table.dbt")

				testData.tables should have size (1)
				testData.tables(0).records should have size (2)

				testData.insertAll()

				jdbcTemplate.queryForInt("select count(*) from two_string_table") should equal (2)

				testData.deleteAll()

				jdbcTemplate.queryForInt("select count(*) from two_string_table") should equal (0)
			}
		}
	}

}