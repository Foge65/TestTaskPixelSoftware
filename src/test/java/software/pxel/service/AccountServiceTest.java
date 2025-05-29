package software.pxel.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import software.pxel.entity.AccountEntity;
import software.pxel.entity.EmailEntity;
import software.pxel.entity.UserEntity;
import software.pxel.repository.AccountRepository;
import software.pxel.repository.EmailRepository;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private EmailRepository emailRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Authentication auth = Mockito.mock(Authentication.class);

        Mockito.when(auth.getName()).thenReturn("test@example.com");
        Mockito.when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void transfer_shouldSucceed_whenBalanceEnough() {
        UserEntity userFrom = new UserEntity();
        userFrom.setId(1L);
        UserEntity userTo = new UserEntity();
        userTo.setId(2L);

        AccountEntity from = new AccountEntity();
        from.setUserEntity(userFrom);
        from.setBalance(BigDecimal.valueOf(200));

        AccountEntity to = new AccountEntity();
        to.setUserEntity(userTo);
        to.setBalance(BigDecimal.valueOf(100));

        EmailEntity email = new EmailEntity();
        email.setEmail("test@example.com");
        email.setUserEntity(userFrom);

        Mockito.when(emailRepository.findByEmail("test@example.com")).thenReturn(Optional.of(email));
        Mockito.when(accountRepository.findByUserEntity_Id(1L)).thenReturn(Optional.of(from));
        Mockito.when(accountRepository.findByUserEntity_Id(2L)).thenReturn(Optional.of(to));

        accountService.transfer(2L, BigDecimal.valueOf(50));

        Assertions.assertEquals(BigDecimal.valueOf(150), from.getBalance());
        Assertions.assertEquals(BigDecimal.valueOf(150), to.getBalance());
    }
}