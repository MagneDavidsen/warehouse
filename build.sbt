import com.typesafe.startscript.StartScriptPlugin

import com.github.bigtoast.sbtliquibase.LiquibasePlugin

seq(StartScriptPlugin.startScriptForClassesSettings: _*)

name :="nbrno"

scalaVersion :="2.9.2"

version :="0.1"

resolvers += "twitter-repo" at "http://maven.twttr.com"

libraryDependencies ++= Seq("com.twitter" % "finagle-core" % "1.9.0", "com.twitter" % "finagle-http" % "1.9.0", "postgresql" % "postgresql" % "9.1-901.jdbc4")

seq(LiquibasePlugin.liquibaseSettings: _*)

liquibaseUsername := "clocking"

liquibasePassword := {System.getenv("ENV")}

liquibaseDriver   := "org.postgresql.Driver"

//liquibaseUrl  := {if(sys.env("ENV")=="prod") sys.env("DATABASE_URL") else "jdbc:postgresql://localhost:5432/nbrno"}

liquibaseUrl := ""

liquibaseChangelog := "src/main/migrations/changelog.sql"