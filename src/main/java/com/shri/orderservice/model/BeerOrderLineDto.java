/*
 * Created by zeeroiq on 9/12/20, 3:44 AM
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
public class BeerOrderLineDto extends BaseItem {

    private String name;
}
