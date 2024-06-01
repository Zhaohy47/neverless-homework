package com.example.demo.persistent.impl

import com.example.demo.entity.MoneyTransfer
import com.example.demo.enums.TransferStatusEnum
import com.example.demo.enums.TransferTypeEnum
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

/**
 * @author zhaohangyu
 * @date 1/6/24
 */
class MoneyTransferServiceImplSpec extends Specification {

    MoneyTransferServiceImpl moneyTransferService = new MoneyTransferServiceImpl()

    def setup() {
        moneyTransferService.MOCK_DB.clear()
        UUID transferId = UUID.randomUUID()
        moneyTransferService.MOCK_DB.put(transferId, new MoneyTransfer(
                transferId,
                1L,
                TransferTypeEnum.WITHDRAW.getValue(),
                "test target",
                "USD",
                new BigDecimal("100"),
                "EUR",
                new BigDecimal("85"),
                new BigDecimal("0.85"),
                TransferStatusEnum.PROCESSING.getValue(),
                System.currentTimeMillis(),
                System.currentTimeMillis()
        ))
    }

    @Unroll
    def "insert adds a new transfer to the database"() {
        given:
        int pre = moneyTransferService.MOCK_DB.size();
        def moneyTransfer = new MoneyTransfer(
                null,
                1L,
                TransferTypeEnum.WITHDRAW.getValue(),
                "test target",
                "USD",
                new BigDecimal("100"),
                "EUR",
                new BigDecimal("85"),
                new BigDecimal("0.85"),
                TransferStatusEnum.PROCESSING.getValue(),
                System.currentTimeMillis(),
                System.currentTimeMillis()
        )

        when:
        moneyTransferService.insert(moneyTransfer)
        int post = moneyTransferService.MOCK_DB.size();

        then:
        post == pre + 1
    }

    @Unroll
    def "updateStatus updates the status of an existing transfer"() {
        def transferId = moneyTransferService.MOCK_DB.keySet().iterator().next()
        def moneyTransfer = moneyTransferService.MOCK_DB.get(transferId)
        moneyTransfer.setStatus(TransferStatusEnum.COMPLETED.getValue())

        when:
        moneyTransferService.updateStatus(transferId, TransferStatusEnum.COMPLETED)

        then:
        moneyTransferService.MOCK_DB.get(transferId).getStatus() == TransferStatusEnum.COMPLETED.getValue()
    }

    @Unroll
    def "getByTransferId returns the correct transfer for an existing transfer id"() {
        def transferId = moneyTransferService.MOCK_DB.keySet().iterator().next()

        expect:
        moneyTransferService.getByTransferId(transferId).getTransferId() == transferId
    }
}
