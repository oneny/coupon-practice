package com.oneny.couponpractice.adapter.out.persistence;

import com.oneny.couponpractice.adapter.out.persistence.entity.CouponEntity;
import com.oneny.couponpractice.application.port.out.CouponPort;
import com.oneny.couponpractice.domain.Coupon;
import com.oneny.couponpractice.domain.ResolvedCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CouponAdapter implements CouponPort {

    private final CouponJpaRepository couponJpaRepository;

    @Override
    public Coupon save(Coupon coupon) {
        CouponEntity couponEntity = couponJpaRepository.save(CouponEntityConverter.toCouponEntity(coupon));
        return CouponEntityConverter.toCouponModel(couponEntity);
    }

    @Override
    public List<ResolvedCoupon> listByUserId(Long userId) {
        List<CouponEntity> couponEntities = couponJpaRepository.findAllByUserId(userId);
        return couponEntities.stream().map(CouponEntityConverter::toResolvedCouponModel).toList();
    }
}
