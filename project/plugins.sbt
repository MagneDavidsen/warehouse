resolvers += Classpaths.typesafeResolver

resolvers += "magnedavidsen-github" at "http://bigtoast.github.com/repo/"

resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.0.0")

addSbtPlugin("com.typesafe.startscript" % "xsbt-start-script-plugin" % "0.5.3")

addSbtPlugin("com.github.bigtoast" % "sbt-liquibase" % "0.5")