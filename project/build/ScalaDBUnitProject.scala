import sbt._

class ScalaDBUnitProject(info: ProjectInfo) extends DefaultProject(info) with IdeaProject {

	// Repositories
	val scalaToolsRepository = "Scala-Tools Maven2 Snapshots Repository" at "http://nexus.scala-tools.org/"

	// Libraries
  val scalatest = "org.scalatest" % "scalatest" % "1.2"
	val hsqldb = "org.hsqldb" % "hsqldb" % "2.0.0"
	val mysql = "mysql" % "mysql-connector-java" % "5.1.13"
	val squeryl = "org.squeryl" % "squeryl_2.8.0.RC7" % "0.9.4beta7"

}
