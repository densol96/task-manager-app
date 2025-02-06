package com.accenture.backend.service.serviceimpl;

import com.accenture.backend.service.CodeVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeVerificationServiceImp implements CodeVerificationService {
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void storeCode(String email, String code){
        log.info("Storing verification code for email: {}", email);
        stringRedisTemplate.opsForValue().set("VERIFY_" + email, code, 5, TimeUnit.MINUTES);
    }

    @Override
    public boolean verifyCode(String email, String code){
        log.info("Verifying verification code for email: {}", email);
        String storedCode = stringRedisTemplate.opsForValue().get("VERIFY_" + email);

        return storedCode!=null &&storedCode.equals(code);
    }
}
