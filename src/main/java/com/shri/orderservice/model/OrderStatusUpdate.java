/*
 * Created by zeeroiq on 9/12/20, 4:04 AM
 */

package com.shri.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusUpdate extends BaseItem{

    private UUID orderId;
    private String customerRef;
    private String orderStatus;
}
