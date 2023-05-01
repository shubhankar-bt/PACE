package com.newproject.pace.Model;

public class OrderDetails {
    String fullName;
    String totalPrice;
    String paymentDone;
    String OrderId;
    String OrderName;
    String phone;
    String DeliveryDate;
    String checkboxValue;
    String uniqueId;
    String assignedToName;
    String dues;

    public OrderDetails() {
    }

    public OrderDetails(String fullName, String totalPrice, String paymentDone, String orderId, String orderName, String phone, String deliveryDate, String checkboxValue, String uniqueId, String assignedToName, String dues) {
        this.fullName = fullName;
        this.totalPrice = totalPrice;
        this.paymentDone = paymentDone;
        OrderId = orderId;
        OrderName = orderName;
        DeliveryDate = deliveryDate;
        this.checkboxValue = checkboxValue;
        this.phone = phone;
        this.uniqueId = uniqueId;
        this.assignedToName = assignedToName;
        this.dues = dues;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPaymentDone() {
        return paymentDone;
    }

    public void setPaymentDone(String paymentDone) {
        this.paymentDone = paymentDone;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getOrderName() {
        return OrderName;
    }

    public void setOrderName(String orderName) {
        OrderName = orderName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDeliveryDate() {
        return DeliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        DeliveryDate = deliveryDate;
    }

    public String getCheckboxValue() {
        return checkboxValue;
    }

    public void setCheckboxValue(String checkboxValue) {
        this.checkboxValue = checkboxValue;
    }



    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getAssignedToName() {
        return assignedToName;
    }

    public void setAssignedToName(String assignedToName) {
        this.assignedToName = assignedToName;
    }

    public String getDues() {
        return dues;
    }

    public void setDues(String dues) {
        this.dues = dues;
    }
}
