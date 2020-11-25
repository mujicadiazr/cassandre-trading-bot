package tech.cassandre.trading.bot.test.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.domain.Trade;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USD;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

@SpringBootTest
@DisplayName("Domain - Trade")
@Configuration({
        @Property(key = "spring.datasource.data", value = "classpath:/backup.sql")
})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("schedule-disabled")
public class TradeTest extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private TradeFlux tradeFlux;

    @Test
    @DisplayName("Check load trade from database")
    public void checkLoadTradeFromDatabase() {
        // =============================================================================================================
        // Check that positions, orders and trades in database doesn't trigger strategy events.
        assertTrue(strategy.getPositionsUpdateReceived().isEmpty());
        assertTrue(strategy.getTradesUpdateReceived().isEmpty());
        assertTrue(strategy.getOrdersUpdateReceived().isEmpty());

        // Check trade 01.
        TradeDTO trade = strategy.getTrades().get("BACKUP_TRADE_01");
        assertNotNull(trade);
        assertEquals("BACKUP_TRADE_01", trade.getId());
        assertEquals("BACKUP_OPENING_ORDER_02", trade.getOrderId());
        assertEquals(BID, trade.getType());
        assertEquals(0, new BigDecimal("20").compareTo(trade.getOriginalAmount()));
        assertEquals(new CurrencyPairDTO(BTC, USDT), trade.getCurrencyPair());
        assertEquals(0, new BigDecimal("10").compareTo(trade.getPrice()));
        assertEquals(createZonedDateTime("01-08-2020"), trade.getTimestamp());
        assertEquals(0, new BigDecimal("1").compareTo(trade.getFee().getValue()));
        assertEquals(USDT, trade.getFee().getCurrency());
        // Check trade 02.
        trade = strategy.getTrades().get("BACKUP_TRADE_02");
        assertNotNull(trade);
        assertEquals("BACKUP_TRADE_02", trade.getId());
        assertEquals("BACKUP_OPENING_ORDER_03", trade.getOrderId());
        assertEquals(BID, trade.getType());
        assertEquals(0, new BigDecimal("30").compareTo(trade.getOriginalAmount()));
        assertEquals(new CurrencyPairDTO(BTC, USDT), trade.getCurrencyPair());
        assertEquals(0, new BigDecimal("20").compareTo(trade.getPrice()));
        assertEquals(createZonedDateTime("02-08-2020"), trade.getTimestamp());
        assertEquals(0, new BigDecimal("2").compareTo(trade.getFee().getValue()));
        assertEquals(USDT, trade.getFee().getCurrency());
        // Check trade 03.
        trade = strategy.getTrades().get("BACKUP_TRADE_03");
        assertNotNull(trade);
        assertEquals("BACKUP_TRADE_03", trade.getId());
        assertEquals("BACKUP_OPENING_ORDER_04", trade.getOrderId());
        assertEquals(BID, trade.getType());
        assertEquals(0, new BigDecimal("40").compareTo(trade.getOriginalAmount()));
        assertEquals(new CurrencyPairDTO(BTC, USDT), trade.getCurrencyPair());
        assertEquals(0, new BigDecimal("30").compareTo(trade.getPrice()));
        assertEquals(createZonedDateTime("03-08-2020"), trade.getTimestamp());
        assertEquals(0, new BigDecimal("3").compareTo(trade.getFee().getValue()));
        assertEquals(USDT, trade.getFee().getCurrency());
        // Check trade 04.
        trade = strategy.getTrades().get("BACKUP_TRADE_04");
        assertNotNull(trade);
        assertEquals("BACKUP_TRADE_04", trade.getId());
        assertEquals("BACKUP_CLOSING_ORDER_01", trade.getOrderId());
        assertEquals(ASK, trade.getType());
        assertEquals(0, new BigDecimal("40").compareTo(trade.getOriginalAmount()));
        assertEquals(new CurrencyPairDTO(BTC, USDT), trade.getCurrencyPair());
        assertEquals(0, new BigDecimal("40").compareTo(trade.getPrice()));
        assertEquals(createZonedDateTime("04-08-2020"), trade.getTimestamp());
        assertEquals(0, new BigDecimal("4").compareTo(trade.getFee().getValue()));
        assertEquals(USDT, trade.getFee().getCurrency());
        // Check trade 05.
        trade = strategy.getTrades().get("BACKUP_TRADE_05");
        assertNotNull(trade);
        assertEquals("BACKUP_TRADE_05", trade.getId());
        assertEquals("BACKUP_CLOSING_ORDER_02", trade.getOrderId());
        assertEquals(ASK, trade.getType());
        assertEquals(0, new BigDecimal("50").compareTo(trade.getOriginalAmount()));
        assertEquals(new CurrencyPairDTO(ETH, USD), trade.getCurrencyPair());
        assertEquals(0, new BigDecimal("50").compareTo(trade.getPrice()));
        assertEquals(createZonedDateTime("05-08-2020"), trade.getTimestamp());
        assertEquals(0, new BigDecimal("5").compareTo(trade.getFee().getValue()));
        assertEquals(USD, trade.getFee().getCurrency());
    }

    @Test
    @DisplayName("Check save trade in database")
    public void checkSaveTradeInDatabase() {
        // =============================================================================================================
        // Check that positions, orders and trades in database doesn't trigger strategy events.
        assertTrue(strategy.getPositionsUpdateReceived().isEmpty());
        assertTrue(strategy.getTradesUpdateReceived().isEmpty());
        assertTrue(strategy.getOrdersUpdateReceived().isEmpty());

        // =============================================================================================================
        // Add a trade and check that it's correctly saved in database.
        long tradeCount = tradeRepository.count();
        TradeDTO t1 = TradeDTO.builder()
                .id("BACKUP_TRADE_11")
                .orderId("EMPTY")
                .type(BID)
                .originalAmount(new BigDecimal("1.100001"))
                .currencyPair(new CurrencyPairDTO(USDT, BTC))
                .price(new BigDecimal("2.200002"))
                .timestamp(createZonedDateTime("01-09-2020"))
                .feeAmount(new BigDecimal("3.300003"))
                .feeCurrency(BTC)
                .create();
        tradeFlux.emitValue(t1);

        // Wait until it is saved & check results.
        await().untilAsserted(() -> assertEquals(tradeCount + 1, tradeRepository.count()));
        Optional<Trade> t1FromDatabase = tradeRepository.findById("BACKUP_TRADE_11");
        assertTrue(t1FromDatabase.isPresent());
        assertEquals("BACKUP_TRADE_11", t1FromDatabase.get().getId());
        assertEquals("EMPTY", t1FromDatabase.get().getOrderId());
        assertEquals(BID, t1FromDatabase.get().getType());
        assertEquals(0, t1FromDatabase.get().getOriginalAmount().compareTo(new BigDecimal("1.100001")));
        assertEquals("USDT/BTC", t1FromDatabase.get().getCurrencyPair());
        assertEquals(0, t1FromDatabase.get().getPrice().compareTo(new BigDecimal("2.200002")));
        assertEquals(createZonedDateTime("01-09-2020"), t1FromDatabase.get().getTimestamp());
        assertEquals(0, t1FromDatabase.get().getFeeAmount().compareTo(new BigDecimal("3.300003")));
        assertEquals("BTC", t1FromDatabase.get().getFeeCurrency());
    }

}