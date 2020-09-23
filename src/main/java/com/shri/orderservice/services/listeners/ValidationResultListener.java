/*
 * Created by zeeroiq on 9/24/20, 2:13 AM
 */

package com.shri.orderservice.services.listeners;

import com.shri.model.events.ValidateOrderResult;
import com.shri.orderservice.services.sm.OrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class ValidationResultListener {
    private final OrderManager orderManager;


    public void listen(ValidateOrderResult result) {
        final UUID orderId = result.getOrderId();
        log.debug(">>>>> validation result for orderId: " + orderId);
        orderManager.processValidationResult(orderId, result.getIsValid());
    }
}
