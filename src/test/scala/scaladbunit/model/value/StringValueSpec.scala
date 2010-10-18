package scaladbunit.model.value

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

import scaladbunit.SpecSupport

class StringValueSpec extends SpecSupport {

	describe("A String Value") {
		describe("When is not empty") {
			val value = new StringValue(Some("value1"))

			it("should return the value with single-quotes when asked for it's sql value")	{
				value.sqlValue should equal ("'value1'")
			}
		}

		describe("When None") {
			val value = new StringValue(None)

			it("should return the value with single-quotes when asked for it's sql value")	{
				value.sqlValue should equal ("NULL")
			}
		}
	}

}