package scaladbtest.model

import javax.sql.DataSource
import collection.mutable.ArrayBuffer
import org.springframework.jdbc.core.JdbcTemplate

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

class TestData(val dataSource: DataSource, val filename: String = "", val records: ArrayBuffer[Record] = ArrayBuffer()) {

	val tables = ArrayBuffer[Table]()
	val jdbcTemplate = new JdbcTemplate(dataSource)
	var isLoaded = false

	def createTable(name: String, defaultColumns: Set[Column] = Set(), records: Set[Record] = Set()) = {
		val table = new Table(this, name, defaultColumns, records)

		addTable(table)

		table
	}

	def addTable(table: Table) {
		if(!tables.contains(table)) tables += table
	}

	def +(record: Record) {
		records += record
		addTable(record.table)
	}

	def addRecord(record: Record) {
		this + record
	}
	
	def insertAll() {
		records.foreach(_.insert())
	}

	def deleteAll() {
		tables.foreach(_.delete())
	}

}