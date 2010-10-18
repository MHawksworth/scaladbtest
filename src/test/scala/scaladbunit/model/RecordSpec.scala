package scaladbunit.model

import scaladbunit.TestSupport
import value.StringValue
import io.Source

class RecordSpec extends TestSupport {

	createTables("hsqldb.sql");

	describe("A record") {
		describe("when it contains a column with two string value") {
			val table = new Table(dataSource, "test_table", Set(), Set())
			val record = new Record(table, "record", Set(
	      new Column("col1", Option(StringValue("value1"))),
				new Column("col2", Option(StringValue("value2")))
			))

			it("should construct itself properly") {
				record.table should equal (table)
				record.label should equal ("record")
				record.values should have size (2)
				record.values should contain (new Column("col1", Option(StringValue("value1"))))
				record.values should contain (new Column("col2", Option(StringValue("value2"))))
			}

			it("should produce a comma-seperated string of column names") {
				record.commaSeparatedColumnNames should equal ("col1, col2")
			}

			it("should produce a comma-seperated string of values") {
				record.commaSeparatedColumnValues should equal ("'value1', 'value2'")
			}

			it("should build an sql insert string") {
				record.insertSql should equal ("INSERT INTO test_table(col1, col2) VALUES('value1', 'value2');")
			}
			
			it("should insert its values as a new record into the table") {
				record.insert

				val map = jdbcTemplate.queryForMap("select * from test_table where col1 = ?", "value1")

				map.get("col1") should equal ("value1")
				map.get("col2") should equal ("value2")
			}
		}
	}

}