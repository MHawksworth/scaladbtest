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

class TestData(val dataSource: DataSource) {

	val tables = ArrayBuffer[Table]()
	val jdbcTemplate = new JdbcTemplate(dataSource)

	def createTable(name: String, defaultColumns: List[DefaultColumn] = List(), records: List[Record] = List()): Table = {
		val table = new Table(this, name, defaultColumns, records)
		addTable(table)
		table
	}

	def addTable(table: Table) {
		if(table != null) tables += table
	}

	def insertAll() {
		for(table <- tables;	record <- table.records) {
			record.insert()
		}
	}

	def deleteAll() {
		tables.foreach(_.delete())
	}

	def load(filename: String) {
		load(List(filename))
	}

	def load(filenames: Traversable[String]) {
		for(filename <- filenames) {
			new TestDataResource(this).loadFrom(filename)
		}
	}

	override def toString = "TestData()"
}