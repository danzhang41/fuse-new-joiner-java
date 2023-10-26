package org.galatea.starter.entrypoint;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Collections;
import junitparams.JUnitParamsRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


@RequiredArgsConstructor
@Slf4j
// We need to do a full application start up for this one, since we want the feign clients to be instantiated.
// It's possible we could do a narrower slice of beans, but it wouldn't save that much test run time.
@SpringBootTest
// this gives us the MockMvc variable
@AutoConfigureMockMvc
// we previously used WireMockClassRule for consistency with ASpringTest, but when moving to a dynamic port
// to prevent test failures in concurrent builds, the wiremock server was created too late and feign was
// already expecting it to be running somewhere else, resulting in a connection refused
@AutoConfigureWireMock(port = 0, files = "classpath:/wiremock")
// Use this runner since we want to parameterize certain tests.
// See runner's javadoc for more usage.
@RunWith(JUnitParamsRunner.class)
public class IexRestControllerTest extends ASpringTest {

  @Autowired
  private MockMvc mvc;

  @Test
  public void testGetSymbolsEndpoint() throws Exception {
    MvcResult result = this.mvc.perform(
        // note that we were are testing the fuse REST end point here, not the IEX end point.
        // the fuse end point in turn calls the IEX end point, which is WireMocked for this test.

        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/iex/symbols?token=pk_b13d22c163814587aee834dbc8a768e9")
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        // some simple validations, in practice I would expect these to be much more comprehensive.
        .andExpect(jsonPath("$[0].symbol", is("A")))
        .andExpect(jsonPath("$[1].symbol", is("AA")))
        .andExpect(jsonPath("$[2].symbol", is("AAAU")))
        .andReturn();
  }

  @Test
  public void testGetLastTradedPrice() throws Exception {

    MvcResult result = this.mvc.perform(
        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
            //  /tops/last?symbols=FB&token=pk_b13d22c163814587aee834dbc8a768e9
            .get("/iex/lastTradedPrice?symbols=FB&token=pk_b13d22c163814587aee834dbc8a768e9")
            // This URL will be hit by the MockMvc client. The result is configured in the file
            // src/test/resources/wiremock/mappings/mapping-lastTradedPrice.json
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].symbol", is("FB")))
        .andExpect(jsonPath("$[0].price").value(new BigDecimal("186.34")))
        .andReturn();
  }

  @Test
  public void testGetLastTradedPriceEmpty() throws Exception {

    MvcResult result = this.mvc.perform(
        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
            .get("/iex/lastTradedPrice?symbols=&token=pk_b13d22c163814587aee834dbc8a768e9")
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is(Collections.emptyList())))
        .andReturn();
  }
  @Test
  public void testGetHistoricalPriceForSymbol() throws Exception{


    MvcResult result = this.mvc.perform(
        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
            .get("/iex/historicalPrice/?symbol=AAPL&token=pk_b13d22c163814587aee834dbc8a768e9")
            .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].symbol", is("AAPL")))
        .andExpect(jsonPath("$[0].date", is("2022-05-24")))
        .andExpect(jsonPath("$[0].open", is(140.805)))
        .andExpect(jsonPath("$[0].close", is(140.36)))
        .andExpect(jsonPath("$[0].high", is(141.97)))
        .andExpect(jsonPath("$[0].low", is(137.33)))
        .andExpect(jsonPath("$[0].volume", is(104132746)))
        .andExpect(jsonPath("$[1].symbol", is("AAPL")))
        .andExpect(jsonPath("$[1].low", is(138.34)))
        .andExpect(jsonPath("$[2].close", is(143.78)))
        .andReturn();

  }
  @Test
  public void testGetHistoricalPriceForDate() throws Exception{

    MvcResult result = this.mvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .get("/iex/historicalPrice/?symbol=AAPL&date=20220104&token=pk_b13d22c163814587aee834dbc8a768e9")
                .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].symbol", is("AAPL")))
        .andExpect(jsonPath("$[0].date", is("2022-01-04")))
        .andExpect(jsonPath("$[0].open", is(182.655)))
        .andExpect(jsonPath("$[0].close", is(182.53)))
        .andExpect(jsonPath("$[0].high", is(182.88)))
        .andExpect(jsonPath("$[0].low", is(182.3)))
        .andExpect(jsonPath("$[0].volume", is(51459)))
        .andExpect(jsonPath("$[1].volume", is(19473)))
        .andExpect(jsonPath("$[2].volume", is(9688)))
        .andExpect(jsonPath("$[3].volume", is(16268)))

        .andReturn();
  }

  @Test
  public void testGetHistoricalPriceForRange() throws Exception{
    MvcResult result = this.mvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .get("/iex/historicalPrice/?symbol=AAPL&range=6m&token=pk_b13d22c163814587aee834dbc8a768e9")
                .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].symbol", is("AAPL")))
        .andExpect(jsonPath("$[0].date", is("2021-12-27")))
        .andExpect(jsonPath("$[0].open", is(177.085)))
        .andExpect(jsonPath("$[0].close", is(180.33)))
        .andExpect(jsonPath("$[0].high", is(180.42)))
        .andExpect(jsonPath("$[0].low", is(177.07)))
        .andExpect(jsonPath("$[0].volume", is(74919582)))
        .andExpect(jsonPath("$[1].volume", is(79144339)))
        .andExpect(jsonPath("$[2].volume", is(62348931)))
        .andExpect(jsonPath("$[3].volume", is(59773014)))

        .andReturn();
  }
  @Test
  public void testGetHistoricalEmpty() throws Exception {
    MvcResult result = this.mvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .get("/iex/historicalPrice/?range=6m&token=pk_b13d22c163814587aee834dbc8a768e9")
                .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest())
        .andReturn();
  }
}
