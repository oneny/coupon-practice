package com.oneny.couponpractice.adapter.out.redis;

import com.oneny.couponpractice.application.port.out.CouponIssueRequestHistoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@RequiredArgsConstructor
@Component
public class CouponIssueRequestHistoryAdapter implements CouponIssueRequestHistoryPort {

    private static final String USER_REQUEST_HISTORY_KEY_PREFIX = "coupon_history.user_request.v1:";
    private static final String REQUEST_COUNT_HISTORY_KEY_PREFIX = "coupon_history.request_count.v1:";
    private static final Long EXPIRE_SECONDS = 60 * 60 * 24 * 7L;  // 일주일

    private final RedisTemplate<String, String> redisTemplate;

    // 해당 쿠폰이벤트 내에서, 유저의 발급 요청이력이 없다면 기록
    @Override
    public boolean setHistoryIfNotExists(Long couponEventId, Long userId) {
        return redisTemplate.opsForValue().setIfAbsent(
                this.generateUserRequestHistoryCacheKey(couponEventId, userId),
                "1",
                Duration.ofSeconds(EXPIRE_SECONDS)
        );
    }

    // 해당 쿠폰이벤트 내에서, 발급 요청을 몇번째로 했는지 확인
    @Override
    public Long getRequestSequentialNumber(Long couponEventId) {
        String key = this.generateRequestCountHistoryCacheKey(couponEventId);
        Long requestSequentialNumber = redisTemplate.opsForValue().increment(key); // 값이 없으면 0부터 출발해서 1 더한 후 1 반환
        if (requestSequentialNumber != null && requestSequentialNumber == 1) { // 만약 키가 처음 생성되었다면
            redisTemplate.expire(key, Duration.ofSeconds(EXPIRE_SECONDS)); // TTL 설정
        }

        return requestSequentialNumber;
    }

    private String generateUserRequestHistoryCacheKey(Long couponEventId, Long userId) {
        return USER_REQUEST_HISTORY_KEY_PREFIX + couponEventId + ":" + userId;
    }

    private String generateRequestCountHistoryCacheKey(Long couponEventId) {
        return REQUEST_COUNT_HISTORY_KEY_PREFIX + couponEventId;
    }
}

