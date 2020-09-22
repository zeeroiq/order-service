package com.shri.model.enums;

public enum OrderStatusEnum {
    NEW("0"),
    VALIDATED("1"),
    VALIDATION_EXCEPTION("2"),
    ALLOCATED("3"),
    ALLOCATION_EXCEPTION("4"),
    PENDING_INVENTORY("5"),
    DELIVERED("6"),
    DELIVERED_EXCEPTION("7"),
    PICKED_UP("8");

    private String orderStatus;

    OrderStatusEnum(String status) {
        this.orderStatus = status;
    }

    private String getOrder() {
        return this.orderStatus;
    }
}
