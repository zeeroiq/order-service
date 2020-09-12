package com.shri.orderservice.model;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDto extends BaseItem {

    private String customerName;

}
