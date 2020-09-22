/*
 * Created by zeeroiq on 9/23/20, 12:42 AM
 */

/*
 * Created by zeeroiq on 9/16/20, 2:30 AM
 */

package com.shri.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerDto {

    private UUID id;
    private Integer version;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH-mm-ssZ", shape = JsonFormat.Shape.STRING)
    private OffsetDateTime createdOn;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH-mm-ssZ", shape = JsonFormat.Shape.STRING)
    private OffsetDateTime modifiedOn;
    private String beerName;
    private String beerStyle;
    private String upc;
    private Integer quantityOnHand;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal price;
}
