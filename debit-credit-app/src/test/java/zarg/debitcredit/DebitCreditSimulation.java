package zarg.debitcredit;

import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.core.OpenInjectionStep.atOnceUsers;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class DebitCreditSimulation extends Simulation {

    private static final String lowContentionAccounts = "other_requests.csv";
    private static final String highContentionAccounts = "top_requests.csv";

    private static final int hcUsers = 5;
    private static final int users = 10;
    private static final int repeats = 20;

    public DebitCreditSimulation() {

        FeederBuilder.Batchable<String> creditFeederLow = csv(lowContentionAccounts).shuffle().circular();
        FeederBuilder.Batchable<String> debitFeederLow = csv(lowContentionAccounts).shuffle().circular();
        FeederBuilder.Batchable<String> balanceFeeder = csv(lowContentionAccounts).shuffle().circular();
        FeederBuilder.Batchable<String> highContentionFeeder = csv(highContentionAccounts).shuffle().circular();

        HttpProtocolBuilder httpProtocol = http
                .baseUrl("http://localhost:8080/teller/")
                .contentTypeHeader("application/json")
                .acceptHeader("application/json")
                .userAgentHeader("Gatling");

        ScenarioBuilder highContentionCreditDebit = scenario("Scenario High Contention")
                .feed(highContentionFeeder)
                .repeat(repeats)
                .on(
                        exec(http("High Contention Credit ")
                                .put("${customer_bid}/credit")
                                .body(StringBody("${credit_request}"))
                                .check(status().is(200)))
                                .pause(1)
                                .exec(http("High Contention Debit")
                                        .put("${customer_bid}/${account_bid}/debit")
                                        .body(StringBody("${debit_request}"))
                                        .check(status().is(200))));


        ScenarioBuilder lowContentionCredits = scenario("Scenario Credits")
                .feed(creditFeederLow)
                .repeat(repeats)
                .on(
                        exec(
                                http("Credit")
                                        .put("${customer_bid}/credit")
                                        .body(StringBody("${credit_request}"))
                                        .check(status().is(200))));

        ScenarioBuilder lowContentionDebits = scenario("Scenario Debits")
                .feed(debitFeederLow)
                .repeat(repeats)
                .on(
                        exec(
                                http("Debit")
                                        .put("${customer_bid}/${account_bid}/debit")
                                        .body(StringBody("${debit_request}"))
                                        .check(status().is(200))));

        ScenarioBuilder balance = scenario("Scenario Balances")
                .feed(balanceFeeder)
                .repeat(repeats)
                .on(
                        exec(
                                http("Balance")
                                        .get("${customer_bid}/${account_bid}/balance")
                                        .check(status().is(200))));


        setUp(
                lowContentionCredits.injectOpen(atOnceUsers(users)),
                lowContentionDebits.injectOpen(atOnceUsers(users)),
                balance.injectOpen(atOnceUsers(users)),
                highContentionCreditDebit.injectOpen(atOnceUsers(hcUsers)))
                .protocols(httpProtocol);
    }
}
