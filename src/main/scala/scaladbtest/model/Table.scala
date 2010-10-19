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

case class Table(testData: TestData, name: String, defaultColumns: Set[Column] = Set(), records: Set[Record] = Set()) {

	def this(name: String, defaultColumns: Set[Column], records: Set[Record]) =
		this(null, name, defaultColumns, records)

	def mergeInDefaultColumnValues(values: Set[Column]): Set[Column] = {
		val columnMap = values.groupBy(_.name)

		for(defaultColumn <- defaultColumns ++ values) yield
			if(columnMap.contains(defaultColumn.name)) columnMap(defaultColumn.name).last
			else defaultColumn
	}

	def createRecord(label: String, columns: Set[Column] = Set()): Record = {
		new Record(this, label,	mergeInDefaultColumnValues(columns))
	}

	def delete() {
		testData.jdbcTemplate.update("delete from " + name)
	}

}