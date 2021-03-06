package com.shri.orderservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Customer extends BaseEntity {

    private String customerName;
    @Type(type = "org.hibernate.type.UUIDCharType")
    @Column(length = 40, columnDefinition = "varchar(40)")
    private UUID apiKey;
    @OneToMany(mappedBy = "customer")
    private Set<BeerOrder> beerOrders;
}
