package com.oneny.couponpractice.adapter.out.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oneny.couponpractice.application.port.out.CouponIssueRequestPort;
import com.oneny.couponpractice.domain.CouponIssueRequestMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.oneny.couponpractice.global.constants.Topic.COUPON_ISSUE_REQUEST;

@Component
@RequiredArgsConstructor
public class CouponIssueRequestAdapter implements CouponIssueRequestPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void sendMessage(Long userId, Long couponEventId) {
        CouponIssueRequestMessage message = CouponIssueRequestMessage.from(userId, couponEventId);

        try {
            kafkaTemplate.send(COUPON_ISSUE_REQUEST, message.getUserId().toString(), objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
