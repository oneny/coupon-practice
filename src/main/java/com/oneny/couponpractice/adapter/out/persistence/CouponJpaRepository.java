package com.oneny.couponpractice.adapter.out.persistence;

import com.oneny.couponpractice.adapter.out.persistence.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponJpaRepository extends JpaRepository<CouponEntity, Long> {

    List<CouponEntity> findAllByUserId(Long userId);
}
