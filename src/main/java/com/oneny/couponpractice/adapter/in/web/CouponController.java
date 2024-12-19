package com.oneny.couponpractice.adapter.in.web;

import com.oneny.couponpractice.adapter.in.model.CouponDto;
import com.oneny.couponpractice.adapter.in.model.CouponIssueRequest;
import com.oneny.couponpractice.application.port.in.CouponIssueHistoryUsecase;
import com.oneny.couponpractice.application.port.in.ListUsableCouponsUsecase;
import com.oneny.couponpractice.application.port.in.RequestCouponIssueUsecase;
import com.oneny.couponpractice.domain.ResolvedCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/coupons")
public class CouponController {

    private final CouponIssueHistoryUsecase couponIssueHistoryUsecase;
    private final RequestCouponIssueUsecase requestCouponIssueUsecase;
    private final ListUsableCouponsUsecase listUsableCouponsUsecase;

    @PostMapping
    public ResponseEntity<String> issue(
            @RequestBody CouponIssueRequest request
    ) {

        if (!couponIssueHistoryUsecase.isFirstRequestFromUser(request.getCouponEventId(), request.getUserId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Already tried to issue a coupon\n");
        }
        if (!couponIssueHistoryUsecase.hasRemainingCoupon(request.getCouponEventId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not enough available coupons\n");
        }
        // 큐로 전송
        requestCouponIssueUsecase.queue(request.getCouponEventId(), request.getUserId());

        return ResponseEntity.ok().body("Successfully Issued\n");
    }

    @GetMapping
    public ResponseEntity<List<CouponDto>> listUsableCoupons(
            @RequestParam(name = "userId", defaultValue = "0", required = false) Long userId
    ) {

        List<ResolvedCoupon> resolvedCoupons = listUsableCouponsUsecase.listByUserId(userId);

        return ResponseEntity.ok().body(resolvedCoupons.stream().map(CouponDto::toDto).toList());
    }
}
