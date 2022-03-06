package dev.ultimaratio.messari.bean;

import dev.ultimaratio.messari.config.AppConfig;
import dev.ultimaratio.messari.pojo.MarketDataMetric;
import dev.ultimaratio.messari.pojo.RawMarketData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.math.MathContext;

@ContextConfiguration(classes = {
    AppConfig.class
})
@SpringBootTest(classes = {
    RawMarketCalculator.class,
})
@EnableAutoConfiguration
@ActiveProfiles("test")
class RawMarketCalculatorTest {

    @Autowired
    private RawMarketCalculator rawMarketCalculator;

    private RawMarketData rawMarketDataBuilder(Integer id, Integer market, long price, long volume, boolean isBuy) {
        var rawMarketData = new RawMarketData();
        rawMarketData.setId(id);
        rawMarketData.setMarket(market);
        rawMarketData.setPrice(new BigDecimal(price));
        rawMarketData.setVolume(new BigDecimal(volume));
        rawMarketData.setIsBuy(isBuy);

        return rawMarketData;
    }

    @Test
    public void add() {
        var rawMarketData1 = rawMarketDataBuilder(1, 1, 1L, 10L, true);
        var rawMarketData2 = rawMarketDataBuilder(2, 1, 2L, 20L, false);

        rawMarketCalculator.calculator(rawMarketData1);
        rawMarketCalculator.calculator(rawMarketData2);

        var expected = new MarketDataMetric(rawMarketData1);
        expected.add(rawMarketData2);
        var actual = RawMarketCalculator.marketMetrics.get(1);

        Assertions.assertTrue(RawMarketCalculator.marketMetrics.containsKey(1));

        Assertions.assertEquals(1, actual.getMarket());
        Assertions.assertEquals(BigDecimal.valueOf(1.5), actual.getMeanPrice());
        Assertions.assertEquals(BigDecimal.valueOf(15), actual.getMeanVolume());
        Assertions.assertEquals(BigDecimal.valueOf(30), actual.getTotalVolume());
        Assertions.assertEquals(new BigDecimal((2D * 20D + 10D) / (20D + 10D), MathContext.DECIMAL32), actual.getVolumeWeightedAveragePrice());
        Assertions.assertEquals(BigDecimal.valueOf(0.5), actual.getPercentageBuy());
    }

}