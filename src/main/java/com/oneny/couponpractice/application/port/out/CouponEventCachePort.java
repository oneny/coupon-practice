package com.oneny.couponpractice.application.port.out;

import com.oneny.couponpractice.domain.CouponEvent;

public interface CouponEventCachePort {

    void set(CouponEvent couponEvent);
    CouponEvent get(Long couponEventId);
}
