package com.accenture.backend.service.serviceimpl;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.accenture.backend.exception.InvalidInputException;
import com.accenture.backend.exception.InvalidUUIDException;
import com.accenture.backend.service.TokenStoreService;

@Service
public class TokenStoreServiceImpl implements TokenStoreService {

    private ConcurrentHashMap<String, String> tokenStore = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> tokenExpiry = new ConcurrentHashMap<>();
    private static final long TOKEN_TTL = 5 * 60 * 1000; // 5 minutes

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public TokenStoreServiceImpl() {
        scheduler.scheduleAtFixedRate(this::removeExpiredTokens, TOKEN_TTL, TOKEN_TTL, TimeUnit.MILLISECONDS);
    }

    @Override
    public String addTokenAndReturnUUID(String token) {
        String uuid = UUID.randomUUID().toString();
        addToken(uuid, token);
        return uuid;
    }

    @Override
    public void addToken(String uuid, String token) {
        if (uuid == null)
            throw new InvalidInputException("uuid", null);
        if (token == null)
            throw new InvalidInputException("token", null);

        tokenStore.put(uuid, token);
        tokenExpiry.put(uuid, System.currentTimeMillis() + TOKEN_TTL);
    }

    @Override
    public String getToken(String uuid) {
        if (uuid == null)
            throw new InvalidInputException("uuid", null);

        String token = tokenStore.get(uuid);
        tokenStore.remove(uuid);

        if (token == null)
            throw new InvalidUUIDException();

        return token;
    }

    private void removeExpiredTokens() {
        long now = System.currentTimeMillis();
        tokenExpiry.forEach((uuid, expiryTime) -> {
            if (expiryTime < now) {
                tokenStore.remove(uuid);
                tokenExpiry.remove(uuid);
            }
        });
    }
}
