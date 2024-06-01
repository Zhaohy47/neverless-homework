package com.example.demo.persistent.impl;

import com.example.demo.entity.UserAccount;
import com.example.demo.exception.BizException;
import com.example.demo.exception.InsufficientBalanceException;
import com.example.demo.exception.UserNotExistException;
import com.example.demo.persistent.AccountService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

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
public class AccountServiceImpl implements AccountService {

    private static final ConcurrentHashMap<Long, UserAccount> MOCK_DB = new ConcurrentHashMap<>();

    @Override
    public Optional<UserAccount> getUserAccount(Long userId) {
        return Optional.ofNullable(MOCK_DB.get(userId));
    }

    @PostConstruct
    public void init() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<UserAccount>> typeReference = new TypeReference<>() {};
        InputStream inputStream;
        try {
            inputStream = new ClassPathResource("userAccount.json").getInputStream();
            List<UserAccount> userAccounts = mapper.readValue(inputStream, typeReference);
            userAccounts.forEach(userAccount -> MOCK_DB.put(userAccount.getUserId(), userAccount));
        } catch (IOException e) {
            throw new RuntimeException("Unable to load user accounts from JSON file", e);
        }
    }

    @Override
    public void freezeAmount(Long userId, BigDecimal amount) {
        validateAmount(amount);
        validateUserId(userId);
        UserAccount userAccount = MOCK_DB.get(userId);
        if (userAccount.getActiveBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }
        userAccount.setActiveBalance(userAccount.getActiveBalance().subtract(amount));
        userAccount.setFreezeBalance(userAccount.getFreezeBalance().add(amount));
    }

    @Override
    public void unfreezeAmount(Long userId, BigDecimal amount) {
        validateAmount(amount);
        validateUserId(userId);
        UserAccount userAccount = MOCK_DB.get(userId);
        if (userAccount.getFreezeBalance().compareTo(amount) < 0) {
            throw new BizException("unfreeze amount is greater than freeze balance");
        }
        userAccount.setActiveBalance(userAccount.getActiveBalance().add(amount));
        userAccount.setFreezeBalance(userAccount.getFreezeBalance().subtract(amount));
    }

    @Override
    public void deductAmount(Long userId, BigDecimal amount) {
        validateAmount(amount);
        validateUserId(userId);
        UserAccount userAccount = MOCK_DB.get(userId);
        if (userAccount.getBalance().compareTo(amount) < 0) {
            throw new BizException("deduct amount is greater than balance");
        }
        userAccount.setBalance(userAccount.getBalance().subtract(amount));
        userAccount.setFreezeBalance(userAccount.getFreezeBalance().subtract(amount));
    }

    @Override
    public void directAddBalance(Long userId, BigDecimal amount) {
        validateAmount(amount);
        validateUserId(userId);
        UserAccount userAccount = MOCK_DB.get(userId);
        userAccount.setBalance(userAccount.getBalance().add(amount));
        userAccount.setActiveBalance(userAccount.getActiveBalance().add(amount));
    }

    @Override
    public void directDeductBalance(Long userId, BigDecimal amount) {
        validateAmount(amount);
        validateUserId(userId);
        UserAccount userAccount = MOCK_DB.get(userId);
        if (userAccount.getBalance().compareTo(amount) < 0) {
            throw new BizException("deduct amount is greater than balance");
        }
        userAccount.setBalance(userAccount.getBalance().subtract(amount));
        userAccount.setActiveBalance(userAccount.getActiveBalance().subtract(amount));
    }

    private void validateUserId(Long userId) {
        if (!MOCK_DB.containsKey(userId)) {
            throw new UserNotExistException();
        }
    }

    private void validateAmount(BigDecimal amount) {
        Assert.isTrue(amount != null && amount.compareTo(BigDecimal.ZERO) > 0, "amount must be positive");
    }
}
