import sbt._
import scala.util.Properties

object NbrnoBuild extends Build {

  lazy val realenv : String = Properties.envOrElse("ENV", "UNKNOWN")

  lazy val environment = SettingKey[String]("environment", "environment variable")

  lazy val root = Project(id = "nbrno",
    base = file("."),
    settings = Project.defaultSettings ++
      Seq(environment := realenv)
  )
}