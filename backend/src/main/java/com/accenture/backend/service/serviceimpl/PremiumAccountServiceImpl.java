package com.accenture.backend.service.serviceimpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.accenture.backend.entity.PremiumAccount;
import com.accenture.backend.entity.User;
import com.accenture.backend.exception.custom.EntityNotFoundException;
import com.accenture.backend.exception.custom.PremiumAccountAlreadyActiveException;
import com.accenture.backend.repository.PremiumAccountRepository;
import com.accenture.backend.repository.UserRepository;
import com.accenture.backend.service.PremiumAccountService;
import com.accenture.backend.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PremiumAccountServiceImpl implements PremiumAccountService {

    private final PremiumAccountRepository premiumAccountRepo;
    private final UserRepository userRepo;

    @Value("${subscription.duration.days}")
    private long subscriptionDurationDays;

    @Override
    public PremiumAccount createPremiumAccount(Long userId) {
        if (userHasActivePremiumAccount(userId))
            throw new PremiumAccountAlreadyActiveException();

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));

        LocalDateTime isActiveTill = LocalDateTime.now().plus(subscriptionDurationDays, ChronoUnit.DAYS);

        PremiumAccount premiumAccount = PremiumAccount.builder().user(user).isActiveTill(isActiveTill).build();

        return premiumAccountRepo.save(premiumAccount);
    }

    @Override
    public boolean userHasActivePremiumAccount(Long userId) {
        Optional<PremiumAccount> activeAccount = premiumAccountRepo.findByUserId(userId);
        if (activeAccount.isPresent()) {
            PremiumAccount account = activeAccount.get();
            if (account.getIsActiveTill().isAfter(LocalDateTime.now())) {
                return true;
            }
        }
        return false;
    }

}
