package dev.ultimaratio.messari.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RawMarketData {

    public Integer id;

    public Integer market;

    public BigDecimal price;

    public BigDecimal volume;

    @JsonProperty("is_buy")
    public Boolean isBuy;
}