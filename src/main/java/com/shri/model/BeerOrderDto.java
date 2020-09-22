/*
 * Created by zeeroiq on 9/23/20, 12:42 AM
 */

package com.shri.model;

import com.shri.model.enums.OrderStatusEnum;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerOrderDto extends BaseItem {

    private UUID customerId;
    private String customerReference;
    private CustomerDto customerDto;
    private List<BeerOrderLineDto> beerOrderLines;
    private OrderStatusEnum orderStatus;
    private String orderStatusCallbackUrl;
}
