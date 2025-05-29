package software.pxel.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.pxel.entity.AccountEntity;
import software.pxel.entity.EmailEntity;
import software.pxel.entity.UserEntity;
import software.pxel.repository.AccountRepository;
import software.pxel.repository.EmailRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AccountService {
    private static final BigDecimal GROWTH_MULTIPLIER = BigDecimal.valueOf(1.1);
    private static final BigDecimal MAX_MULTIPLIER = BigDecimal.valueOf(2.07);
    private final AccountRepository accountRepository;
    private final EmailRepository emailRepository;
    private final Map<Long, BigDecimal> initialDeposits = new ConcurrentHashMap<>();

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void updateBalances() {
        accountRepository.findAll().forEach(accountEntity -> {
            Long accountId = accountEntity.getId();
            BigDecimal currentBalance = accountEntity.getBalance();

            initialDeposits.computeIfAbsent(accountId, id -> currentBalance);

            BigDecimal initialDeposit = initialDeposits.get(accountId);
            BigDecimal maxBalance = initialDeposit.multiply(MAX_MULTIPLIER);
            BigDecimal nextBalance = currentBalance.multiply(GROWTH_MULTIPLIER);

            if (nextBalance.compareTo(maxBalance) > 0) {
                nextBalance = maxBalance;
            }

            accountEntity.setBalance(nextBalance);
        });
    }

    @Transactional
    public void transfer(Long toUserId, BigDecimal amount) {
        String currentUser = getCurrentUser();
        Optional<EmailEntity> currentEmail = emailRepository.findByEmail(currentUser);

        if (currentEmail.isPresent()) {
            UserEntity userEntity = currentEmail.get().getUserEntity();

            AccountEntity from = accountRepository.findByUserEntity_Id(userEntity.getId())
                    .orElseThrow(() -> new RuntimeException("User from not found"));

            AccountEntity to = accountRepository.findByUserEntity_Id(toUserId)
                    .orElseThrow(() -> new RuntimeException("User to not found"));

            if (from.getBalance().compareTo(amount) < 0) {
                throw new RuntimeException("Insufficient funds");
            }

            from.setBalance(from.getBalance().subtract(amount));
            to.setBalance(to.getBalance().add(amount));
        }
    }

    private String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
