import sbt._
import Keys._
import com.github.bigtoast.sbtliquibase.LiquibasePlugin._

object NbrnoBuild extends Build {

  val realenv : String = System.getenv("ENV")

  val environment = SettingKey[String]("environment", "environment variable")

  lazy val root = Project(id = "nbrno",
    base = file("."),
    settings = Project.defaultSettings ++ liquibaseSettings ++
      Seq(environment := realenv)++

      Seq(liquibaseUsername := "clocking",
      liquibasePassword := "clocking",
      liquibaseDriver := "org.postgresql.Driver",
      liquibaseUrl := "jdbc:postgresql://localhost:5432/nbrno",
      liquibaseChangelog := "src/main/migrations/changelog.sql")
  )
}