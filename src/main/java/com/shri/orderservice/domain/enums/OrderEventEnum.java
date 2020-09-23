package com.shri.orderservice.domain.enums;

public enum OrderEventEnum {
    VALIDATE_ORDER(0),
    VALIDATION_PASSED(1),
    VALIDATION_FAILED(2),
    ALLOCATED_ORDER(3),
    ALLOCATION_SUCCESS(4),
    ALLOCATION_NO_INVENTORY(5),
    ALLOCATION_FAILED(6),
    ORDER_PICKED_UP(7);

    private int orderEvent;

    OrderEventEnum(int orderEvent) {
        this.orderEvent = orderEvent;
    }

    private int getOrder() {
        return this.orderEvent;
    }
}
