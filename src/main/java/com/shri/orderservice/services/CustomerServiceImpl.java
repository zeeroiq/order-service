/*
 * Created by zeeroiq on 10/20/20, 4:46 AM
 */

package com.shri.orderservice.services;

import com.shri.model.CustomerPagedList;
import com.shri.orderservice.domain.Customer;
import com.shri.orderservice.mappers.CustomerMapper;
import com.shri.orderservice.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerPagedList listCustomers(Pageable pageable) {
        Page<Customer> customerPage = customerRepository.findAll(pageable);

        return new CustomerPagedList(customerPage.stream()
                                    .map(customerMapper::customerToDto)
                                    .collect(Collectors.toList()),
                                PageRequest.of(customerPage.getPageable().getPageNumber(),
                                        customerPage.getPageable().getPageSize()),
                                        customerPage.getTotalElements());
    }
}
