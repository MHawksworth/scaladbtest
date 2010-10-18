package scaladbunit.model

case class Record(table: Table, label: String, values: Set[Column]) {

	def commaSeparatedString(columnToString: Column => String): String = {
		values.tail.foldLeft(columnToString(values.head))(_ + ", " + columnToString(_))
	}
	
	def commaSeparatedColumnNames: String = {
		commaSeparatedString(_.name)
	}

	def commaSeparatedColumnValues: String = {
		commaSeparatedString(_.value.get.toString)
	}

	def insertSql = {
		new StringBuilder()
			.append("INSERT INTO ")
			.append(table.name)
			.append("(")
			.append(commaSeparatedColumnNames)
			.append(") VALUES(")
			.append(commaSeparatedColumnValues)
			.append(");")
			.toString
	}

	def insert {

	}

}