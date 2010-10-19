package scaladbtest.model.value

import java.util.Date
import java.text.SimpleDateFormat
import scaladbtest.model.Record

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
			text.trim match {
				case "$now" => Value.now()
				case "$null" => Value.none()
				case "$label" => {
					if(record == null) throw new IllegalArgumentException("The record argument must not be null")
					Value.string(record.label)
				}
				case _ => Value.string(text)
			}
		}
	}

}

case class Value(value: Option[String] = None) {

	def sqlValue: String = {
		value match {
			case Some(text) => "'" + text + "'"
			case None => "NULL"
		}
	}

}