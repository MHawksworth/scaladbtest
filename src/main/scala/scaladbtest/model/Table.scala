package scaladbtest.model

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

case class Table(testData: TestData, name: String, defaultColumns: List[Column] = List(), records: List[Record] = List()) {

	records.map(_.table = Some(this))

	def mergeInDefaultColumnValues(columns: List[Column]): List[Column] = {
		val columnNames = columns.groupBy(_.name)
		val defaultColumnsNeeded = defaultColumns.filterNot(
			(c: Column) => columnNames.contains(c.name))

		columns ++ defaultColumnsNeeded
	}

	def createRecord(label: Option[String], columns: List[Column] = List()): Record = {
		new Record(label,	mergeInDefaultColumnValues(columns), Some(this))
	}

	def delete() {
		testData.jdbcTemplate.update("delete from " + name)
	}

}