/*
 * Created by zeeroiq on 9/30/20, 1:40 AM
 */

package com.shri.orderservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.shri.model.BeerDto;
import com.shri.model.BeerPagedList;
import com.shri.orderservice.domain.BeerOrder;
import com.shri.orderservice.domain.BeerOrderLine;
import com.shri.orderservice.domain.Customer;
import com.shri.orderservice.domain.enums.OrderStatusEnum;
import com.shri.orderservice.repositories.BeerOrderRepository;
import com.shri.orderservice.repositories.CustomerRepository;
import com.shri.orderservice.services.beer.BeerServiceImpl;
import com.shri.orderservice.services.sm.OrderManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class OrderManagerImplIT {

    @Autowired OrderManager orderManager;
    @Autowired BeerOrderRepository orderRepository;
    @Autowired CustomerRepository customerRepository;
    @Autowired WireMockServer wireMockServer;
    @Autowired ObjectMapper objectMapper;

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
    public void testNewAllocated() throws JsonProcessingException, InterruptedException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
        BeerPagedList list = new BeerPagedList(Collections.singletonList(beerDto));

        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + "12345")
                        .willReturn(okJson(objectMapper.writeValueAsString(list))));
        BeerOrder beerOrder = createBeerOrder();
        BeerOrder order = orderManager.newBeerOrder(beerOrder);
        System.out.println(">>>>> SLEEPING... >>>>>");
        Thread.sleep(10000);
        System.out.println(">>>>> AWAKENING... >>>>>");

        BeerOrder order2 = orderRepository.findOneById(order.getId());
        assertNotNull(order);
        assertEquals(OrderStatusEnum.ALLOCATED, order2.getOrderStatus());
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
