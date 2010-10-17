package scaladbunit.schema

import scaladbunit.model.ColumnValue

case class Table(name: String, columns: Set[Column], defaultValues: Set[ColumnValue])