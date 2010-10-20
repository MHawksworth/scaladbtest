package scaladbtest.model

import scaladbtest.model.value.Value

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

object DefaultColumn {

	def apply(name: String, value: Value, table: Option[Table] = None) =
		new DefaultColumn(name, value, table)

}

class DefaultColumn(val name: String, val value: Value, var table: Option[Table] = None) {

	def create(record: Option[Record] = None): Column = {
		new Column(name, value, record) 
	}

	override def toString = "DefaultColumn(" + name + "," + value + ")"

	override def equals(other: Any): Boolean = {
		other match {
			case that: DefaultColumn =>
				name == that.name &&
				value == that.value &&
				table == that.table
			case _ => false
		}
	}

	override def hashCode: Int = {
		41 * (
			41 * (
				41 + name.hashCode
			) + value.hashCode
		) + table.hashCode
	}

}