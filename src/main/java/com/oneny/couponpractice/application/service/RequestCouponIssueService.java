package com.oneny.couponpractice.application.service;

import com.oneny.couponpractice.application.port.in.RequestCouponIssueUsecase;
import com.oneny.couponpractice.application.port.out.CouponIssueRequestPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RequestCouponIssueService implements RequestCouponIssueUsecase {

    private final CouponIssueRequestPort couponIssueRequestPort;

    @Override
    public void queue(Long couponEventId, Long userId) {
        couponIssueRequestPort.sendMessage(userId, couponEventId);
    }
}
