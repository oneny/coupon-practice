package com.oneny.couponpractice.application.port.out;

public interface CouponIssueRequestPort {

    void sendMessage(Long userId, Long couponEventId);
}
