/*
 * Created by zeeroiq on 9/24/20, 12:59 AM
 */

package com.shri.orderservice.config.sm.actions;

import com.shri.model.events.ValidateOrderRequest;
import com.shri.orderservice.config.JmsConfig;
import com.shri.orderservice.domain.BeerOrder;
import com.shri.orderservice.domain.enums.OrderEventEnum;
import com.shri.orderservice.domain.enums.OrderStatusEnum;
import com.shri.orderservice.mappers.BeerOrderMapper;
import com.shri.orderservice.repositories.BeerOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class ValidateOrderAction implements Action<OrderStatusEnum, OrderEventEnum> {

    private final BeerOrderRepository orderRepository;
    private final BeerOrderMapper orderMapper;
    private final JmsTemplate jmsTemplate;

    @Override
    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE)
    public void execute(StateContext<OrderStatusEnum, OrderEventEnum> context) {
        UUID beerOrderId = context.getMessage().getHeaders().getId();
        BeerOrder beerOrder = orderRepository.findOneById(beerOrderId);

        jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_QUEUE, ValidateOrderRequest.builder()
                .beerOrder(orderMapper.beerOrderToDto(beerOrder))
                .build()
        );
        log.debug(">>>>> Sent Validation request to queue for order id " + beerOrderId);
    }
}
