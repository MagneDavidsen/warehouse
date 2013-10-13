import sbt._
import Keys._
import com.github.bigtoast.sbtliquibase.LiquibasePlugin._
import scala.util.Properties

object NbrnoBuild extends Build {

  lazy val realenv : String = Properties.envOrElse("ENV", "UNKNOWN")

  lazy val environment = SettingKey[String]("environment", "environment variable")

}