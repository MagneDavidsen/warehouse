resolvers += Classpaths.typesafeResolver

resolvers += "magnedavidsen-github" at "http://bigtoast.github.com/repo/"

addSbtPlugin("com.typesafe.startscript" % "xsbt-start-script-plugin" % "0.5.3")

addSbtPlugin("com.github.bigtoast" % "sbt-liquibase" % "0.5")