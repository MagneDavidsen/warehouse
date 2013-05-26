import com.typesafe.startscript.StartScriptPlugin

seq(StartScriptPlugin.startScriptForClassesSettings: _*)

name :="nbrno"

scalaVersion :="2.9.2"

version :="0.1"

resolvers += "twitter-repo" at "http://maven.twttr.com"

resolvers += "coda" at "http://repo.codahale.com"

resolvers += "sonatype-repo" at "https://oss.sonatype.org"


libraryDependencies ++= Seq("com.twitter" % "finagle-core" % "1.9.0", "com.twitter" % "finagle-http" % "1.9.0", "postgresql" % "postgresql" % "9.1-901.jdbc4")

libraryDependencies += "net.databinder" %% "unfiltered-filter" % "0.6.8"

libraryDependencies += "net.databinder" %% "unfiltered-jetty" % "0.6.8"