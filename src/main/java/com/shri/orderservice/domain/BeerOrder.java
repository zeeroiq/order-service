package com.shri.orderservice.domain;

import com.shri.orderservice.domain.enums.OrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class BeerOrder extends BaseEntity{

    private String customerReference;
    @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "beerOrder", cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    private Set<BeerOrderLine> beerOrderLines;

    private OrderStatusEnum orderStatus = OrderStatusEnum.NEW;
    private String orderStatusCallbackUrl;
}
