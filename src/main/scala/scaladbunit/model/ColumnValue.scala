package scaladbunit.model

import scaladbunit.schema.Column
import value.Value

case class ColumnValue(column: Column, value: Option[Value] = None)