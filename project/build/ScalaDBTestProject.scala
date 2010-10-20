import sbt._

class ScalaDBTestProject(info: ProjectInfo) extends DefaultProject(info) with IdeaProject {

	// Repositories
	val scalaToolsRepository = "Scala-Tools Maven2 Snapshots Repository" at "http://nexus.scala-tools.org/"

	// Libraries
  val scalatest = "org.scalatest" % "scalatest" % "1.2"
	val hsqldb = "org.hsqldb" % "hsqldb" % "2.0.0"
	val mysql = "mysql" % "mysql-connector-java" % "5.1.13"
	val c3po = "c3p0" % "c3p0" % "0.9.1.2"
	val springJdbc = "org.springframework" % "spring-jdbc" % "3.0.3.RELEASE"
	val scalaj_collection = "org.scalaj" %% "scalaj-collection" % "1.0"

}
