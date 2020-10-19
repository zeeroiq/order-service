/*
 * Created by zeeroiq on 10/20/20, 4:38 AM
 */

package com.shri.orderservice.mappers;

import com.shri.model.CustomerDto;
import com.shri.orderservice.domain.Customer;
import org.mapstruct.Mapper;

@Mapper(uses = DateMapper.class)
public interface CustomerMapper {

    CustomerDto customerToDto(Customer customer);
    Customer dtoToCustomer(CustomerDto customerDto);
}
