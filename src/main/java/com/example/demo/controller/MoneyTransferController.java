package com.example.demo.controller;

import com.example.demo.biz.MoneyTransferManager;
import com.example.demo.dto.request.DirectTransferRequest;
import com.example.demo.dto.request.WithDrawRequest;
import com.example.demo.external.WithdrawalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;

/**
 * @author zhaohangyu
 * @date 1/6/24
 */
@RestController
@RequestMapping(value = "/moneyTransfer")
public class MoneyTransferController {

    private final MoneyTransferManager moneyTransferManager;

    @Autowired
    public MoneyTransferController(MoneyTransferManager moneyTransferManager) {
        this.moneyTransferManager = moneyTransferManager;
    }

    @PostMapping("/direc-transfer")
    public ResponseEntity<String> transfer(@RequestBody @Valid DirectTransferRequest request) {
        String transferId = moneyTransferManager.directTransfer(request.getFromUserId(), request.getToUserId(), new BigDecimal(request.getAmount()));
        return ResponseEntity.ok(transferId);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestBody @Valid WithDrawRequest request) {
        String transferId = moneyTransferManager.withDraw(
                request.getUserId(),
                new WithdrawalService.Address(request.getAddress()),
                new BigDecimal(request.getAmount()),
                request.getTargetCurrency());
        return ResponseEntity.ok(transferId);
    }

    @GetMapping("/status")
    public ResponseEntity<String> status(@RequestParam String transferId) {
        return ResponseEntity.ok(moneyTransferManager.getTransferStatus(transferId).name());
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("test");
    }
}
