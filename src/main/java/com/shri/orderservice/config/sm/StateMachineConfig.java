/*
 * Created by zeeroiq on 9/24/20, 12:26 AM
 */

/**
 * Created by zeeroiq on 9/23/20, 12:07 AM
 */

package com.shri.orderservice.config.sm;

import com.shri.orderservice.domain.enums.OrderEventEnum;
import com.shri.orderservice.domain.enums.OrderStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@RequiredArgsConstructor
@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<OrderStatusEnum, OrderEventEnum> {

    private final Action<OrderStatusEnum, OrderEventEnum> validateOrderAction;
    private final Action<OrderStatusEnum, OrderEventEnum> allocateOrderAction;
    private final Action<OrderStatusEnum, OrderEventEnum> validationFailureAction;
    private final Action<OrderStatusEnum, OrderEventEnum> allocationFailureAction;
    @Override
    public void configure(StateMachineStateConfigurer<OrderStatusEnum, OrderEventEnum> states) throws Exception {

        states.withStates()
                .initial(OrderStatusEnum.NEW)
                .states(EnumSet.allOf(OrderStatusEnum.class))
                .end(OrderStatusEnum.PICKED_UP)
                .end(OrderStatusEnum.DELIVERED)
                .end(OrderStatusEnum.DELIVERED_EXCEPTION)
                .end(OrderStatusEnum.VALIDATION_EXCEPTION)
                .end(OrderStatusEnum.ALLOCATION_EXCEPTION);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatusEnum, OrderEventEnum> transitions) throws Exception {

        transitions.withExternal()
                    .source(OrderStatusEnum.NEW).target(OrderStatusEnum.VALIDATION_PENDING)
                    .event(OrderEventEnum.VALIDATE_ORDER)
                    .action(validateOrderAction)
                .and()
                .withExternal()
                    .source(OrderStatusEnum.VALIDATION_PENDING).target(OrderStatusEnum.VALIDATED)
                    .event(OrderEventEnum.VALIDATION_PASSED)
                .and()
                .withExternal()
                    .source(OrderStatusEnum.VALIDATION_PENDING).target(OrderStatusEnum.VALIDATION_EXCEPTION)
                    .event(OrderEventEnum.VALIDATION_FAILED)
                    .action(validationFailureAction)
                .and().withExternal()
                    .source(OrderStatusEnum.VALIDATED).target(OrderStatusEnum.ALLOCATION_PENDING)
                    .event(OrderEventEnum.ALLOCATED_ORDER)
                    .action(allocateOrderAction)
                .and().withExternal()
                    .source(OrderStatusEnum.ALLOCATION_PENDING).target(OrderStatusEnum.ALLOCATED)
                    .event(OrderEventEnum.ALLOCATION_SUCCESS)
                .and().withExternal()
                    .source(OrderStatusEnum.ALLOCATION_PENDING).target(OrderStatusEnum.ALLOCATION_EXCEPTION)
                    .event(OrderEventEnum.ALLOCATION_FAILED)
                    .action(allocationFailureAction)
                .and().withExternal()
                    .source(OrderStatusEnum.ALLOCATION_PENDING).target(OrderStatusEnum.PENDING_INVENTORY)
                    .event(OrderEventEnum.ALLOCATION_NO_INVENTORY)
                .and().withExternal()
                    .source(OrderStatusEnum.ALLOCATED).target(OrderStatusEnum.PICKED_UP)
                    .event(OrderEventEnum.ORDER_PICKED_UP);
    }
}
