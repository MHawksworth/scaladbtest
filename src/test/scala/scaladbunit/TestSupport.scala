package scaladbunit

import org.scalatest.matchers.ShouldMatchers
import com.mchange.v2.c3p0.ComboPooledDataSource
import org.scalatest.{OneInstancePerTest, Spec}
import org.springframework.jdbc.datasource.DataSourceUtils
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate
import io.Source

abstract class TestSupport extends Spec
	with ShouldMatchers
	with OneInstancePerTest
{
	protected val dataSource = new ComboPooledDataSource()
	dataSource.setDriverClass("org.hsqldb.jdbc.JDBCDriver")
	dataSource.setJdbcUrl("jdbc:hsqldb:mem:scaladbunit")
	dataSource.setUser("sa")
	dataSource.setPassword("")

	protected val jdbcTemplate = new SimpleJdbcTemplate(dataSource)

	def getSqlTextFromFile(filename: String) = {
		val sqlStatements =
		for (
			line <- Source.fromFile(filename).getLines
			if !line.startsWith("-")
		) yield line

		sqlStatements.mkString("\n")
	}

	def createTables(filename: String) {
		val sqlText = getSqlTextFromFile("src/test/resources/ddl/" + filename)

		jdbcTemplate.update(sqlText)
	}

}