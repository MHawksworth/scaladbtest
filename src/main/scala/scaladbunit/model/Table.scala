package scaladbunit.model

import javax.sql.DataSource

case class Table(dataSource: DataSource, name: String, defaultColumns: Set[Column], records: Set[Record]) {
	def this(name: String, defaultColumns: Set[Column], records: Set[Record]) =
		this(null, name, defaultColumns, records)
}