/*
 * Created by zeeroiq on 9/12/20, 3:44 AM
 */

package com.shri.orderservice.model;

import lombok.*;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerOrderLineDto extends BaseItem {

    private UUID uuid;
    private String name;
    private String upc;
    private Integer orderQuantity = 0;
}
