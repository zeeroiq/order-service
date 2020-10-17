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

        boolean pendingInventory = false;
        boolean allocationException = false;
        boolean sendResponse = true;

        AllocateOrderRequest request = (AllocateOrderRequest) msg.getPayload();
        if(request.getBeerOrderDto().getCustomerReference() != null) {
            // set pending inventory
            if(request.getBeerOrderDto().getCustomerReference().equals("pending-allocation")) {
                pendingInventory = true;
            }
            // set allocation error
            if(request.getBeerOrderDto().getCustomerReference().equals("fail-allocation")) {
                allocationException = true;
            }
            else if(request.getBeerOrderDto().getCustomerReference().equals("dont-allocate")) {
                sendResponse = false;
            }
        }

        final boolean finalPendingInventory = pendingInventory;
        request.getBeerOrderDto().getBeerOrderLines()
                .forEach(beerOrderLineDto -> {
                    if (finalPendingInventory) {
                        beerOrderLineDto.setQuantityAllocated(beerOrderLineDto.getOrderQuantity() - 1);
                    } else {
                        beerOrderLineDto.setQuantityAllocated(beerOrderLineDto.getOrderQuantity());
                    }
                });

        if (sendResponse) {
            jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE,
                    AllocateOrderResult.builder()
                            .beerOrderDto(request.getBeerOrderDto())
                            .pendingInventory(pendingInventory)
                            .allocationError(allocationException)
                            .build());
        }
    }
}
