package com.oneny.couponpractice.adapter.in.model;

import com.oneny.couponpractice.domain.ResolvedCoupon;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CouponDto {
    private final Long id;
    private final String displayName;
    private final LocalDateTime expiresAt;

    public static CouponDto toDto(ResolvedCoupon resolvedCoupon) {
        return new CouponDto(
                resolvedCoupon.getCoupon().getId(),
                resolvedCoupon.getCouponEvent().getDisplayName(),
                resolvedCoupon.getCouponEvent().getExpiresAt()
        );
    }
}
