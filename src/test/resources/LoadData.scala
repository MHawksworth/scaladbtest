import scaladbunit.model.{Record, Table}

object LoadData {

	def table(name: Symbol) = {
		new Table(name, Set(), Set())
	}

	def record(name: Symbol) = {
		new Record(this, name, Set())
	}

	/*
	table `stop_list
		record (`myStopList)
	  column (`words -> "the and in")

	table `country
			record `Canada
			columns {
					`name -> label
			}

			record `UnitedStates {
					`name -> "United States"
			}
	}

	table `province {
			default {
					`country -> `Canada
			}

			record `Alberta {
					name -> label
			}

			record `NewYork {
					name -> "New York"
					country -> `UnitedStates
			}
	}

	table `user_account {
			record `Vincent {
					username -> vincent
					password -> `username
			}
	}*/
}