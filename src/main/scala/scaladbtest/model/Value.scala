package scaladbtest.model.value

import java.util.Date
import java.text.SimpleDateFormat
import scaladbtest.model.{Column, Record}
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

object Value {

	def apply(text: Option[String] = None, column: Option[Column] = None) =
		new Value(text, column)

	def string(value: String) = {
		value match {
			case null => new Value(None)
	    case _ => new Value(Some(value))
		}
	}

	def formatDate(date: java.util.Date): String = {
		new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date)
	}

	def now() = new Value(Some(formatDate(new Date())))

	def none() = new Value(None)

	def parse(text: String, record: Record = null) = {
		if(text == null) Value.none()
		else {
			text.trim.toLowerCase match {
				case "$now" => Value.now()
				case "$null" => Value.none()
				case _ => Value.string(text)
			}
		}
	}

}

class Value(val text: Option[String] = None, var column: Option[Column] = None) {

	private def processText(text: String): String = {
		text.toLowerCase match {
			case "$label" => {
				if(column.isEmpty)
					throw new IllegalStateException("Column must be defined for value: " + this)
				if(column.get.record.isEmpty) 
					throw new IllegalStateException("Record must be defined for column: " + column.get)

				column.get.record.get.label
			}
			case _ => text
		}
	}

	def sqlValue: String = {
		text match {
			case Some(text) => "'" + processText(text) + "'"
			case None => "NULL"
		}
	}

	override def toString = "Value(" + text + ")"

	override def equals(other: Any): Boolean = {
		other match {
			case that: Value =>
				text == that.text 
			case _ => false
		}
	}

	override def hashCode: Int = {
		41 + text.hashCode
	}

}