package scaladbtest.builder

import scaladbtest.DataSourceSpecSupport
import scaladbtest.model.{Column, TestData}
import scaladbtest.model.value.Value
import java.util.Date

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

		it("should parse a single row with a single column") {
			testDataResource loadFrom (dslDir + "one_column.dbt")

			testData.tables should have size (1)
			testData.tables(0).name should equal ("user_account")

			testData.records should have size (1)
			testData.records(0).table.get should equal (testData.tables(0))
			testData.records(0).label.get should equal ("ken")

			testData.records(0).columns should have size (1)
			testData.records(0).columns should contain (
				Column("first_name", Value.string("Ken"), Some(testData.records(0)))
			)
		}

		it("should parse a value that contains many spaces") {
			testDataResource loadFrom (dslDir + "one_column_with_spaces.dbt")

			testData.tables should have size (1)
			testData.tables(0).name should equal ("user_account")

			testData.records should have size (1)
			testData.records(0).table.get should equal (testData.tables(0))
			testData.records(0).label.get should equal ("ken")

			testData.records(0).columns should have size (1)
			testData.records(0).columns should contain (
				Column("full_name", Value.string("Ken Egervari"), Some(testData.records(0))))
		}

		it("should parse $now for a column value and infer today's date") {
			testDataResource loadFrom (dslDir + "now_column.dbt")

			val formattedDate = Value.formatDate(new Date()).substring(0, 15)

			testData.records(0).columns(0).name should equal ("date")
			testData.records(0).columns(0).value.text.get should startWith (formattedDate)
		}

		it("should parse $null or null for a column value and infer None") {
			testDataResource loadFrom (dslDir + "null_column.dbt")

			testData.records should have size (2)

			testData.records(0).columns(0).name should equal ("col")
			testData.records(0).columns(0).value.text should equal (None)

			testData.records(1).columns(0).name should equal ("col")
			testData.records(1).columns(0).value.text should equal (None)
		}

		it("should parse $label and replace it with the label's name") {
			testDataResource loadFrom (dslDir + "label_column.dbt")

			testData.records should have size (1)

			testData.records(0).columns(0).name should equal ("col")
			testData.records(0).columns(0).value.text.get should equal ("$label")
			testData.records(0).columns(0).value.sqlValue should equal ("'record1'")
		}

		it("should parse a single row with 2 columns seperated by a comma") {
			testDataResource loadFrom (dslDir + "two_columns_with_comma.dbt")

			testData.tables should have size (1)
			testData.tables(0).name should equal ("user_account")

			testData.records should have size (1)
			testData.records(0).table.get should equal (testData.tables(0))
			testData.records(0).label.get should equal ("ken")

			testData.records(0).columns should have size (2)
			testData.records(0).columns should contain (
				Column("first_name", Value.string("Ken"), Some(testData.records(0))))
			testData.records(0).columns should contain (
				Column("last_name", Value.string("Egervari"), Some(testData.records(0))))
		}

		it("should parse two records from the same table") {
			testDataResource loadFrom (dslDir + "two_records.dbt")

			testData.tables should have size (1)
			testData.tables(0).name should equal ("user_account")

			testData.records should have size (2)
			testData.records(0).table.get should equal (testData.tables(0))
			testData.records(0).label.get should equal ("ken")
			testData.records(0).columns should have size (2)
			testData.records(0).columns should contain (
				Column("first_name", Value.string("Ken"), Some(testData.records(0))))
			testData.records(0).columns should contain (
				Column("last_name", Value.string("Egervari"), Some(testData.records(0))))

			testData.records(1).table.get should equal (testData.tables(0))
			testData.records(1).label.get should equal ("ben")
			testData.records(1).columns should have size (2)
			testData.records(1).columns should contain (
				Column("first_name", Value.string("Ben"), Some(testData.records(1))))
			testData.records(1).columns should contain (
				Column("last_name", Value.string("Sisko"), Some(testData.records(1))))
		}

		it("should parse two anonyomous records (doesn't have labels)") {
			testDataResource loadFrom (dslDir + "anonymous_records.dbt")

			testData.tables should have size (1)
			testData.tables(0).name should equal ("country")

			testData.records should have size (2)
			testData.records(0).table.get should equal (testData.tables(0))
			testData.records(0).label should equal (None)
			testData.records(0).columns should have size (2)
			testData.records(0).columns should contain (
				Column("id", Value.string("1"), Some(testData.records(0))))
			testData.records(0).columns should contain (
				Column("name", Value.string("Canada"), Some(testData.records(0))))

			testData.records(1).table.get should equal (testData.tables(0))
			testData.records(1).label should equal (None)
			testData.records(1).columns should have size (2)
			testData.records(1).columns should contain (
				Column("id", Value.string("2"), Some(testData.records(1))))
			testData.records(1).columns should contain (
				Column("name", Value.string("United States"), Some(testData.records(1))))
		}

		it("should parse 3 records from 2 different tables and maintain order they were written in") {
			testDataResource loadFrom (dslDir + "three_records_two_tables.dbt")

			testData.tables should have size (2)
			testData.tables(0).name should equal ("user_account")
			testData.tables(1).name should equal ("country")

			testData.records should have size (3)
			testData.records(0).table.get should equal (testData.tables(0))
			testData.records(0).label.get should equal ("ken")
			testData.records(0).columns should have size (2)
			testData.records(0).columns should contain (
				Column("first_name", Value.string("Ken"), Some(testData.records(0))))
			testData.records(0).columns should contain (
				Column("last_name", Value.string("Egervari"), Some(testData.records(0))))

			testData.records(1).table.get should equal (testData.tables(1))
			testData.records(1).label.get should equal ("canada")
			testData.records(1).columns should have size (1)
			testData.records(1).columns should contain (
				Column("name", Value.string("Canada"), Some(testData.records(1))))
			
			testData.records(2).table.get should equal (testData.tables(0))
			testData.records(2).label.get should equal ("ben")
			testData.records(2).columns should have size (2)
			testData.records(2).columns should contain (
				Column("first_name", Value.string("Ben"), Some(testData.records(2))))
			testData.records(2).columns should contain (
				Column("last_name", Value.string("Sisko"), Some(testData.records(2))))
		}

		it("should parse the record with an optionla arrow seperating the record/columns") {
			testDataResource loadFrom (dslDir + "optional_record_arrow.dbt")

			testData.tables should have size (1)
			testData.tables(0).name should equal ("stop_list")

			testData.records should have size (1)
			testData.records(0).table.get should equal (testData.tables(0))
			testData.records(0).label.get should equal ("stopList1")
			testData.records(0).columns should have size (2)
			testData.records(0).columns should contain (
				Column("id", Value.string("1"), Some(testData.records(0))))
			testData.records(0).columns should contain (
				Column("words", Value.string("the and in"), Some(testData.records(0))))
		}
	}

}