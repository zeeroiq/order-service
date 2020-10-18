/*
 * Created by zeeroiq on 9/26/20, 12:33 AM
 */

package com.shri.orderservice.services.listeners;

import com.shri.model.events.AllocateOrderResult;
import com.shri.orderservice.config.JmsConfig;
import com.shri.orderservice.services.sm.OrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderAllocationResultListener {

    private final OrderManager orderManager;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE)
    public void listen(AllocateOrderResult result) {
        if(!result.getAllocationError() && !result.getPendingInventory()){
            //allocated normally
            orderManager.orderAllocationPassed(result.getBeerOrderDto());
        } else if(!result.getAllocationError() && result.getPendingInventory()) {
            //pending inventory
            orderManager.orderAllocationPendingInventory(result.getBeerOrderDto());
        } else if(result.getAllocationError()){
            //allocation error
            orderManager.orderAllocationFailed(result.getBeerOrderDto());
        }
    }
}
