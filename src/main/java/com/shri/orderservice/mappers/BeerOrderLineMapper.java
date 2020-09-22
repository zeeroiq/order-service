/*
 * Created by zeeroiq on 9/12/20, 4:20 AM
 */

package com.shri.orderservice.mappers;

import com.shri.orderservice.domain.BeerOrderLine;
import com.shri.model.BeerOrderLineDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
@DecoratedWith(BeerOrderLineMapperDecorator.class)
public interface BeerOrderLineMapper {

    BeerOrderLineDto beerOrderLineToDto(BeerOrderLine orderLine);

    BeerOrderLine dtoToBeerOrderLine(BeerOrderLineDto dto);
}
