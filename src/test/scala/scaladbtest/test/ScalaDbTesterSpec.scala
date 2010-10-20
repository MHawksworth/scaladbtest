package scaladbtest.test

import scaladbtest.DataSourceSpecSupport
import org.scalatest.PrivateMethodTester

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

class ScalaDbTesterSpec extends DataSourceSpecSupport with PrivateMethodTester {

	createTables("hsqldb.sql")

	override protected def beforeEach() {
		jdbcTemplate.update("delete from two_string_table")
	}

	describe("Scala DB Tester") {
		describe("with a base directory") {
			val tester = new ScalaDbTester(dataSource, Some("src/test/resources/dsl"))

			it("should add missing slash to the path if it's missing") {
				val addMissingSlash = PrivateMethod[String]('addMissingSlash)

				tester invokePrivate addMissingSlash("some/path") should equal ("some/path/")
			}

			it("should not add missing slash to the path if it's present") {
				val addMissingSlash = PrivateMethod[String]('addMissingSlash)

				tester invokePrivate addMissingSlash("some/path/") should equal ("some/path/")
			}

			it("should transform a list of files to add base path and any missing slashes") {
				val absoluteFilenames = PrivateMethod[Traversable[String]]('absoluteFilenames)

				tester invokePrivate absoluteFilenames(List("file1.dbt", "file2.dbt")) should equal (
					List("src/test/resources/dsl/file1.dbt", "src/test/resources/dsl/file2.dbt"))
			}

			it("should insert and delete all data before test") {
			  tester.onBefore("two_string_table.dbt")

				jdbcTemplate.queryForInt("select count(*) from two_string_table") should equal (2)

				tester.onAfter()

				jdbcTemplate.queryForInt("select count(*) from two_string_table") should equal (0)
			}

			it("should insert all data from a java collection") {
				val list: java.util.Collection[String] = new java.util.ArrayList[String]()
				list.add("two_string_table.dbt")
				tester.onBefore(list)

				jdbcTemplate.queryForInt("select count(*) from two_string_table") should equal (2)
			}
		}
	}

}