package com.example.demo.persistent.impl;

import com.example.demo.entity.ExchangeRate;
import com.example.demo.persistent.ExchangeRateService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhaohangyu
 * @date 1/6/24
 */
@Repository
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private static final ConcurrentHashMap<String, BigDecimal> MOCK_DB = new ConcurrentHashMap<>();

    private static final String CURRENCY_PAIR_FORMAT = "%s_%s";

    @PostConstruct
    public void init() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<ExchangeRate>> typeReference = new TypeReference<>() {};
        InputStream inputStream;
        try {
            inputStream = new ClassPathResource("exchangeRate.json").getInputStream();
            List<ExchangeRate> rates = mapper.readValue(inputStream, typeReference);
            rates.forEach(rate -> MOCK_DB.put(String.format(CURRENCY_PAIR_FORMAT, rate.getFromCurrency(), rate.getToCurrency()), rate.getRate()));
        } catch (IOException e) {
            throw new RuntimeException("Unable to load exchange rates from JSON file", e);
        }
    }

    @Override
    public Optional<BigDecimal> getExchangeRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return Optional.of(BigDecimal.ONE);
        }
        return Optional.ofNullable(MOCK_DB.get(String.format(CURRENCY_PAIR_FORMAT, fromCurrency, toCurrency)));
    }
}
