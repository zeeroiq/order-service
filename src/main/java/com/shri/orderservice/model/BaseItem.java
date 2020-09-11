package com.shri.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseItem {


    private UUID id;
    private Long version;
    private Timestamp addedOn;
    private Timestamp lastModifiedOn;

}
