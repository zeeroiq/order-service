/*
 * Created by zeeroiq on 9/13/20, 1:31 AM
 */

package com.shri.orderservice.repositories;

import com.shri.orderservice.domain.BeerOrder;
import com.shri.orderservice.domain.Customer;
import com.shri.model.enums.OrderStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.UUID;

public interface BeerOrderRepository extends JpaRepository<BeerOrder, UUID> {

    Page<BeerOrder> findAllByCustomer(Customer customer, Pageable pageable);
    List<BeerOrder> findAllByOrderStatus(OrderStatusEnum  orderStatusEnum);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    BeerOrder findOneById(UUID id);
}
