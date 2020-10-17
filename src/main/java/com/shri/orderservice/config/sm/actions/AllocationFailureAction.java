/*
 * Created by zeeroiq on 9/24/20, 12:59 AM
 */

package com.shri.orderservice.config.sm.actions;

import com.shri.model.events.AllocationFailureEvent;
import com.shri.orderservice.config.JmsConfig;
import com.shri.orderservice.domain.enums.OrderEventEnum;
import com.shri.orderservice.domain.enums.OrderStatusEnum;
import com.shri.orderservice.services.sm.OrderManagerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class AllocationFailureAction implements Action<OrderStatusEnum, OrderEventEnum> {

   private final JmsTemplate jmsTemplate;

    @Override
    public void execute(StateContext<OrderStatusEnum, OrderEventEnum> context) {
        String beerOrderId = (String) context.getMessage().getHeaders().get(OrderManagerImpl.ORDER_ID_HEADER);
        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_FAILURE_QUEUE, AllocationFailureEvent.builder()
                .orderId(UUID.fromString(beerOrderId))
                .build());

        log.debug(">>>>> Sent AllocationFailureMessage to queue for order id " + beerOrderId);
    }
}
