/*
 * Created by zeeroiq on 9/12/20, 3:44 AM
 */

package com.shri.orderservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class BeerOrderLine extends BaseEntity{

    @ManyToOne
    private BeerOrder beerOrder;
    private UUID beerId;
    private Integer orderQuantity = 0;
    private Integer quantityAllocated = 0;
}
