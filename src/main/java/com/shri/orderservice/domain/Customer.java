package com.shri.orderservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Customer extends BaseEntity{

    private String customerName;
    @Column(length = 40, columnDefinition = "varchar")
    private UUID apiKey;
    @OneToMany(mappedBy = "customer")
    private Set<BeerOrder> beerOrders;
}
