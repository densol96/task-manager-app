package com.accenture.backend.service;

public interface TokenStoreService {
    public void addToken(String uuid, String token);

    public String addTokenAndReturnUUID(String token);

    public String getToken(String uuid);
}
