package com.oneny.couponpractice.adapter.out.persistence;

import com.oneny.couponpractice.adapter.out.persistence.entity.CouponEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponEventJpaRepository extends JpaRepository<CouponEventEntity, Long> { }
