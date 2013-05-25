import com.typesafe.startscript.StartScriptPlugin

seq(StartScriptPlugin.startScriptForClassesSettings: _*)

name :="nbrno"

scalaVersion :="2.10.1"

version :="0.1"

resolvers += "twitter-repo" at "http://maven.twttr.com"

resolvers += "coda" at "http://repo.codahale.com"

resolvers += "sonatype-repo" at "https://oss.sonatype.org"

resolvers += "maven-repo" at "http://search.maven.org"


libraryDependencies ++= Seq("com.twitter" % "finagle-core" % "1.9.0", "com.twitter" % "finagle-http" % "1.9.0", "postgresql" % "postgresql" % "9.1-901.jdbc4")

libraryDependencies += "net.databinder" %% "unfiltered-filter" % "0.6.8"

libraryDependencies += "net.databinder.dispatch" %% "dispatch-core" % "0.9.5"

libraryDependencies += "net.databinder.dispatch" %% "json4s-native" % "0.9.5"

libraryDependencies += "net.databinder" %% "unfiltered-jetty" % "0.6.8"

libraryDependencies ++= List(
  // use the right Slick version here:
  "com.typesafe.slick" %% "slick" % "1.0.0",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.h2database" % "h2" % "1.3.166"
)