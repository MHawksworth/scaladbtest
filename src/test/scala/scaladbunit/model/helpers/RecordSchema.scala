package scaladbunit.model.helpers

import org.squeryl.Schema

object RecordSchema extends Schema {
	val countries = table[Country]
}