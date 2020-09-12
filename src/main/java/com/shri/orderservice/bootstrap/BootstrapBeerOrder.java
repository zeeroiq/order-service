/*
 * Created by zeeroiq on 9/13/20, 3:27 AM
 */

package com.shri.orderservice.bootstrap;

import com.shri.orderservice.domain.Customer;
import com.shri.orderservice.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BootstrapBeerOrder implements CommandLineRunner {

    public static final String TASTING_ROOM = "Tasting Room";
    public static final String BEER_1_UPC = UUID.randomUUID().toString();
    public static final String BEER_2_UPC = UUID.randomUUID().toString();
    public static final String BEER_3_UPC = UUID.randomUUID().toString();

    private final CustomerRepository customerRepository;
    @Override
    public void run(String... args) throws Exception {
        loadCustomerData();
    }

    private void loadCustomerData() {
        if(customerRepository.count() ==0) {
            customerRepository.save(Customer.builder()
            .customerName(TASTING_ROOM)
            .apiKey(UUID.randomUUID())
            .build());
        }
    }
}
