package scaladbtest.test

import javax.sql.DataSource
import scaladbtest.model.TestData

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

class ScalaDbTester(
	val dataSource: DataSource,
	val basePath: Option[String] = None,
	var disableForeignKeys: Boolean = true
) {

	val testData = new TestData(dataSource)

	def absoluteFilenames(filenames: String*) = {
		filenames.map((filename: String) =>
			if(basePath.isDefined) basePath + filename
			else filename
		)
	}

	def onBefore(filenames: String*) {
		//testData.loadFrom(absoluteFilenames(filenames))
		testData.insertAll()
	}

	def onAfter() {
		testData.deleteAll()
	}
}