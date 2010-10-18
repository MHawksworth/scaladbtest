package scaladbunit.model

import value.Value

case class Column(name: String, value: Option[Value] = None)