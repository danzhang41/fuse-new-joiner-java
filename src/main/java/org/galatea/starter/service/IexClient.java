package org.galatea.starter.service;

import feign.Feign;
import java.util.List;
import org.galatea.starter.domain.IexHistoricalPrice;
import org.galatea.starter.domain.IexLastTradedPrice;
import org.galatea.starter.domain.IexSymbol;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * A Feign Declarative REST Client to access endpoints from the Free and Open IEX API to get market
 * data. See https://iextrading.com/developer/docs/
 */
@FeignClient(name = "IEX", url = "${spring.rest.iexBasePath}")
public interface IexClient {


  /**
   * Get a list of all stocks supported by IEX. See https://iextrading.com/developer/docs/#symbols.
   * As of July 2019 this returns almost 9,000 symbols, so maybe don't call it in a loop.
   * @param token token
   * @return a list of all of the stock symbols supported by IEX.
   */
  @GetMapping("/ref-data/symbols?token={token}")
  List<IexSymbol> getAllSymbols(@PathVariable("token") String token);

  /**
   * Get the last traded price for each stock symbol passed in. See https://iextrading.com/developer/docs/#last.
   *
   * @param symbols stock symbols to get last traded price for.
   * @param token token
   * @return a list of the last traded price for each of the symbols passed in.
   */
  @GetMapping("/tops/last?symbols={symbols}&token={token}")
  List<IexLastTradedPrice> getLastTradedPriceForSymbols(@PathVariable("symbols") String[] symbols,
                                                        @PathVariable("token") String token);

 
  /**
   * Get historical prices using symbol and a date.
   *
   * @param symbol stock symbols to get historical data for
   * @param token token
   * @return a list of the historical price for the stock symbol passed in and the date passed in
   */
  @GetMapping("/stock/{symbol}/chart?token={token}")
  List<IexHistoricalPrice> getHistoricalPriceForSymbol(
      @PathVariable("symbol") String symbol,
      @PathVariable("token") String token);

  /**
   * Get historical prices using symbol and a date.
   *
   * @param symbol stock symbols to get historical data for
   * @param range time range
   * @return a list of the historical price for the stock symbol passed in and the date passed in
   */
  @GetMapping("/stock/{symbol}/chart/{range}?token={token}")
  List<IexHistoricalPrice> getHistoricalPriceForRange(
      @PathVariable("symbol") String symbol,
      @PathVariable("range") String range,
      @PathVariable("token") String token);

  /**
   * Get historical prices using symbol and a date.
   *
   * @param symbol stock symbols to get historical data for
   * @param token token
   * @param date specified date to get the price for
   * @return a list of the historical price for the stock symbol passed in and the date passed in
   */
  @GetMapping("/stock/{symbol}/chart/{date}?token={token}")
  List<IexHistoricalPrice> getHistoricalPriceForDate(
      @PathVariable("symbol") String symbol,
      @PathVariable("date") String date,
      @PathVariable("token") String token);
}
