package com.oneny.couponpractice.application.service;

import com.oneny.couponpractice.application.port.in.IssueCouponUsecase;
import com.oneny.couponpractice.application.port.out.CouponPort;
import com.oneny.couponpractice.domain.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class IssueCouponService implements IssueCouponUsecase {

    private final CouponPort couponPort;

    @Override
    public Coupon save(Long couponEventId, Long userId) {
        Coupon coupon = Coupon.generate(userId, couponEventId);
        return couponPort.save(coupon);
    }
}
