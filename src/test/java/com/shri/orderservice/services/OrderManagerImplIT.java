/*
 * Created by zeeroiq on 9/30/20, 1:40 AM
 */

package com.shri.orderservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.shri.model.BeerDto;
import com.shri.model.BeerPagedList;
import com.shri.model.events.AllocationFailureEvent;
import com.shri.model.events.DeallocateOrderRequest;
import com.shri.orderservice.config.JmsConfig;
import com.shri.orderservice.domain.BeerOrder;
import com.shri.orderservice.domain.BeerOrderLine;
import com.shri.orderservice.domain.Customer;
import com.shri.orderservice.domain.enums.OrderStatusEnum;
import com.shri.orderservice.repositories.BeerOrderRepository;
import com.shri.orderservice.repositories.CustomerRepository;
import com.shri.orderservice.services.beer.BeerServiceImpl;
import com.shri.orderservice.services.sm.OrderManager;
import org.jgroups.util.Util;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class OrderManagerImplIT {

    @Autowired OrderManager orderManager;
    @Autowired BeerOrderRepository orderRepository;
    @Autowired CustomerRepository customerRepository;
    @Autowired WireMockServer wireMockServer;
    @Autowired ObjectMapper objectMapper;
    @Autowired JmsTemplate jmsTemplate;
    Customer testCustomer;

    UUID beerId = UUID.randomUUID();

    @BeforeEach
    void setup() {
        testCustomer = customerRepository.save(Customer.builder()
                .customerName("Test Customer")
                .build());
        wireMockServer.start();
    }
    @AfterEach
    void after() {
        wireMockServer.resetAll();
    }

//    @AfterAll
//    static void clean() {
//        wireMockServer.shutdown();
//    }

    @TestConfiguration
    static class RestTemplateBuilderProvider {

        @Bean(destroyMethod = "stop")
        public WireMockServer wireMockServer() {
            WireMockServer wireMockServer = new WireMockServer(8083);
            wireMockServer.start();
            return wireMockServer;
        }
    }

    @Test
    public void testNewAllocated() throws JsonProcessingException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
        BeerPagedList list = new BeerPagedList(Collections.singletonList(beerDto));

        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + "12345")
                        .willReturn(okJson(objectMapper.writeValueAsString(list))));
        BeerOrder beerOrder = createBeerOrder();
        BeerOrder order = orderManager.newBeerOrder(beerOrder);

        await().untilAsserted(() -> {
            BeerOrder foundOrder = orderRepository.findById(beerOrder.getId()).get();
            assertEquals(OrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());
        });
        await().untilAsserted(() -> {
            BeerOrder foundOrder = orderRepository.findById(beerOrder.getId()).get();
            BeerOrderLine line = foundOrder.getBeerOrderLines().iterator().next();
            assertEquals(line.getOrderQuantity(), line.getQuantityAllocated());
        });
        BeerOrder savedBeerOrder = orderRepository.findById(order.getId()).get();

        Util.assertNotNull(savedBeerOrder);
        assertEquals(OrderStatusEnum.ALLOCATED, savedBeerOrder.getOrderStatus());
        savedBeerOrder.getBeerOrderLines().forEach(line -> {
            assertEquals(line.getOrderQuantity(), line.getQuantityAllocated());
        });
    }

    @Test
    public void testFailedValidation() throws JsonProcessingException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + "12345")
        .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));
        BeerOrder beerOrder = createBeerOrder();
        beerOrder.setCustomerReference("fail-validation");
        orderManager.newBeerOrder(beerOrder);
        await().untilAsserted(() -> {
            BeerOrder foundOrder = orderRepository.findById(beerOrder.getId()).get();
            assertEquals(OrderStatusEnum.VALIDATION_EXCEPTION, foundOrder.getOrderStatus());
        });
    }

    @Test
    public void testNewToPickedUp() throws JsonProcessingException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
        BeerPagedList list = new BeerPagedList(Collections.singletonList(beerDto));

        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + "12345")
                .willReturn(okJson(objectMapper.writeValueAsString(list))));
        BeerOrder beerOrder = createBeerOrder();
        BeerOrder order = orderManager.newBeerOrder(beerOrder);

        await().untilAsserted(() -> {
            BeerOrder foundOrder = orderRepository.findById(beerOrder.getId()).get();
            assertEquals(OrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());
        });

        orderManager.orderPickedUp(order.getId());

        await().untilAsserted(() -> {
            BeerOrder orderFound = orderRepository.findById(beerOrder.getId()).get();
            assertEquals(OrderStatusEnum.PICKED_UP, orderFound.getOrderStatus());
        });

        BeerOrder orderPickedUp = orderRepository.findById(order.getId()).get();
        assertEquals(OrderStatusEnum.PICKED_UP, orderPickedUp.getOrderStatus());
    }

    @Test
    public void testFailedAllocation() throws JsonProcessingException {

        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + "12345")
                        .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));
        BeerOrder beerOrder = createBeerOrder();
        beerOrder.setCustomerReference("fail-allocation");
        BeerOrder savedBeerOrder = orderManager.newBeerOrder(beerOrder);
        await().untilAsserted(() -> {
            BeerOrder foundOrder = orderRepository.findById(beerOrder.getId()).get();
            assertEquals(OrderStatusEnum.ALLOCATION_EXCEPTION, foundOrder.getOrderStatus());
        });

        AllocationFailureEvent allocationFailureEvent = (AllocationFailureEvent) jmsTemplate.receiveAndConvert(JmsConfig.ALLOCATE_FAILURE_QUEUE);
        assertNotNull(allocationFailureEvent);
        assertThat(allocationFailureEvent.getOrderId()).isEqualTo(savedBeerOrder.getId());
    }

    @Test
    public void testPendingAllocation() throws JsonProcessingException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + "12345")
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));
        BeerOrder beerOrder = createBeerOrder();
        beerOrder.setCustomerReference("pending-allocation");
        orderManager.newBeerOrder(beerOrder);
        await().untilAsserted(() -> {
            BeerOrder foundOrder = orderRepository.findById(beerOrder.getId()).get();
            assertEquals(OrderStatusEnum.PENDING_INVENTORY, foundOrder.getOrderStatus());
        });
    }

    @Test
    public void testValidationPendingToCancel() throws JsonProcessingException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + "12345")
        .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder();
        beerOrder.setCustomerReference("dont-validate");
        BeerOrder savedOrder = orderManager.newBeerOrder(beerOrder);

        await().untilAsserted(() -> {
            BeerOrder foundOrder = orderRepository.findById(beerOrder.getId()).get();
            assertEquals(OrderStatusEnum.VALIDATION_PENDING, foundOrder.getOrderStatus());
        });

        orderManager.cancelOrder(savedOrder.getId());

        await().untilAsserted(() -> {
            BeerOrder foundOrder = orderRepository.findById(beerOrder.getId()).get();
            assertEquals(OrderStatusEnum.CANCELLED, foundOrder.getOrderStatus());
        });
    }

    @Test
    public void testAllocationPendingToCancel() throws JsonProcessingException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + "12345")
        .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder();
        beerOrder.setCustomerReference("dont-allocate");
        BeerOrder savedOrder = orderManager.newBeerOrder(beerOrder);

        await().untilAsserted(() -> {
            BeerOrder foundOrder = orderRepository.findById(beerOrder.getId()).get();
            assertEquals(OrderStatusEnum.ALLOCATION_PENDING, foundOrder.getOrderStatus());
        });

        orderManager.cancelOrder(savedOrder.getId());

        await().untilAsserted(() -> {
            BeerOrder foundOrder = orderRepository.findById(beerOrder.getId()).get();
            assertEquals(OrderStatusEnum.CANCELLED, foundOrder.getOrderStatus());
        });
    }

    @Test
    public void testAllocatedToCancel() throws JsonProcessingException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + "12345")
        .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder();
        BeerOrder savedOrder = orderManager.newBeerOrder(beerOrder);

        await().untilAsserted(() -> {
            BeerOrder foundOrder = orderRepository.findById(beerOrder.getId()).get();
            assertEquals(OrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());
        });

        orderManager.cancelOrder(savedOrder.getId());

        await().untilAsserted(() -> {
            BeerOrder foundOrder = orderRepository.findById(beerOrder.getId()).get();
            assertEquals(OrderStatusEnum.CANCELLED, foundOrder.getOrderStatus());
        });

        DeallocateOrderRequest deallocateOrderRequest = (DeallocateOrderRequest) jmsTemplate
                .receiveAndConvert(JmsConfig.DEALLOCATE_ORDER_QUEUE);

        assertNotNull(deallocateOrderRequest);
        assertThat(deallocateOrderRequest.getBeerOrderDto().getId()).isEqualTo(savedOrder.getId());
    }

    public BeerOrder createBeerOrder() {
        BeerOrder beerOrder = BeerOrder.builder().customer(testCustomer).build();
        Set<BeerOrderLine> lines = new HashSet<>();
        lines.add(BeerOrderLine.builder()
                .beerId(beerId)
                .upc("12345")
                .orderQuantity(1)
                .beerOrder(beerOrder)
                .build());
        beerOrder.setBeerOrderLines(lines);
        return beerOrder;
    }




}
