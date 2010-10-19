package scaladbtest.builder

import util.parsing.combinator.JavaTokenParsers
import java.io.FileReader
import scaladbtest.model.value.Value
import scaladbtest.model.{Record, Column, Table, TestData}
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

object TestDataResource {
	def removeQuotes(stringLiteral: String) = {
		stringLiteral.substring(1, stringLiteral.length - 1)
	}

}

class TestDataResource(val testData: TestData) extends JavaTokenParsers {

	import TestDataResource._

	def tables: Parser[Any] = rep(table)

	def table: Parser[Table] = ident ~ ":" ~ rep(record) ^^ {
		case t ~ ":" ~ records => {
			testData.createOrMergeTable(t, List(), records)
		}
	}

	def default: Parser[Any] = "default:" ~ repsep(column, ",")

	def record: Parser[Record] = optionalRecordLabel ~ opt("->") ~ repsep(column, ",") ^^ {
		case label ~ arrow ~ columns => new Record(label, columns)
	}

	def optionalRecordLabel: Parser[Option[String]] =
		"-" ^^ (x => None) |
		"record:" ~ ident ^^ { case "record:" ~ label => Some(label) }

	def column: Parser[Column] = ident ~ ":" ~ value ^^ { 
		case c ~ ":" ~ v => Column(c.toString, Value.parse(v))
	}

	def value: Parser[String] =
		stringLiteral ^^ (removeQuotes(_)) |
		floatingPointNumber |
		"true" |
		"false" |
		"$label" |
		"$now" |
		"$null" |
		"null" ^^ ("$" + _)

	def loadFrom(filename: String) {
		println(parse(tables, new FileReader(filename)))
	}

}