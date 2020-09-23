/*
 * Created by zeeroiq on 9/24/20, 12:34 AM
 */

/*
 * Created by zeeroiq on 9/23/20, 11:45 PM
 */

package com.shri.orderservice.services.sm;

import com.shri.orderservice.domain.BeerOrder;

import java.util.UUID;

public interface OrderManager {

    BeerOrder newBeerOrder(BeerOrder beerOrder);

    void processValidationResult(UUID orderId, Boolean isValid);
}
