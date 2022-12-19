import io.gatling.core.Predef._
import io.gatling.core.feeder.BatchableFeederBuilder
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._

class BalanceEnquirySimulation extends Simulation {

  object BalanceEnquiry {

    val feeder: BatchableFeederBuilder[String]#F = csv("accounts-100.csv").random

    val balance: ChainBuilder = feed(feeder)
      .exec(
        http("Balance")
          .get("${bid}" + s"/balance")
          check status.is(200))
  }

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("http://localhost:8080/teller/")
    .userAgentHeader("Gatling")

  val balances: ScenarioBuilder = scenario("Balances")
    .exec(BalanceEnquiry.balance)

  setUp(balances
    .inject(heavisideUsers(1000) during (20 seconds)))
    .protocols(httpProtocol)
}
