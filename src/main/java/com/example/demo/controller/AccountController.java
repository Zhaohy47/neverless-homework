package com.example.demo.controller;

import com.example.demo.entity.UserAccount;
import com.example.demo.persistent.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaohangyu
 * @date 1/6/24
 */
@RestController
@RequestMapping(value = "/account")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * only for test
     * @param userIds
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<UserAccount>> list(@RequestParam List<Long> userIds) {
        List<UserAccount> userAccounts = new ArrayList<>();
        for (Long userId : userIds) {
            accountService.getUserAccount(userId).ifPresent(userAccounts::add);
        }
        return ResponseEntity.ok(userAccounts);
    }
}
