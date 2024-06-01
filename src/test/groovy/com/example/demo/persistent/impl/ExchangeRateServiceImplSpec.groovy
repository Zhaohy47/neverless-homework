package com.example.demo.persistent.impl

import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author zhaohangyu
 * @date 1/6/24
 */
class ExchangeRateServiceImplSpec extends Specification {

    ExchangeRateServiceImpl exchangeRateService = new ExchangeRateServiceImpl()

    def setup() {
        exchangeRateService.MOCK_DB.clear()
        exchangeRateService.MOCK_DB.put("USD_EUR", new BigDecimal("0.85"))
    }

    @Unroll
    def "getExchangeRate returns correct rate for existing currency pair"() {
        expect:
        exchangeRateService.getExchangeRate("USD", "EUR").get() == new BigDecimal("0.85")
    }

    @Unroll
    def "getExchangeRate returns empty for non-existing currency pair"() {
        expect:
        !exchangeRateService.getExchangeRate("USD", "JPY").isPresent()
    }
}
