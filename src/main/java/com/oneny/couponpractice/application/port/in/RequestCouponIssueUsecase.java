package com.oneny.couponpractice.application.port.in;

public interface RequestCouponIssueUsecase {

    void queue(Long couponEventId, Long userId);
}
