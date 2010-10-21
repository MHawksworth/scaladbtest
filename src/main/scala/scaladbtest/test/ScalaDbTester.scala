package scaladbtest.test

import javax.sql.DataSource
import scaladbtest.model.TestData
import scalaj.collection.Imports._

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
	var basePath: Option[String] = None,
	var disableForeignKeys: Boolean = true
) {

	def this(dataSource: DataSource, basePath: String) =
		this(dataSource, Some(basePath))
	
	val testData = new TestData(dataSource)

	def onBefore(filename: String) {
		onBefore(List(filename))
	}

	def onBefore(filenames: java.util.Collection[String]) {
		onBefore(filenames.asScala)
	}

	def onBefore(filenames: Traversable[String]) {
		testData.load(absoluteFilenames(filenames))
		testData.insertAll()
	}

	def onAfter() {
		testData.deleteAll()
	}

	private def addMissingSlash(basePath: String): String = {
		if(basePath.endsWith("/")) basePath
		else basePath + "/"
	}

	private def absoluteFilenames(filenames: Traversable[String]) = {
		filenames.map((filename: String) =>
			if(basePath.isDefined) addMissingSlash(basePath.get) + filename
			else filename
		)
	}
}