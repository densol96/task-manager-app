package com.accenture.backend.service;

import com.accenture.backend.entity.PremiumAccount;

public interface PremiumAccountService {
    PremiumAccount createPremiumAccount(Long userId);

    boolean userHasActivePremiumAccount(Long userId);

}
