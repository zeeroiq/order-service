/*
 * Created by zeeroiq on 9/24/20, 1:32 AM
 */

/*
 * Created by zeeroiq on 9/24/20, 1:09 AM
 */

package com.shri.model.events;

import com.shri.model.BeerOrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidateOrderRequest {

    private BeerOrderDto beerOrder;
}
