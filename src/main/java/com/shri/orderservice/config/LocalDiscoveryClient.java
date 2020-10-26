/*
 * Created by zeeroiq on 10/26/20, 5:39 AM
 */

package com.shri.orderservice.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("local-discovery")
@EnableDiscoveryClient
@Configuration
public class LocalDiscoveryClient {
}
