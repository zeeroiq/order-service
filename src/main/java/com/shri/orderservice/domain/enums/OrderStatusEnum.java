package com.shri.orderservice.domain.enums;

public enum OrderStatusEnum {
    NEW(0),
    VALIDATED(1),
    VALIDATION_PENDING(2),
    VALIDATION_EXCEPTION(3),
    ALLOCATED(4),
    ALLOCATION_PENDING(5),
    ALLOCATION_EXCEPTION(6),
    PENDING_INVENTORY(7),
    DELIVERED(8),
    DELIVERED_EXCEPTION(9),
    PICKED_UP(10),
    CANCELLED(11);

    private int orderStatus;

    OrderStatusEnum(int status) {
        this.orderStatus = status;
    }

    private int getOrder() {
        return this.orderStatus;
    }
}
