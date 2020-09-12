/*
 * Created by zeeroiq on 9/13/20, 1:42 AM
 */

package com.shri.orderservice.services;

import com.shri.orderservice.model.BeerOrderDto;
import com.shri.orderservice.model.BeerOrderPagedList;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BeerOrderService {

    BeerOrderPagedList listOrders(UUID customerId, Pageable pageable);

    BeerOrderDto placeOrder(UUID customerId, BeerOrderDto beerOrderDto);

    BeerOrderDto getOrderById(UUID customerId, UUID orderId);

    void pickupOrder(UUID customerId, UUID orderId);

}
