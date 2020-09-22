package com.shri.orderservice.domain.enums;

public enum OrderEventEnum {
    VALIDATE_ORDER("0"),
    VALIDATION_PASSED("1"),
    VALIDATION_FAILED("2"),
    ALLOCATION_SUCCESS("3"),
    ALLOCATION_NO_INVENTORY("4"),
    ALLOCATION_FAILED("5"),
    ORDER_PICKED_UP("6");

    private String orderEvent;

    OrderEventEnum(String orderEvent) {
        this.orderEvent = orderEvent;
    }

    private String getOrder() {
        return this.orderEvent;
    }
}
