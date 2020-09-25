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

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listen(AllocateOrderResult result) {
        if(!result.getAllocationError() && !result.getPendingInventory()) {
            // normal allocation
            orderManager.orderAllocationPassed(result.getBeerOrderDto());
        }
        else if(!result.getAllocationError() && result.getPendingInventory()) {
            // inventory pending
            orderManager.orderAllocationPendingInventory(result.getBeerOrderDto());
        }
        else if(result.getAllocationError() && !result.getPendingInventory()) {
            // error while allocation
            orderManager.orderAllocationFailed(result.getBeerOrderDto());
        }
        else {
            throw new RuntimeException("Problem while allocation of order : " + result);
        }
    }
}
