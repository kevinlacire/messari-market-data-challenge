package dev.ultimaratio.messari.route;

import com.fasterxml.jackson.core.JsonParseException;
import dev.ultimaratio.messari.bean.RawMarketCalculator;
import dev.ultimaratio.messari.config.AppConfig;
import dev.ultimaratio.messari.pojo.MarketDataMetric;
import dev.ultimaratio.messari.pojo.RawMarketData;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static dev.ultimaratio.messari.Constants.DIRECT_ENDPOINT;

@Component
public class CommonRoute extends RouteBuilder {

    @Autowired
    private AppConfig config;

    @Override
    public void configure() {
        fileReader();
        parser();
        calculator();
        outputCalculations();
    }

    private void fileReader() {
        onException(Exception.class)
            .handled(true);

        // @formatter:off
        from("file:" + config.getFolder() + "?fileName=" + config.getFilename() + "&charset=utf-8&noop=true")
        .routeId("readFile")
        .to(DIRECT_ENDPOINT + "parser");
        // @formatter:on
    }

    private void parser() {
        // @formatter:off
        from(DIRECT_ENDPOINT + "parser")
        .routeId("parser")
        .split(body().tokenize("\n", 1, true)).streaming()
            .doTry()
                .choice()
                    .when(body().isEqualTo("END"))
                        .to(DIRECT_ENDPOINT + "outputCalculations")
                    .otherwise()
                        .unmarshal().json(JsonLibrary.Jackson, RawMarketData.class)
                        .to(DIRECT_ENDPOINT + "calculator")
            .endDoTry()
            .doCatch(JsonParseException.class)
                .stop();
        // @formatter:on
    }

    private void calculator() {
        // @formatter:off
        from(DIRECT_ENDPOINT + "calculator")
        .routeId("calculator")
        .bean(RawMarketCalculator.class, "calculator");
        // @formatter:on
    }

    private void outputCalculations() {
        // @formatter:off
        from(DIRECT_ENDPOINT + "outputCalculations")
        .routeId("outputCalculations")
        .process(exchange -> exchange.getIn().setBody(RawMarketCalculator.marketMetrics.values()))
        .split(body())
            .marshal().json(JsonLibrary.Jackson, MarketDataMetric.class)
            .log("${body}");
        // @formatter:on
    }
}