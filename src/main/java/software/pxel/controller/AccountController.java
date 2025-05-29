package software.pxel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import software.pxel.service.AccountService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/transferMoney")
    public ResponseEntity<?> transferMoney(@RequestParam Long toUserId, @RequestParam BigDecimal amount) {
        accountService.transfer(toUserId, amount);
        return ResponseEntity.ok().build();
    }
}
