package scaladbtest.model

import javax.sql.DataSource
import collection.mutable.ArrayBuffer
import org.springframework.jdbc.core.JdbcTemplate
import scaladbtest.builder.TestDataResource

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

class TestData(val dataSource: DataSource, val records: ArrayBuffer[Record] = ArrayBuffer()) {

	val tables = ArrayBuffer[Table]()
	val jdbcTemplate = new JdbcTemplate(dataSource)

	def createTable(name: String, defaultColumns: List[Column] = List(), records: List[Record] = List()): Table = {
		val table = new Table(this, name, defaultColumns, records)
		addTable(table)
		addRecords(table.records)
		table
	}

	def createOrMergeTable(name: String, defaultColumns: List[Column] = List(), records: List[Record] = List()) = {
		tables.find(_.name == name) match {
			case Some(table) => mergeRecordsWithExistingTable(records, table)
			case None => createTable(name, defaultColumns, records)
		}
	}

	private def mergeRecordsWithExistingTable(records: List[Record], table: Table): Table = {
		records.foreach(_.table = table)
		addRecords(records)
		table
	}

	private def addRecords(records: List[Record]) {
		records.foreach(addRecord(_))
	}

	def addTable(table: Table) {
		if(table != null && !tables.contains(table)) tables += table
	}

	def addRecord(record: Record) {
		records += record
		addTable(record.table)
	}
	
	def insertAll() {
		records.foreach(_.insert())
	}

	def deleteAll() {
		tables.foreach(_.delete())
	}

	def load(filenames: String*) {
		for(filename <- filenames) {
			new TestDataResource(this).loadFrom(filename)
		}
	}

	override def toString = "TestData()"
}