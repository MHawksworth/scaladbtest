package scaladbunit.model

import scaladbunit.schema.Table

case class Record(table: Table, label: Symbol, values: Set[ColumnValue])