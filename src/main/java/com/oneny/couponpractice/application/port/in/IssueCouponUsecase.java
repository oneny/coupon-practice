package com.oneny.couponpractice.application.port.in;

import com.oneny.couponpractice.domain.Coupon;

public interface IssueCouponUsecase {

    Coupon save(Long couponEventId, Long userId);
}
