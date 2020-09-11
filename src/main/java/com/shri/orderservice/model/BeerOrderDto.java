package com.shri.orderservice.model;

import com.shri.orderservice.model.enums.OrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerOrder extends BaseItem {

    private String customerReference;
    private Customer customer;
    private Set<BeerOrderLine> beerOrderLines;
    private OrderStatusEnum orderStatus = OrderStatusEnum.NEW;
    private String orderStatusCallbackUrl;
}
