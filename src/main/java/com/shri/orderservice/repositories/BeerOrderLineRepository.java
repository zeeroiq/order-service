/*
 * Created by zeeroiq on 9/13/20, 1:30 AM
 */

package com.shri.orderservice.repositories;

import com.shri.orderservice.domain.BeerOrderLine;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface BeerOrderLineRepository extends PagingAndSortingRepository<BeerOrderLine, UUID> {
}
