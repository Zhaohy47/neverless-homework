package com.example.demo.controller;

import com.example.demo.entity.MoneyTransfer;
import com.example.demo.persistent.MoneyTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author zhaohangyu
 * @date 1/6/24
 */
@RestController
@RequestMapping(value = "/transfer")
public class TransferController {

    private final MoneyTransferService moneyTransferService;

    @Autowired
    public TransferController(MoneyTransferService moneyTransferService) {
        this.moneyTransferService = moneyTransferService;
    }

    @RequestMapping("/query")
    public ResponseEntity<MoneyTransfer> query(@RequestParam String transferId) {
        return ResponseEntity.ok(moneyTransferService.getByTransferId(UUID.fromString(transferId)));
    }
}
