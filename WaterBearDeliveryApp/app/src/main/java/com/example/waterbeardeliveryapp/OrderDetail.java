package com.example.waterbeardeliveryapp;

public class OrderDetail {

    private String orderId, cartId, OrderStoreName, OrderStoreId, OrderBuyerName, OrderBuyerId, OrderBuyerAddress, OrderBuyerContactNumber, OrderStatus, DeliveredBy;
    private double OrderDeliveryFee, OrderTotal;

    public OrderDetail(String orderId, String cartId, String orderStoreName, String orderStoreId, String orderBuyerName, String orderBuyerId, String orderBuyerAddress, String orderBuyerContactNumber, String orderStatus, double orderDeliveryFee, double orderTotal, String deliveredBy) {
        this.orderId = orderId;
        this.cartId = cartId;
        OrderStoreName = orderStoreName;
        OrderStoreId = orderStoreId;
        OrderBuyerName = orderBuyerName;
        OrderBuyerId = orderBuyerId;
        OrderBuyerAddress = orderBuyerAddress;
        OrderBuyerContactNumber = orderBuyerContactNumber;
        OrderStatus = orderStatus;
        OrderDeliveryFee = orderDeliveryFee;
        OrderTotal = orderTotal;
        DeliveredBy = deliveredBy;
    }

    public OrderDetail() {

    }

    public String getDeliveredBy() {
        return DeliveredBy;
    }

    public void setDeliveredBy(String deliveredBy) {
        DeliveredBy = deliveredBy;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getOrderStoreName() {
        return OrderStoreName;
    }

    public void setOrderStoreName(String orderStoreName) {
        OrderStoreName = orderStoreName;
    }

    public String getOrderStoreId() {
        return OrderStoreId;
    }

    public void setOrderStoreId(String orderStoreId) {
        OrderStoreId = orderStoreId;
    }

    public String getOrderBuyerName() {
        return OrderBuyerName;
    }

    public void setOrderBuyerName(String orderBuyerName) {
        OrderBuyerName = orderBuyerName;
    }

    public String getOrderBuyerId() {
        return OrderBuyerId;
    }

    public void setOrderBuyerId(String orderBuyerId) {
        OrderBuyerId = orderBuyerId;
    }

    public String getOrderBuyerAddress() {
        return OrderBuyerAddress;
    }

    public void setOrderBuyerAddress(String orderBuyerAddress) {
        OrderBuyerAddress = orderBuyerAddress;
    }

    public String getOrderBuyerContactNumber() {
        return OrderBuyerContactNumber;
    }

    public void setOrderBuyerContactNumber(String orderBuyerContactNumber) {
        OrderBuyerContactNumber = orderBuyerContactNumber;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }

    public double getOrderDeliveryFee() {
        return OrderDeliveryFee;
    }

    public void setOrderDeliveryFee(double orderDeliveryFee) {
        OrderDeliveryFee = orderDeliveryFee;
    }

    public double getOrderTotal() {
        return OrderTotal;
    }

    public void setOrderTotal(double orderTotal) {
        OrderTotal = orderTotal;
    }
}
