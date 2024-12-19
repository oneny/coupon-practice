package com.oneny.couponpractice.application.port.in;

public interface CouponIssueHistoryUsecase {

    boolean isFirstRequestFromUser(Long couponEventId, Long userId);
    boolean hasRemainingCoupon(Long couponEventId);
}
