package scaladbunit.model.helpers

class Country(val country_id: Long, val name: String) {
	def this() = this(0, "")
}