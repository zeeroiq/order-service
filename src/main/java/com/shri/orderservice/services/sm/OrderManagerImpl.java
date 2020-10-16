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
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderManagerImpl implements OrderManager {

    public static final String ORDER_ID_HEADER = "ORDER_ID_HEADER";
    private final StateMachineFactory<OrderStatusEnum, OrderEventEnum> stateMachineFactory;
    private final BeerOrderRepository orderRepository;
    private final OrderStateInterceptor orderStateInterceptor;

    @Override
    @Transactional
    public BeerOrder newBeerOrder(BeerOrder beerOrder) {
        beerOrder.setId(null);
        beerOrder.setOrderStatus(OrderStatusEnum.NEW);
        BeerOrder savedBeer = orderRepository.saveAndFlush(beerOrder);

        sendBeerOrderEvent(savedBeer, OrderEventEnum.VALIDATE_ORDER);
        return savedBeer;
    }

    @Transactional
    @Override
    public void processValidationResult(UUID orderId, Boolean isValid) {
        log.debug(">>>>> Process Validation Result for beerOrderId: " + orderId + " Valid? " + isValid);
        Optional<BeerOrder> beerOrderOptional = orderRepository.findById(orderId);
        beerOrderOptional.ifPresentOrElse(beerOrder ->  {
            if (isValid) {
                sendBeerOrderEvent(beerOrder, OrderEventEnum.VALIDATION_PASSED);
                // wait for status change
                awaitForStatus(orderId, OrderStatusEnum.VALIDATED);

                BeerOrder validatedOrder = orderRepository.findById(orderId).get();
                sendBeerOrderEvent(validatedOrder, OrderEventEnum.ALLOCATED_ORDER);
            } else {
                sendBeerOrderEvent(beerOrder, OrderEventEnum.VALIDATION_FAILED);
            }
        }, () -> log.error(">>>>> Order Not Found. Id: " + orderId));

    }

    @Override
    public void orderAllocationPassed(BeerOrderDto beerOrderDto) {
        Optional<BeerOrder> beerOrderOptional = orderRepository.findById(beerOrderDto.getId());
        beerOrderOptional.ifPresentOrElse(beerOrder -> {
            sendBeerOrderEvent(beerOrder, OrderEventEnum.ALLOCATION_SUCCESS);
            awaitForStatus(beerOrder.getId(), OrderStatusEnum.ALLOCATED);
            updateAllocatedQuantity(beerOrderDto);
        }, () -> log.error("Order Id not found: " + beerOrderDto.getId()));
    }

    @Override
    public void orderAllocationPendingInventory(BeerOrderDto beerOrderDto) {
        Optional<BeerOrder> beerOrderOptional = orderRepository.findById(beerOrderDto.getId());
        beerOrderOptional.ifPresentOrElse(beerOrder -> {
            sendBeerOrderEvent(beerOrder, OrderEventEnum.ALLOCATION_NO_INVENTORY);
            awaitForStatus(beerOrder.getId(), OrderStatusEnum.PENDING_INVENTORY);
            updateAllocatedQuantity(beerOrderDto);
        }, () -> log.error("Order Id not found: " + beerOrderDto.getId()));

    }

    @Override
    public void orderAllocationFailed(BeerOrderDto beerOrderDto) {
        Optional<BeerOrder> beerOrderOptional = orderRepository.findById(beerOrderDto.getId());

        beerOrderOptional.ifPresentOrElse(beerOrder -> {
            sendBeerOrderEvent(beerOrder, OrderEventEnum.ALLOCATION_FAILED);
        }, () -> log.error("Order Not Found. Id: " + beerOrderDto.getId()) );

    }

    @Override
    public void orderPickedUp(final UUID orderId) {
        Optional<BeerOrder> orderOptional = orderRepository.findById(orderId);
        orderOptional.ifPresentOrElse(beerOrder -> {
            sendBeerOrderEvent(beerOrder, OrderEventEnum.ORDER_PICKED_UP);
            log.debug(">>>>> Order PICKED UP. Order Id: " + beerOrder.getId());
        }, () -> log.error("Order Not found to be picked up. Order Id: "+ orderId));
    }

    private void updateAllocatedQuantity(BeerOrderDto beerOrderDto) {
        Optional<BeerOrder> allocatedOrderOptional = orderRepository.findById(beerOrderDto.getId());

        allocatedOrderOptional.ifPresentOrElse(allocatedOrder -> {
            allocatedOrder.getBeerOrderLines().forEach(orderLine -> {
                beerOrderDto.getBeerOrderLines().forEach(beerOrderLineDto -> {
                    if (orderLine.getId().equals(beerOrderLineDto.getId())) {
                        orderLine.setQuantityAllocated(beerOrderLineDto.getQuantityAllocated());
                    }
                });
            });

            orderRepository.saveAndFlush(allocatedOrder);
        }, () -> log.error("Order Id not found: " + beerOrderDto.getId()));


    }

    private void sendBeerOrderEvent(BeerOrder savedBeer, OrderEventEnum eventEnum) {

        StateMachine<OrderStatusEnum, OrderEventEnum> stateMachine = build(savedBeer);
        Message<OrderEventEnum> msg = MessageBuilder.withPayload(eventEnum)
                .setHeader(ORDER_ID_HEADER, savedBeer.getId().toString())
                .build();
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

    private void awaitForStatus(UUID orderId, OrderStatusEnum statusEnum) {
        AtomicBoolean found = new AtomicBoolean(false);
        AtomicInteger loopCount = new AtomicInteger(0);

        while(!found.get()) {
            if(loopCount.incrementAndGet() > 10) {
                found.set(true);
                log.debug(">>>>> Loop Retries exceeds");
            }

            orderRepository.findById(orderId).ifPresentOrElse(beerOrder -> {
                if(beerOrder.getOrderStatus().equals(statusEnum)) {
                    found.set(true);
                    log.debug(">>>>> Order found");
                } else {
                    log.debug(">>>>>> Order status not equal. Expected: " + statusEnum.name() + " Found"
                            + beerOrder.getOrderStatus().name());
                }
            }, () -> log.debug("OrderId not found"));

            if(!found.get()) {
                try {
                    log.debug("Sleeping for retry");
                    Thread.sleep(100);
                } catch (Exception e) {
                    log.error("<<<<< Exception occurred with reason: " + e.getLocalizedMessage());
                }
            }
        }
    }

}
