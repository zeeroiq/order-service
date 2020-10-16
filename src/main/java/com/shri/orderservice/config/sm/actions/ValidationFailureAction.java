/*
 * Created by zeeroiq on 9/24/20, 12:59 AM
 */

package com.shri.orderservice.config.sm.actions;

import com.shri.orderservice.domain.enums.OrderEventEnum;
import com.shri.orderservice.domain.enums.OrderStatusEnum;
import com.shri.orderservice.services.sm.OrderManagerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ValidationFailureAction implements Action<OrderStatusEnum, OrderEventEnum> {


    @Override
    public void execute(StateContext<OrderStatusEnum, OrderEventEnum> context) {
        String beerOrderId = (String) context.getMessage().getHeaders().get(OrderManagerImpl.ORDER_ID_HEADER);
        log.error(">>>>> Compensating Transaction... \n >>>>> Validation Failed for order id " + beerOrderId);
    }
}
