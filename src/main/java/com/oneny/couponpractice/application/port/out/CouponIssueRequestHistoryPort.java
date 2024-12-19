package com.oneny.couponpractice.application.port.out;

public interface CouponIssueRequestHistoryPort {

    boolean setHistoryIfNotExists(Long couponEventId, Long userId);

    Long getRequestSequentialNumber(Long couponEventId);
}
