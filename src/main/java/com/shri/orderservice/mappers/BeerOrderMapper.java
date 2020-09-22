/*
 * Created by zeeroiq on 9/12/20, 4:19 AM
 */

package com.shri.orderservice.mappers;

import com.shri.orderservice.domain.BeerOrder;
import com.shri.model.BeerOrderDto;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class, BeerOrderLineMapper.class})
public interface BeerOrderMapper {

    BeerOrderDto beerOrderToDto(BeerOrder order);

    BeerOrder dtoToBeerOrder(BeerOrderDto dto);
}
