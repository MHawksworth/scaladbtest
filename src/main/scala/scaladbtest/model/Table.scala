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

case class Table(testData: TestData, name: String, defaultColumns: List[DefaultColumn] = List(), records: List[Record] = List()) {

	defaultColumns.foreach(_.table = Some(this))

	// TODO: Seriously look at implementing this better, as this is a mix between mutable/immutable is actually kind of bad.
	// set table references, generate defaultColumns columns if not present in record, and then make sure
	// those defaultColumns columns point to the record they are in contained in.
	records.foreach((record: Record) => {
		record.table = Some(this)
		record.columns = mergeInDefaultColumnValues(record.columns, Some(record))
	})

	private def mergeInDefaultColumnValues(columns: List[Column], record: Option[Record]): List[Column] = {
		val columnNames = columns.groupBy(_.name)
		val defaultColumnsNeeded = defaultColumns.filterNot(
			(c: DefaultColumn) => columnNames.contains(c.name))

		columns ++ copyDefaultColumns(defaultColumnsNeeded, record)
	}

	private def copyDefaultColumns(defaultColumns: List[DefaultColumn], record: Option[Record]): List[Column] = {
		defaultColumns.map((defaultColumn: DefaultColumn) =>
			defaultColumn.create(record))
	}

	def delete() {
		testData.jdbcTemplate.update("delete from " + name)
	}

}