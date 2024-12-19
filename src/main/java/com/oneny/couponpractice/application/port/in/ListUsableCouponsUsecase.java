package com.oneny.couponpractice.application.port.in;

import com.oneny.couponpractice.domain.ResolvedCoupon;

import java.util.List;

public interface ListUsableCouponsUsecase {

    List<ResolvedCoupon> listByUserId(Long userId);
}
