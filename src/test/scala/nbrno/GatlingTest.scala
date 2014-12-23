//package nbrno
//
//import io.gatling.core.Predef._
//import io.gatling.http.Predef._
//import scala.concurrent.duration._
//import io.gatling.core.controller.inject.AtOnceInjection
//import io.gatling.core.config.GatlingPropertiesBuilder
//import scala.reflect.io.File
//import io.gatling.app.Gatling
//
//class GatlingTest extends Simulation{
//
//  val httpProtocol = http.baseURL("http://nbrno.herokuapp.com").disableFollowRedirect
//
//  setUp(CreateScenario.scn(duration = 2 minutes).inject(AtOnceInjection(10)))
//    .protocols(httpProtocol)
//}
//
//object CreateScenario {
//  def scn(name: String = "Get rappers", duration: Duration) =
//    scenario(name)
//      .during(duration) {
//      exec(
//        http("api/rappers")
//          .get("/api/rappers")
//          .check(status.is(200)))
//    }
//}
//
//object IDEPathHelper {
//  val gatlingConfUrl = getClass.getClassLoader.getResource("gatling.conf").getPath
//  val projectRootDir = File(gatlingConfUrl).parents(2)
//
//  val mavenTargetDirectory = projectRootDir / "target"
//  val mavenBinariesDirectory = mavenTargetDirectory / "test-classes"
//  val resultsDirectory = mavenTargetDirectory / "results"
//}
//
//object Engine extends App {
//  val props = new GatlingPropertiesBuilder
//  props.resultsDirectory(IDEPathHelper.resultsDirectory.toString)
//  props.binariesDirectory(IDEPathHelper.mavenBinariesDirectory.toString)
//  props.simulationClass("nbrno.GatlingTest")
//
//  Gatling.fromMap(props.build)
//}