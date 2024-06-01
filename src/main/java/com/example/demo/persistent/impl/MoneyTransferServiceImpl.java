package com.example.demo.persistent.impl;

import com.example.demo.entity.MoneyTransfer;
import com.example.demo.enums.TransferStatusEnum;
import com.example.demo.exception.BizException;
import com.example.demo.persistent.MoneyTransferService;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhaohangyu
 * @date 1/6/24
 */
@Repository
public class MoneyTransferServiceImpl implements MoneyTransferService {

    private static final ConcurrentHashMap<UUID, MoneyTransfer> MOCK_DB = new ConcurrentHashMap<>();

    @Override
    public UUID insert(MoneyTransfer moneyTransfer) {
        UUID transferId = UUID.randomUUID();
        moneyTransfer.setTransferId(transferId);
        MOCK_DB.put(transferId, moneyTransfer);
        return transferId;
    }

    @Override
    public void updateStatus(UUID transferId, TransferStatusEnum status) {
        if (!MOCK_DB.containsKey(transferId)) {
            throw new BizException("transfer does not exist");
        }
        MoneyTransfer moneyTransfer = MOCK_DB.get(transferId);
        moneyTransfer.setStatus(status.getValue());
        moneyTransfer.setStatusTime(System.currentTimeMillis());
    }

    @Override
    public MoneyTransfer getByTransferId(UUID transferId) {
        if (!MOCK_DB.containsKey(transferId)) {
            throw new BizException("transfer does not exist");
        }
        return MOCK_DB.get(transferId);
    }
}
