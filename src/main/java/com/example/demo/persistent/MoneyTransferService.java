package com.example.demo.persistent;

import com.example.demo.entity.MoneyTransfer;
import com.example.demo.enums.TransferStatusEnum;

import java.util.UUID;

/**
 * @author zhaohangyu
 * @date 1/6/24
 */
public interface MoneyTransferService {
    /**
     * Insert a money transfer record
     * @param moneyTransfer
     * @return transfer id
     */
    UUID insert(MoneyTransfer moneyTransfer);

    void updateStatus(UUID transferId, TransferStatusEnum status);

    MoneyTransfer getByTransferId(UUID transferId);
}
