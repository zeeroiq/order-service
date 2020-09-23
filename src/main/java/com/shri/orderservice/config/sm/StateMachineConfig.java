/*
 * Created by zeeroiq on 9/24/20, 12:26 AM
 */

/**
 * Created by zeeroiq on 9/23/20, 12:07 AM
 */

package com.shri.orderservice.config.sm;

import com.shri.orderservice.domain.enums.OrderEventEnum;
import com.shri.orderservice.domain.enums.OrderStatusEnum;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<OrderStatusEnum, OrderEventEnum> {

    @Override
    public void configure(StateMachineStateConfigurer<OrderStatusEnum, OrderEventEnum> states) throws Exception {

        states.withStates()
                .initial(OrderStatusEnum.NEW)
                .states(EnumSet.allOf(OrderStatusEnum.class))
                .end(OrderStatusEnum.PICKED_UP)
                .end(OrderStatusEnum.DELIVERED)
                .end(OrderStatusEnum.DELIVERED_EXCEPTION)
                .end(OrderStatusEnum.VALIDATED)
                .end(OrderStatusEnum.ALLOCATION_EXCEPTION);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatusEnum, OrderEventEnum> transitions) throws Exception {

        transitions.withExternal()
                .source(OrderStatusEnum.NEW).target(OrderStatusEnum.VALIDATION_PENDING)
                .event(OrderEventEnum.VALIDATE_ORDER)
                .and()
                .withExternal()
                .source(OrderStatusEnum.NEW).target(OrderStatusEnum.VALIDATED)
                .event(OrderEventEnum.VALIDATION_PASSED)
                .and()
                .withExternal()
                .source(OrderStatusEnum.NEW).target(OrderStatusEnum.VALIDATION_EXCEPTION)
                .event(OrderEventEnum.VALIDATION_FAILED);
    }
}
