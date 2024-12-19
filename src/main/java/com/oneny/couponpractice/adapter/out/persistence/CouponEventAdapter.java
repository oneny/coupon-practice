package com.oneny.couponpractice.adapter.out.persistence;

import com.oneny.couponpractice.adapter.out.persistence.entity.CouponEventEntity;
import com.oneny.couponpractice.application.port.out.CouponEventPort;
import com.oneny.couponpractice.domain.CouponEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CouponEventAdapter implements CouponEventPort {

    private final CouponEventJpaRepository couponEventJpaRepository;

    @Override
    public CouponEvent findById(Long id) {
        CouponEventEntity couponEventEntity = couponEventJpaRepository.findById(id).orElse(null);
        if (couponEventEntity == null) {
            return null;
        }

        return CouponEntityConverter.toCouponEventModel(couponEventEntity);
    }
}
