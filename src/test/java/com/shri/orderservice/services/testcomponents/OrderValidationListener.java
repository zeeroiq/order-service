/*
 * Created by zeeroiq on 10/14/20, 9:19 PM
 */

package com.shri.orderservice.services.testcomponents;

import com.shri.model.events.ValidateOrderRequest;
import com.shri.model.events.ValidateOrderResult;
import com.shri.orderservice.config.JmsConfig;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class OrderValidationListener {

    private final JmsTemplate jmsTemplate;

    public OrderValidationListener(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Transactional
    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_QUEUE)
    public void list(Message msg) {
        boolean isValid = true;

        ValidateOrderRequest request = (ValidateOrderRequest) msg.getPayload();
        if(request.getBeerOrder().getCustomerReference() != null
                && request.getBeerOrder().getCustomerReference().equals("fail-validation")) {
            isValid = false;
        }
        jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE,
                ValidateOrderResult.builder()
                        .isValid(isValid)
                        .orderId(request.getBeerOrder().getId())
                    .build());
        System.out.println(">>>> AFTER VALIDATE_ORDER_RESPONSE_QUEUE >>>>>");

    }
}
