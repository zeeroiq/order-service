/*
 * Created by zeeroiq on 9/23/20, 11:45 PM
 */

package com.shri.orderservice.services;

import com.shri.orderservice.domain.BeerOrder;

public interface OrderManager {

    BeerOrder newBeerOrder(BeerOrder beerOrder);
}
