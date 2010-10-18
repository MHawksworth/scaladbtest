package scaladbunit.model.value

import scaladbunit.SpecSupport
import java.util.{GregorianCalendar, Date}
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

class TodayValueSpec extends SpecSupport {

	describe("A Today Value") {
		it("should format a date and time") {
			val date = new GregorianCalendar(2010, 4, 15, 8, 30, 20).getTime

			TodayValue.formatDate(date) should equal ("2010-05-15 08:30:20")
		}

		it("should return a sql value of its internal date wrapped in single quotes") {
			TodayValue.sqlValue should equal ("'" + TodayValue.formatDate(new Date()) + "'")
		}
	}

}