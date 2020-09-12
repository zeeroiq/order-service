/*
 * Created by zeeroiq on 9/13/20, 1:47 AM
 */

package com.shri.orderservice.services;

import com.shri.orderservice.domain.BeerOrder;
import com.shri.orderservice.domain.Customer;
import com.shri.orderservice.domain.enums.OrderStatusEnum;
import com.shri.orderservice.mappers.BeerOrderMapper;
import com.shri.orderservice.model.BeerOrderDto;
import com.shri.orderservice.model.BeerOrderPagedList;
import com.shri.orderservice.repositories.BeerOrderRepository;
import com.shri.orderservice.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BeerOrderServiceImpl implements BeerOrderService {

    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;
    private final BeerOrderMapper beerOrderMapper;
    private final ApplicationEventPublisher publisher;


    @Override
    public BeerOrderPagedList listOrders(UUID customerId, Pageable pageable) {

        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isPresent()) {
            Page<BeerOrder> beerOrderPage = beerOrderRepository.findAllByCustomer(optionalCustomer.get(), pageable);

            return new BeerOrderPagedList(beerOrderPage
                    .stream()
                    .map(beerOrderMapper::beerOrderToDto)
                    .collect(Collectors.toList()),
                    PageRequest.of(
                            beerOrderPage.getPageable().getPageNumber(),
                            beerOrderPage.getPageable().getPageSize()),
                    beerOrderPage.getTotalElements());
        }

        return null;
    }

    @Transactional
    @Override
    public BeerOrderDto placeOrder(UUID customerId, BeerOrderDto beerOrderDto) {
        // check if the customer is present or not
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isPresent()) {
            BeerOrder beerOrder = beerOrderMapper.dtoToBeerOrder(beerOrderDto);
            beerOrder.setId(null); // while persisting, it is mentioned to be not managed by outside client
            beerOrder.setCustomer(optionalCustomer.get());
            beerOrder.setOrderStatus(OrderStatusEnum.NEW);
            // fetch all the beer order lines and for all order line set it to the current beerOrder
            beerOrder.getBeerOrderLines().forEach(orderLine -> orderLine.setBeerOrder(beerOrder));
            BeerOrder savedBeerOrder = beerOrderRepository.saveAndFlush(beerOrder);
            log.info(">>>>> Saved beer order : " + savedBeerOrder.getId());

            return beerOrderMapper.beerOrderToDto(savedBeerOrder);
        }

        throw new RuntimeException("Customer not found in record");
    }

    @Override
    public BeerOrderDto getOrderById(UUID customerId, UUID orderId) {
        return beerOrderMapper.beerOrderToDto(getOrder(customerId, orderId));
    }

    private BeerOrder getOrder(UUID customerId, UUID orderId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isPresent()) {
            Optional<BeerOrder> optionalBeerOrder = beerOrderRepository.findById(orderId);
            if (optionalBeerOrder.isPresent()) {
                BeerOrder beerOrder = optionalBeerOrder.get();

                if (beerOrder.getCustomer().getId().equals(customerId)) {
                    return beerOrder;
                }
            }
            throw new RuntimeException("Beer order not found");
        }
        throw new RuntimeException("Customer not found");
    }

    @Override
    public void pickupOrder(UUID customerId, UUID orderId) {
        BeerOrder beerOrder = getOrder(customerId, orderId);
        // once order is received set order status to be picked and save the state to the repository
        beerOrder.setOrderStatus(OrderStatusEnum.PICKED_UP);
        beerOrderRepository.saveAndFlush(beerOrder);
    }


}
