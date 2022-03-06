package dev.ultimaratio.messari.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.math.MathContext;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketDataMetric {

    private Integer market;

    // Internals
    @JsonIgnore
    private BigDecimal nbEntries;

    @JsonIgnore
    private BigDecimal nbBuys;

    @JsonIgnore
    private BigDecimal weightedVolume;

    private BigDecimal sumOfTrasactionsPrice;

    // Stats
    @JsonProperty("total_volume")
    private BigDecimal totalVolume;

    @JsonProperty("mean_price")
    private BigDecimal meanPrice;

    @JsonProperty("mean_volume")
    private BigDecimal meanVolume;

    @JsonProperty("volume_weighted_average_price")
    private BigDecimal volumeWeightedAveragePrice;

    @JsonProperty("percentage_buy")
    private BigDecimal percentageBuy;

    public MarketDataMetric(RawMarketData rawMarketData) {
        this.setMarket(rawMarketData.getMarket());

        // Counters
        this.setNbEntries(new BigDecimal(1));
        this.setTotalVolume(rawMarketData.getVolume());
        this.setWeightedVolume(rawMarketData.getVolume().multiply(rawMarketData.getPrice()));
        this.setSumOfTrasactionsPrice(rawMarketData.getPrice());

        // Metrics
        this.setMeanPrice(this.getSumOfTrasactionsPrice().divide(this.getNbEntries(), MathContext.DECIMAL32));
        this.setMeanVolume(this.getTotalVolume().divide(this.getNbEntries(), MathContext.DECIMAL32));
        this.setVolumeWeightedAveragePrice(this.getWeightedVolume().divide(this.getTotalVolume(), MathContext.DECIMAL32));

        if (rawMarketData.getIsBuy()) {
            this.setNbBuys(new BigDecimal(1));
        } else {
            this.setNbBuys(new BigDecimal(0));
        }
        this.setPercentageBuy(this.getNbBuys().divide(this.getNbEntries(), MathContext.DECIMAL32));
    }

    public MarketDataMetric add(RawMarketData rawMarketData) {
        this.setNbEntries(this.getNbEntries().add(new BigDecimal(1)));
        this.setTotalVolume(this.getTotalVolume().add(rawMarketData.getVolume()));
        this.setWeightedVolume(this.getWeightedVolume().add(rawMarketData.getVolume().multiply(rawMarketData.getPrice())));
        this.setSumOfTrasactionsPrice(this.getSumOfTrasactionsPrice().add(rawMarketData.getPrice()));

        this.setMeanPrice(this.getSumOfTrasactionsPrice().divide(this.getNbEntries(), MathContext.DECIMAL32));
        this.setMeanVolume(this.getTotalVolume().divide(getNbEntries(), MathContext.DECIMAL32));
        this.setVolumeWeightedAveragePrice(this.getWeightedVolume().divide(this.getTotalVolume(), MathContext.DECIMAL32));

        if (rawMarketData.getIsBuy()) {
            this.setNbBuys(this.getNbBuys().add(new BigDecimal(1)));
        }
        this.setPercentageBuy(this.getNbBuys().divide(this.getNbEntries(), MathContext.DECIMAL32));

        return this;
    }
}