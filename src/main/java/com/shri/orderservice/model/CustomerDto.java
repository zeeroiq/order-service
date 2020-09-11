package com.shri.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer extends BaseItem {

    private String customerName;
    private UUID apiKey;
    private Set<BeerOrderDto> beerOrderDtos;
}
