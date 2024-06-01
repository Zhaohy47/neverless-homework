package com.example.demo.persistent.impl

import com.example.demo.entity.UserAccount
import com.example.demo.exception.BizException
import com.example.demo.exception.InsufficientBalanceException
import com.example.demo.exception.UserNotExistException
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

/**
 * @author zhaohangyu
 * @date 1/6/24
 */
class AccountServiceImplSpec extends Specification {
    AccountServiceImpl accountService = new AccountServiceImpl()

    def setup() {
        accountService.MOCK_DB.clear()
        accountService.MOCK_DB.put(1L, UserAccount.builder()
                .userId(1L)
                .balance(new BigDecimal("100"))
                .freezeBalance(new BigDecimal("0"))
                .activeBalance(new BigDecimal("100"))
                .currency("USD")
                .accountStatus(0)
                .lastLoginTime(System.currentTimeMillis())
                .build())
    }

    @Unroll
    def "getUserAccount returns correct account for existing user"() {
        expect:
        accountService.getUserAccount(1L).get().getUserId() == 1L
    }

    @Unroll
    def "getUserAccount returns empty for non-existing user"() {
        expect:
        !accountService.getUserAccount(2L).isPresent()
    }

    @Unroll
    def "freezeAmount throws InsufficientBalanceException when active balance is less than amount"() {
        when:
        accountService.freezeAmount(1L, new BigDecimal("200"))

        then:
        thrown(InsufficientBalanceException)
    }

    @Unroll
    def "freezeAmount updates balances correctly"() {
        when:
        accountService.freezeAmount(1L, new BigDecimal("50"))

        then:
        accountService.MOCK_DB.get(1L).getActiveBalance() == new BigDecimal("50")
        accountService.MOCK_DB.get(1L).getFreezeBalance() == new BigDecimal("50")
    }

    @Unroll
    def "unfreezeAmount throws BizException when freeze balance is less than amount"() {
        when:
        accountService.unfreezeAmount(1L, new BigDecimal("200"))

        then:
        thrown(BizException)
    }

    @Unroll
    def "unfreezeAmount updates balances correctly"() {
        setup:
        accountService.MOCK_DB.get(1L).setFreezeBalance(new BigDecimal("50"))

        when:
        accountService.unfreezeAmount(1L, new BigDecimal("50"))

        then:
        accountService.MOCK_DB.get(1L).getActiveBalance() == new BigDecimal("150")
        accountService.MOCK_DB.get(1L).getFreezeBalance() == new BigDecimal("0")
    }

    @Unroll
    def "deductAmount throws BizException when balance is less than amount"() {
        when:
        accountService.deductAmount(1L, new BigDecimal("200"))

        then:
        thrown(BizException)
    }

    @Unroll
    def "deductAmount updates balances correctly"() {
        setup:
        accountService.MOCK_DB.get(1L).setFreezeBalance(new BigDecimal("50"))

        when:
        accountService.deductAmount(1L, new BigDecimal("50"))

        then:
        accountService.MOCK_DB.get(1L).getBalance() == new BigDecimal("50")
        accountService.MOCK_DB.get(1L).getFreezeBalance() == new BigDecimal("0")
    }

    @Unroll
    def "directAddBalance updates balances correctly"() {
        when:
        accountService.directAddBalance(1L, new BigDecimal("50"))

        then:
        accountService.MOCK_DB.get(1L).getBalance() == new BigDecimal("150")
        accountService.MOCK_DB.get(1L).getActiveBalance() == new BigDecimal("150")
    }

    @Unroll
    def "validateUserId throws UserNotExistException for non-existing user"() {
        when:
        accountService.validateUserId(2L)

        then:
        thrown(UserNotExistException)
    }

    @Unroll
    def "directDeductBalance updates balances correctly"() {
        when:
        accountService.directDeductBalance(1L, new BigDecimal("40"))

        then:
        accountService.MOCK_DB.get(1L).getBalance() == new BigDecimal("60")
        accountService.MOCK_DB.get(1L).getActiveBalance() == new BigDecimal("60")
    }
}
