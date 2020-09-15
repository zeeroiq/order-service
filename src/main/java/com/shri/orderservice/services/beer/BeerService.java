/*
 * Created by zeeroiq on 9/16/20, 2:28 AM
 */

package com.shri.orderservice.services.beer;

import com.shri.orderservice.model.BeerDto;

import java.util.Optional;
import java.util.UUID;

public interface BeerService {
    Optional<BeerDto> getBeerById(UUID beerId);
    Optional<BeerDto> getBeerByUpc(String upc);
}
