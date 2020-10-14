/*
 * Created by zeeroiq on 9/16/20, 2:31 AM
 */

package com.shri.orderservice.services.beer;

import com.shri.model.BeerDto;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@ConfigurationProperties("com.zeero")
@Service
public class BeerServiceImpl implements BeerService {

    public static final String BEER_PATH_V1 = "api/v1/beer/";
    public static final String BEER_UPC_PATH_V1 = "api/v1/beerUpc/";

    private String beerServiceHost;

    private final RestTemplate restTemplate;

    public BeerServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public Optional<BeerDto> getBeerById(UUID beerId) {
        return Optional.of(restTemplate.getForObject(beerServiceHost + BEER_PATH_V1, BeerDto.class));
    }

    @Override
    public Optional<BeerDto> getBeerByUpc(String upc) {
        return Optional.of(restTemplate.getForObject(beerServiceHost + BEER_UPC_PATH_V1, BeerDto.class));
    }

    public void setBeerServiceHost(String beerServiceHost) {
        this.beerServiceHost = beerServiceHost;
    }
}
