package com.oneny.couponpractice.application.service;

import com.oneny.couponpractice.application.port.in.CouponIssueHistoryUsecase;
import com.oneny.couponpractice.application.port.out.CouponEventCachePort;
import com.oneny.couponpractice.application.port.out.CouponEventPort;
import com.oneny.couponpractice.application.port.out.CouponIssueRequestHistoryPort;
import com.oneny.couponpractice.domain.CouponEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponIssueHistoryService implements CouponIssueHistoryUsecase {

    private final CouponIssueRequestHistoryPort couponIssueRequestHistoryPort;
    private final CouponEventPort couponEventPort;
    private final CouponEventCachePort couponEventCachePort;

    @Override
    public boolean isFirstRequestFromUser(Long couponEventId, Long userId) {
        return couponIssueRequestHistoryPort.setHistoryIfNotExists(couponEventId, userId);
    }

    @Override
    public boolean hasRemainingCoupon(Long couponEventId) {
        CouponEvent couponEvent = getCouponEventById(couponEventId);

        return couponIssueRequestHistoryPort.getRequestSequentialNumber(couponEventId) <= couponEvent.getIssueLimit();
    }

    private CouponEvent getCouponEventById(Long couponEventId) {
        CouponEvent couponEventCache = couponEventCachePort.get(couponEventId);
        if (couponEventCache != null) {
            return couponEventCache;
        }

        CouponEvent couponEvent = couponEventPort.findById(couponEventId);
        if (couponEvent == null) {
            throw new RuntimeException("Coupon event does not exist.");
        }
        couponEventCachePort.set(couponEvent);
        return couponEvent;
    }
}
