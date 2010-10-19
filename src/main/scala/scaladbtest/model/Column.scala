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

import value.Value

object Column {
	def apply(name: String, value: Value, record: Option[Record] = None) =
		new Column(name, value, record)
}

final class Column(val name: String, val value: Value, var record: Option[Record] = None) {

	value.column = Some(this)

	override def toString = "Column(" + name + "," + value + ")"

	override def equals(other: Any): Boolean = {
		other match {
			case that: Column =>
				name == that.name &&
				value == that.value &&
				record == that.record
			case _ => false
		}
	}

	override def hashCode: Int = {
		41 * (
			41 * (
				41 + name.hashCode
			) + value.hashCode
		) + record.hashCode
	}

}