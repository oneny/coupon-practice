package com.oneny.couponpractice.application.service;

import com.oneny.couponpractice.application.port.in.ListUsableCouponsUsecase;
import com.oneny.couponpractice.application.port.out.CouponPort;
import com.oneny.couponpractice.domain.ResolvedCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListUsableCouponsService implements ListUsableCouponsUsecase {

    private final CouponPort couponPort;

    @Override
    public List<ResolvedCoupon> listByUserId(Long userId) {
        List<ResolvedCoupon> resolvedCoupons = couponPort.listByUserId(userId);
        return resolvedCoupons.stream().filter(ResolvedCoupon::canBeUsed).toList();
    }
}
