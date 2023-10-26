package org.galatea.starter.service;

import java.util.Collections;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.IexHistoricalPrice;
import org.galatea.starter.domain.IexLastTradedPrice;
import org.galatea.starter.domain.IexSymbol;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * A layer for transformation, aggregation, and business required when retrieving data from IEX.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IexService {

  @NonNull
  private IexClient iexClient;


  /**
   * Get all stock symbols from IEX.
   *
   * @param token token
   * @return a list of all Stock Symbols from IEX.
   */
  public List<IexSymbol> getAllSymbols(@RequestParam("token") final String token) {
    return iexClient.getAllSymbols(token);
  }

  /**
   * Get the last traded price for each Symbol that is passed in.
   *
   * @param symbols the list of symbols to get a last traded price for.
   * @param token token
   * @return a list of last traded price objects for each Symbol that is passed in.
   */
  public List<IexLastTradedPrice> getLastTradedPriceForSymbols(final List<String> symbols,
                                                               final String token) {
    if (CollectionUtils.isEmpty(symbols)) {
      return Collections.emptyList();
    } else {
      return iexClient.getLastTradedPriceForSymbols(symbols.toArray(new String[0]), token);
    }
  }

  /**
   * Get historical prices.
   *
   * @param symbol the list of symbols to get
   * @param range the range to get
   * @param date the date to get
   *
   * @return a list of last traded price objects for each Symbol that is passed in.
   */

  public List<IexHistoricalPrice> getHistoricalPrice(
      final String symbol, final String range, final String date, final String token) {

    if(symbol == null) {
      return Collections.emptyList();
    } else if(range != null) {
      return iexClient.getHistoricalPriceForRange(symbol, range, token);
    } else if(date != null) {
      return iexClient.getHistoricalPriceForDate(symbol, date, token);
    } else {
      return iexClient.getHistoricalPriceForSymbol(symbol, token);
    }
  }

}
