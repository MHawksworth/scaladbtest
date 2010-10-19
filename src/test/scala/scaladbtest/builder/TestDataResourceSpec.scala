package scaladbtest.builder

import scaladbtest.DataSourceSpecSupport
import scaladbtest.model.{Column, TestData}
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

class TestDataResourceSpec extends DataSourceSpecSupport {

	val dslDir = resourceDir + "dsl/"

	describe("A Test Data Dsl") {
		val testData = new TestData(dataSource)
		val testDataResource = new TestDataResource(testData)

		it("should do nothing if file is empty") {
			testDataResource loadFrom (dslDir + "empty.dbt")

			testData.tables should have size (0)
			testData.records should have size (0)
		}

		it("should read in a single row with a single column") {
			testDataResource loadFrom (dslDir + "one_column.dbt")

			testData.tables should have size (1)
			testData.tables(0).name should equal ("user_account")

			testData.records should have size (1)
			testData.records(0).table should equal (testData.tables(0))
			testData.records(0).label should equal ("ken")

			testData.records(0).columns should have size (1)
			testData.records(0).columns should contain (Column("first_name", Value.string("Ken")))
		}

		it("should read in a value that contains many spaces") {
			testDataResource loadFrom (dslDir + "one_column_with_spaces.dbt")

			testData.tables should have size (1)
			testData.tables(0).name should equal ("user_account")

			testData.records should have size (1)
			testData.records(0).table should equal (testData.tables(0))
			testData.records(0).label should equal ("ken")

			testData.records(0).columns should have size (1)
			testData.records(0).columns should contain (Column("full_name", Value.string("Ken Egervari")))
		}

		it("should read in a single row with 2 columns seperated by a comma") {
			testDataResource loadFrom (dslDir + "two_columns_with_comma.dbt")

			testData.tables should have size (1)
			testData.tables(0).name should equal ("user_account")

			testData.records should have size (1)
			testData.records(0).table should equal (testData.tables(0))
			testData.records(0).label should equal ("ken")

			testData.records(0).columns should have size (2)
			testData.records(0).columns should contain (Column("first_name", Value.string("Ken")))
			testData.records(0).columns should contain (Column("last_name", Value.string("Egervari")))
		}

		it("should read in two records from the same table") {
			testDataResource loadFrom (dslDir + "two_records.dbt")

			testData.tables should have size (1)
			testData.tables(0).name should equal ("user_account")

			testData.records should have size (2)
			testData.records(0).table should equal (testData.tables(0))
			testData.records(0).label should equal ("ken")
			testData.records(0).columns should have size (2)
			testData.records(0).columns should contain (Column("first_name", Value.string("Ken")))
			testData.records(0).columns should contain (Column("last_name", Value.string("Egervari")))

			testData.records(1).table should equal (testData.tables(0))
			testData.records(1).label should equal ("ben")
			testData.records(1).columns should have size (2)
			testData.records(1).columns should contain (Column("first_name", Value.string("Ben")))
			testData.records(1).columns should contain (Column("last_name", Value.string("Sisko")))
		}

		it("should read in 3 records from 2 different tables and maintain order they were written in") {
			testDataResource loadFrom (dslDir + "three_records_two_tables.dbt")

			testData.tables should have size (2)
			testData.tables(0).name should equal ("user_account")
			testData.tables(1).name should equal ("country")

			testData.records should have size (3)
			testData.records(0).table should equal (testData.tables(0))
			testData.records(0).label should equal ("ken")
			testData.records(0).columns should have size (2)
			testData.records(0).columns should contain (Column("first_name", Value.string("Ken")))
			testData.records(0).columns should contain (Column("last_name", Value.string("Egervari")))

			testData.records(1).table should equal (testData.tables(1))
			testData.records(1).label should equal ("canada")
			testData.records(1).columns should have size (1)
			testData.records(1).columns should contain (Column("name", Value.string("Canada")))
			
			testData.records(2).table should equal (testData.tables(0))
			testData.records(2).label should equal ("ben")
			testData.records(2).columns should have size (2)
			testData.records(2).columns should contain (Column("first_name", Value.string("Ben")))
			testData.records(2).columns should contain (Column("last_name", Value.string("Sisko")))
		}
	}

}