package scaladbunit

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

import org.springframework.jdbc.core.simple.SimpleJdbcTemplate
import io.Source

abstract class DataSourceSpecSupport extends SpecSupport
{
	protected val resourceDir = "src/test/resources/"

	protected val dataSource = TestContext.dataSource

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
		val sqlText = getSqlTextFromFile(resourceDir + "ddl/" + filename)

		jdbcTemplate.update(sqlText)
	}

}