package dev.ultimaratio.messari.bean;

import dev.ultimaratio.messari.pojo.MarketDataMetric;
import dev.ultimaratio.messari.pojo.RawMarketData;
import org.apache.camel.Handler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RawMarketCalculator {

    public static Map<Integer, MarketDataMetric> marketMetrics = new HashMap<>();

    @Handler
    public void calculator(RawMarketData rawMarketData) {
        var marketMetric = marketMetrics.get(rawMarketData.getMarket());
        if (marketMetric != null) {
            marketMetrics.put(rawMarketData.getMarket(), marketMetric.add(rawMarketData));
        } else {
            marketMetrics.put(rawMarketData.getMarket(), new MarketDataMetric(rawMarketData));
        }
    }

}
