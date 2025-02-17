package com.accenture.backend.service.serviceimpl;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.accenture.backend.entity.PremiumAccount;
import com.accenture.backend.entity.User;
import com.accenture.backend.exception.PremiumAccountAlreadyActiveException;
import com.accenture.backend.repository.PremiumAccountRepository;
import com.accenture.backend.repository.UserRepository;

public class PremiumAccountServiceImplTest {
    @Mock
    private PremiumAccountRepository premiumAccountRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private PremiumAccountServiceImpl premiumAccountService;

    private PremiumAccount activeAccount;
    private Long userId = 1L;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);

        Field subscriptionDurationDays = PremiumAccountServiceImpl.class
                .getDeclaredField("subscriptionDurationDays");
        subscriptionDurationDays.setAccessible(true);
        subscriptionDurationDays.set(premiumAccountService, 30);

        activeAccount = PremiumAccount.builder()
                .isActiveTill(LocalDateTime.now().plusDays(1))
                .build();
        when(premiumAccountRepo.findByUserId(userId)).thenReturn(Optional.of(activeAccount));
    }

    @Test
    void userHasActivePremiumAccount_DoesNotHave_ReturnsFalse() {
        when(premiumAccountRepo.findByUserId(userId)).thenReturn(Optional.empty());
        assertFalse(premiumAccountService.userHasActivePremiumAccount(userId));
    }

    @Test
    void userHasActivePremiumAccount_HasAccountButExpired_ReturnsFalse() {
        PremiumAccount premiumAccount = PremiumAccount.builder().isActiveTill(LocalDateTime.now().minusDays(1)).build();

        when(premiumAccountRepo.findByUserId(userId)).thenReturn(Optional.of(premiumAccount));
        assertFalse(premiumAccountService.userHasActivePremiumAccount(userId));
    }

    @Test
    void userHasActivePremiumAccount_HasActiveAccount_ReturnsTrue() {
        assertTrue(premiumAccountService.userHasActivePremiumAccount(userId));
    }

    @Test
    void createPremiumAccount_AccountAlreadyActive_ThrowException() {
        PremiumAccountAlreadyActiveException e = assertThrows(PremiumAccountAlreadyActiveException.class,
                () -> premiumAccountService.createPremiumAccount(userId));

        assertEquals("You already have an active premium account", e.getMessage());
    }

    @Test
    void createPremiumAccount_Valid_ReturnsCreatedPremiumAccount() {
        Long userId = 2L;
        User user = User.builder().id(userId).build();

        LocalDateTime expectedExpiry = LocalDateTime.now().plus(30, ChronoUnit.DAYS);
        PremiumAccount newAccount = PremiumAccount.builder().user(user).isActiveTill(expectedExpiry).build();

        when(premiumAccountRepo.findByUserId(userId)).thenReturn(Optional.empty());
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(premiumAccountRepo.save(any(PremiumAccount.class))).thenReturn(newAccount);

        PremiumAccount premiumAccount = premiumAccountService.createPremiumAccount(userId);

        assertNotNull(premiumAccount);
        assertEquals(user, premiumAccount.getUser());
        assertTrue(premiumAccount.getIsActiveTill().isAfter(LocalDateTime.now()));

        verify(premiumAccountRepo, times(1)).save(any(PremiumAccount.class));
    }
}
