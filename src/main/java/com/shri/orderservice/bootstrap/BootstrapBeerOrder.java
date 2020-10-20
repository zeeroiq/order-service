/*
 * Created by zeeroiq on 9/13/20, 3:27 AM
 */

package com.shri.orderservice.bootstrap;

import com.shri.orderservice.domain.Customer;
import com.shri.orderservice.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BootstrapBeerOrder implements CommandLineRunner {

    public static final String TASTING_ROOM = "Tasting Room";
//    public static final String BEER_1_UPC = UUID.randomUUID().toString();
//    public static final String BEER_2_UPC = UUID.randomUUID().toString();
//    public static final String BEER_3_UPC = UUID.randomUUID().toString();
    public static final String BEER_1_UPC = "631234200036";
    public static final String BEER_2_UPC = "631234300019";
    public static final String BEER_3_UPC = "083783375213";

    private final CustomerRepository customerRepository;
    @Override
    public void run(String... args) throws Exception {
        loadCustomerData();
    }

    private void loadCustomerData() {
        if(customerRepository.findAllByCustomerNameLike(BootstrapBeerOrder.TASTING_ROOM).size() ==0) {
            Customer savedCustomer = customerRepository.save(Customer.builder()
                    .customerName(TASTING_ROOM)
                    .apiKey(UUID.randomUUID())
                    .build());
            log.debug(">>>>> Tasting Room Customer id: " + savedCustomer.getId().toString());

        }

        log.debug(">>>>>> adding customer tasting room");
    }
}
