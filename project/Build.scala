import sbt._
import Keys._
import com.github.bigtoast.sbtliquibase.LiquibasePlugin._
import scala.util.Properties

object NbrnoBuild extends Build {

  lazy val realenv : String = Properties.envOrElse("ENV", "UNKNOWN")

  lazy val environment = SettingKey[String]("environment", "environment variable")

  lazy val root = Project(id = "nbrno",
    base = file("."),
    settings = Project.defaultSettings ++ liquibaseSettings ++
      Seq(environment := realenv)++

      Seq(liquibaseUsername := Properties.envOrElse("DB_USER", "clocking"),
      liquibasePassword := Properties.envOrElse("DB_PASSWORD", "clocking"),
      liquibaseDriver := "org.postgresql.Driver",
      liquibaseUrl := "jdbc:postgresql://localhost:5432/nbrno",
      liquibaseChangelog := "src/main/migrations/changelog.sql")
  )
}