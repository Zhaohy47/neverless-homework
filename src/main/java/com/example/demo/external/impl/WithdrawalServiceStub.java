package com.example.demo.external.impl;

import com.example.demo.dto.TransferAmountDto;
import com.example.demo.external.WithdrawalService;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

import static com.example.demo.external.WithdrawalService.WithdrawalState.COMPLETED;
import static com.example.demo.external.WithdrawalService.WithdrawalState.FAILED;
import static com.example.demo.external.WithdrawalService.WithdrawalState.PROCESSING;

/**
 * @author zhaohangyu
 * @date 1/6/24
 */
@Service
public class WithdrawalServiceStub implements WithdrawalService {
    private final ConcurrentMap<WithdrawalId, Withdrawal> requests = new ConcurrentHashMap<>();

    @Override
    public void requestWithdrawal(WithdrawalId id, Address address, TransferAmountDto amount) { // Please substitute T with prefered type
        final var existing = requests.putIfAbsent(id, new Withdrawal(finalState(), finaliseAt(), address, amount));
        if (existing != null && !Objects.equals(existing.address, address) && !Objects.equals(existing.amount, amount)) {
            throw new IllegalStateException("Withdrawal request with id[%s] is already present".formatted(id));
        }
    }

    private WithdrawalState finalState() {
        return ThreadLocalRandom.current().nextBoolean() ? COMPLETED : FAILED;
    }

    private long finaliseAt() {
        return System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(1000, 10000);
    }

    @Override
    public WithdrawalState getRequestState(WithdrawalId id) {
        final var request = requests.get(id);
        if (request == null) {
            throw new IllegalArgumentException("Request %s is not found".formatted(id));
        }
        return request.finalState();
    }

    record Withdrawal(WithdrawalState state, long finaliseAt, Address address, TransferAmountDto amount) {
        public WithdrawalState finalState() {
            return finaliseAt <= System.currentTimeMillis() ? state : PROCESSING;
        }
    }
}
