package scaladbunit.model.value

case class StringValue(value: String) extends Value {
	override def toString = "'" + value + "'"
}