package com.oneny.couponpractice.application.port.out;

import com.oneny.couponpractice.domain.CouponEvent;

public interface CouponEventPort {

    CouponEvent findById(Long id);
}
