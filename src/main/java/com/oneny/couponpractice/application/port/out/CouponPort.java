package com.oneny.couponpractice.application.port.out;

import com.oneny.couponpractice.domain.Coupon;
import com.oneny.couponpractice.domain.ResolvedCoupon;

import java.util.List;

public interface CouponPort {

    Coupon save(Coupon coupon);
    List<ResolvedCoupon> listByUserId(Long userId);
}
