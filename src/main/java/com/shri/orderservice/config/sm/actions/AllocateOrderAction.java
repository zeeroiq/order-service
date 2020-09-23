/*
 * Created by zeeroiq on 9/24/20, 2:32 AM
 */

package com.shri.orderservice.config.sm.actions;

import com.shri.orderservice.config.JmsConfig;
import com.shri.orderservice.domain.BeerOrder;
import com.shri.orderservice.domain.enums.OrderEventEnum;
import com.shri.orderservice.domain.enums.OrderStatusEnum;
import com.shri.orderservice.mappers.BeerOrderMapper;
import com.shri.orderservice.repositories.BeerOrderRepository;
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
public class AllocateOrderAction implements Action<OrderStatusEnum, OrderEventEnum> {

    private final JmsTemplate jmsTemplate;
    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;

    @Override
    public void execute(StateContext<OrderStatusEnum, OrderEventEnum> context) {
        String beerOrderId = (String)context.getMessageHeader(OrderManagerImpl.ORDER_ID_HEADER);
        BeerOrder beerOrder = beerOrderRepository.findOneById(UUID.fromString(beerOrderId));

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_QUEUE,
                beerOrderMapper.beerOrderToDto(beerOrder));
        log.debug(">>>>> Sent allocation request for order id: " + beerOrderId);
    }
}
