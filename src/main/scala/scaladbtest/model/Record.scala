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

object Record {
	def apply(label: Option[String], columns: List[Column] = List(), table: Option[Table] = None) =
		new Record(label, columns, table)
}

class Record(val label: Option[String], var columns: List[Column] = List(), var table: Option[Table] = None) {

	columns.foreach(_.record = Some(this))

	def commaSeparatedString(columnToString: Column => String): String = {
		columns.tail.foldLeft(columnToString(columns.head))(_ + ", " + columnToString(_))
	}
	
	def commaSeparatedColumnNames: String = {
		commaSeparatedString(_.name)
	}

	def commaSeparatedColumnValues: String = {
		commaSeparatedString(_.value.sqlValue)
	}

	def insertSql = {
		verifyTableExists()

		new StringBuilder()
			.append("INSERT INTO ")
			.append(table.get.name)
			.append("(")
			.append(commaSeparatedColumnNames)
			.append(") VALUES(")
			.append(commaSeparatedColumnValues)
			.append(");")
			.toString
	}

	def insert() {
		verifyTableExists()

		table.get.testData.jdbcTemplate.update(insertSql)
	}

	def verifyTableExists() {
		if (table.isEmpty)
			throw new IllegalStateException("A table must be defined for record: " + this)
	}

	override def toString = {
		val tableName =
			if(table.isDefined) table.get.name
			else "N/A"

		"Record(Table(" + tableName + ")," + label + "," + columns + ")"
	}

	override def equals(other: Any): Boolean = {
		other match {
			case that: Record =>
				label == that.label &&
				columns == that.columns &&
				table == that.table
			case _ => false
		}
	}

	override def hashCode: Int = {
		41 * (
			41 * (
				41 + label.hashCode
			) + columns.hashCode
		) + table.hashCode
	}

}