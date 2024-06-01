package com.example.demo.entity;

import com.example.demo.dto.TransferAmountDto;
import com.example.demo.external.WithdrawalService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhaohangyu
 * @date 1/6/24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawRequest {

    public WithdrawRequest(MoneyTransfer moneyTransfer) {
        this.withdrawalId = new WithdrawalService.WithdrawalId(moneyTransfer.getTransferId());
        this.address = new WithdrawalService.Address(moneyTransfer.getTargetInfo());
        this.amount = new TransferAmountDto(moneyTransfer.getFromAmount(), moneyTransfer.getFromCurrency());
        this.userId = moneyTransfer.getUserId();
    }

    WithdrawalService.WithdrawalId withdrawalId;
    WithdrawalService.Address address;
    TransferAmountDto amount;
    Long userId;
}
