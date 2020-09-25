/*
 * Created by zeeroiq on 9/24/20, 12:34 AM
 */

/*
 * Created by zeeroiq on 9/23/20, 11:45 PM
 */

package com.shri.orderservice.services.sm;

import com.shri.model.BeerOrderDto;
import com.shri.orderservice.config.sm.OrderStateInterceptor;
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

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OrderManagerImpl implements OrderManager {

    public static final String ORDER_ID_HEADER = "ORDER_ID_HEADER";
    private final StateMachineFactory<OrderStatusEnum, OrderEventEnum> stateMachineFactory;
    private final BeerOrderRepository orderRepository;
    private final OrderStateInterceptor orderStateInterceptor;

    @Override
    public BeerOrder newBeerOrder(BeerOrder beerOrder) {
        beerOrder.setId(null);
        beerOrder.setOrderStatus(OrderStatusEnum.NEW);
        BeerOrder savedBeer = orderRepository.save(beerOrder);

        sendBeerOrderEvent(savedBeer, OrderEventEnum.VALIDATE_ORDER);
        return savedBeer;
    }

    @Override
    public void processValidationResult(UUID orderId, Boolean isValid) {
        BeerOrder beerOrder = orderRepository.getOne(orderId);
        if (isValid) {
            sendBeerOrderEvent(beerOrder, OrderEventEnum.VALIDATION_PASSED);
            BeerOrder validatedOrder = orderRepository.findOneById(orderId);
            sendBeerOrderEvent(validatedOrder, OrderEventEnum.ALLOCATED_ORDER);
        } else {
            sendBeerOrderEvent(beerOrder, OrderEventEnum.VALIDATION_FAILED);
        }

    }

    @Override
    public void orderAllocationPassed(BeerOrderDto beerOrderDto) {
        BeerOrder beerOrder = orderRepository.getOne(beerOrderDto.getId());
        sendBeerOrderEvent(beerOrder, OrderEventEnum.ALLOCATION_SUCCESS);
        updateAllocatedQuantity(beerOrderDto, beerOrder);
    }

    @Override
    public void orderAllocationPendingInventory(BeerOrderDto beerOrderDto) {
        BeerOrder beerOrder = orderRepository.getOne(beerOrderDto.getId());
        sendBeerOrderEvent(beerOrder, OrderEventEnum.ALLOCATION_NO_INVENTORY);
        updateAllocatedQuantity(beerOrderDto, beerOrder);
    }

    @Override
    public void orderAllocationFailed(BeerOrderDto beerOrderDto) {
        BeerOrder beerOrder = orderRepository.getOne(beerOrderDto.getId());
        sendBeerOrderEvent(beerOrder, OrderEventEnum.ALLOCATION_FAILED);
    }


    private void updateAllocatedQuantity(BeerOrderDto beerOrderDto, BeerOrder beerOrder) {
        BeerOrder allocatedOrder = orderRepository.getOne(beerOrderDto.getId());
        allocatedOrder.getBeerOrderLines().forEach(orderLine -> {
            beerOrderDto.getBeerOrderLines().forEach(beerOrderLineDto -> {
                if (orderLine.getId().equals(beerOrderLineDto.getId())) {
                    orderLine.setQuantityAllocated(beerOrderLineDto.getQuantityAllocated());
                }
            });
        });

        orderRepository.saveAndFlush(beerOrder);
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
                    sma.addStateMachineInterceptor(orderStateInterceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(savedBeer.getOrderStatus(), null, null, null));
                });
        stateMachine.start();
        return stateMachine;
    }
}
