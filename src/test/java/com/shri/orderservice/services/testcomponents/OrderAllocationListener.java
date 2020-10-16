/*
 * Created by zeeroiq on 10/15/20, 12:14 PM
 */

package com.shri.orderservice.services.testcomponents;

import com.shri.model.events.AllocateOrderRequest;
import com.shri.model.events.AllocateOrderResult;
import com.shri.orderservice.config.JmsConfig;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class OrderAllocationListener {

    private final JmsTemplate jmsTemplate;

    public OrderAllocationListener(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listen(Message msg) {
        AllocateOrderRequest request = (AllocateOrderRequest) msg.getPayload();
        request.getBeerOrderDto().getBeerOrderLines()
                .forEach(beerOrderLineDto ->
                        beerOrderLineDto.setQuantityAllocated(beerOrderLineDto.getOrderQuantity()));

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE,
                AllocateOrderResult.builder()
                .beerOrderDto(request.getBeerOrderDto())
                .pendingInventory(false)
                .allocationError(false)
                .build());
    }
}
