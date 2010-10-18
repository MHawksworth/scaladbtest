package scaladbunit

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{OneInstancePerTest, Spec}

abstract class TestSupport extends Spec
	with ShouldMatchers
	with OneInstancePerTest