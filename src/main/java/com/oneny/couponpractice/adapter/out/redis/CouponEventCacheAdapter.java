package com.oneny.couponpractice.adapter.out.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oneny.couponpractice.application.port.out.CouponEventCachePort;
import com.oneny.couponpractice.domain.CouponEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class CouponEventCacheAdapter implements CouponEventCachePort {

    private static final String KEY_PREFIX = "coupon_event.v1:";
    private static final Long EXPIRE_SECONDS = 60 * 2L;  // 2분
    private final ObjectMapper objectMapper;

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void set(CouponEvent couponEvent) {
        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(couponEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.opsForValue().set(
            this.generateCacheKey(couponEvent.getId()),
            jsonString,
            Duration.ofSeconds(EXPIRE_SECONDS)
        );
    }

    @Override
    public CouponEvent get(Long couponEventId) {
        String jsonString = redisTemplate.opsForValue().get(this.generateCacheKey(couponEventId));
        if (jsonString == null) return null;
        try {
            return objectMapper.readValue(jsonString, CouponEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateCacheKey(Long couponEventId) {
        return KEY_PREFIX + couponEventId;
    }
}
