package scaladbunit.model.value

import scaladbunit.TestSupport

class StringValueSpec extends TestSupport {

	describe("A String Value") {
		val value = new StringValue("value1")

		it("should single-quote the string when asked for its string representation")	{
			value.toString should equal ("'value1'")
		}
	}

}