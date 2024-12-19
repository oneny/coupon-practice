package com.oneny.couponpractice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ResolvedCoupon {

    private Coupon coupon;
    private CouponEvent couponEvent;

    public boolean canBeUsed() {
        // 쿠폰이 만료되기 전이면서 쿠폰을 발급한 적이 없는 경우
        return !this.couponEvent.isExpired() && this.coupon.getUsedAt() == null;
    }

    public static ResolvedCoupon generate(
        Coupon coupon,
        CouponEvent couponEvent
    ) {
        return new ResolvedCoupon(coupon, couponEvent);
    }
}
