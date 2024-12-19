package com.oneny.couponpractice.adapter.in.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oneny.couponpractice.application.port.in.IssueCouponUsecase;
import com.oneny.couponpractice.domain.CouponIssueRequestMessage;
import com.oneny.couponpractice.global.constants.Topic;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.oneny.couponpractice.global.constants.Topic.COUPON_ISSUE_REQUEST;

@RequiredArgsConstructor
@Component
public class CouponIssuingConsumer {

    private final ObjectMapper objectMapper;

    private final IssueCouponUsecase issueCouponUsecase;

    @KafkaListener(
        topics = { COUPON_ISSUE_REQUEST },
        groupId = "coupon-issue-request",
        concurrency = "3"
    )
    public void listen(ConsumerRecord<String, String> message) throws JsonProcessingException {
        CouponIssueRequestMessage couponIssueRequestMessage = objectMapper.readValue(message.value(), CouponIssueRequestMessage.class);
        issueCouponUsecase.save(
            couponIssueRequestMessage.getCouponEventId(),
            couponIssueRequestMessage.getUserId()
        );
    }
}
