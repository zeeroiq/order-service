/*
 * Created by zeeroiq on 10/20/20, 4:46 AM
 */

package com.shri.orderservice.services;

import com.shri.model.CustomerPagedList;
import org.springframework.data.domain.Pageable;

public interface CustomerService {
    CustomerPagedList listCustomers(Pageable of);
}
