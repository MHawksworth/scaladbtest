package scaladbtest

import org.springframework.jdbc.core.simple.SimpleJdbcTemplate
import io.Source
import javax.sql.DataSource
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

trait DataSourceSpecSupport extends SpecSupport {
	
	protected val resourceDir = "src/test/resources/"

	protected var dataSource: DataSource = _

	protected var jdbcTemplate: SimpleJdbcTemplate = _

	def getSqlTextFromFile(filename: String) = {
		val sqlStatements =
		for (
			line <- Source.fromFile(filename).getLines
			if !line.startsWith("-")
		) yield line

		sqlStatements.mkString("\n")
	}

	def createTables(filename: String) {
		val sqlText = getSqlTextFromFile(resourceDir + "ddl/" + filename)

		jdbcTemplate.update(sqlText)
	}

	override protected def withFixture(test: NoArgTest) {
		runTestWithDataSourceAndDdl(test, TestContext.hsqldbDataSource, "hsqldb.sql")
	}

	private def runTestWithDataSourceAndDdl(test: NoArgTest, dataSource: DataSource, ddlFilename: String) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource)

		createTables(ddlFilename)

		initializeDataSourceReferences(dataSource)

		test()

		cleanUpTables()
	}

	protected def initializeDataSourceReferences(dataSource: DataSource) {

	}

	private def cleanUpTables() {
		jdbcTemplate.update("delete from two_string_table")
		jdbcTemplate.update("delete from single_id_table")
		jdbcTemplate.update("delete from date_table")
	}
	
}