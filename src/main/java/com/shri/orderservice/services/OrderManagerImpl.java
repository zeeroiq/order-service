/*
 * Created by zeeroiq on 9/23/20, 11:45 PM
 */

package com.shri.orderservice.services;

import com.shri.orderservice.domain.BeerOrder;
import com.shri.orderservice.domain.enums.OrderEventEnum;
import com.shri.orderservice.domain.enums.OrderStatusEnum;
import com.shri.orderservice.repositories.BeerOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderManagerImpl implements OrderManager {

    private final StateMachineFactory<OrderStatusEnum, OrderEventEnum> stateMachineFactory;
    private final BeerOrderRepository orderRepository;

    @Override
    public BeerOrder newBeerOrder(BeerOrder beerOrder) {
        beerOrder.setId(null);
        beerOrder.setOrderStatus(OrderStatusEnum.NEW);
        BeerOrder savedBeer = orderRepository.save(beerOrder);

        sendBeerOrderEvent(savedBeer, OrderEventEnum.VALIDATE_ORDER);
        return savedBeer;
    }

    private void sendBeerOrderEvent(BeerOrder savedBeer, OrderEventEnum enumValidateOrder) {

        StateMachine<OrderStatusEnum, OrderEventEnum> stateMachine = build(savedBeer);
        Message<OrderEventEnum> msg = MessageBuilder.withPayload(enumValidateOrder).build();
        stateMachine.sendEvent(msg);
    }

    private StateMachine<OrderStatusEnum, OrderEventEnum> build(BeerOrder savedBeer) {

        StateMachine<OrderStatusEnum, OrderEventEnum> stateMachine = stateMachineFactory.getStateMachine(savedBeer.getId());
        stateMachine.stop();
        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.resetStateMachine(new DefaultStateMachineContext<>(savedBeer.getOrderStatus(), null, null, null));
                });
        stateMachine.start();
        return stateMachine;
    }
}
