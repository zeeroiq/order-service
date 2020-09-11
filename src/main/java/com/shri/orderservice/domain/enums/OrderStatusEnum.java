package com.shri.orderservice.domain.enums;

public enum OrderStatusEnum {
    NEW("0"),
    READY("1"),
    PICKED_UP("2");

    private String orderStatus;

    OrderStatusEnum(String status) {
        this.orderStatus = status;
    }

    private String getOrder() {
        return this.orderStatus;
    }
}
