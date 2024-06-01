package com.example.demo.persistent;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author zhaohangyu
 * @date 1/6/24
 */
public interface ExchangeRateService {
    /**
     * Get exchange rate from fromCurrency to toCurrency
     * @param fromCurrency
     * @param toCurrency
     * @return
     */
    Optional<BigDecimal> getExchangeRate(String fromCurrency, String toCurrency);
}
