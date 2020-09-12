/*
 * Created by zeeroiq on 9/13/20, 2:45 AM
 */

package com.shri.orderservice.services;

import com.shri.orderservice.bootstrap.BootstrapBeerOrder;
import com.shri.orderservice.domain.Customer;
import com.shri.orderservice.model.BeerOrderDto;
import com.shri.orderservice.model.BeerOrderLineDto;
import com.shri.orderservice.repositories.BeerOrderRepository;
import com.shri.orderservice.repositories.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
public class TastingRoomService {

    private final CustomerRepository customerRepository;
    private final BeerOrderService beerOrderService;
    private final BeerOrderRepository beerOrderRepository;
    private final List<String> beerUpcs = new ArrayList<>(3);

    public TastingRoomService(CustomerRepository customerRepository,
                              BeerOrderService beerOrderService,
                              BeerOrderRepository beerOrderRepository) {
        this.customerRepository = customerRepository;
        this.beerOrderService = beerOrderService;
        this.beerOrderRepository = beerOrderRepository;
        beerUpcs.add(BootstrapBeerOrder.BEER_1_UPC);
        beerUpcs.add(BootstrapBeerOrder.BEER_2_UPC);
        beerUpcs.add(BootstrapBeerOrder.BEER_3_UPC);
    }

    @Transactional
    @Scheduled(fixedRate = 2000)
    public void placeTastingRoomOrde() {
        List<Customer> customers = customerRepository.findAllCustomerNameLike(BeerOrderBootstrap.TASTING_ROOM);
        if (customers.size() == 1) {
            doPlaceOrder(customers.get(0));
        } else {
            log.error("Too many or too few tasting room customers found");
        }
    }

    private void doPlaceOrder(Customer customer) {

        String beerToOrder = getRandomBeerUpc();
        BeerOrderLineDto beerOrderLine = BeerOrderLineDto.builder()
                .upc(beerToOrder)
                .orderQuantity(new Random().nextInt(10)).build();
        List<BeerOrderLineDto> beerOrderLineSet = new ArrayList<>();
        beerOrderLineSet.add(beerOrderLine);
        BeerOrderDto order = BeerOrderDto.builder()
                .customerId(customer.getId())
                .customerReference(UUID.randomUUID().toString())
                .beerOrderLines(beerOrderLineSet)
                .build();
        beerOrderService.placeOrder(customer.getId(), order);


    }

    private String getRandomBeerUpc() {
        return beerUpcs.get(new Random().nextInt(beerUpcs.size()));
    }
}
