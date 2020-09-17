/*
 * Created by zeeroiq on 9/13/20, 1:32 AM
 */

package com.shri.orderservice.repositories;

import com.shri.orderservice.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    List<Customer> findAllByCustomerNameLike(String customerName);
}
