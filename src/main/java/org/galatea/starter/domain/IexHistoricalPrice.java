package org.galatea.starter.domain;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IexHistoricalPrice {

  private String symbol;
  private String date;
  private long volume;
  private BigDecimal close;
  private BigDecimal high;
  private BigDecimal low;
  private BigDecimal open;

}
