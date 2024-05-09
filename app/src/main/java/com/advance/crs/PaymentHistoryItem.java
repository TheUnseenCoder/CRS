package com.advance.crs;

public class PaymentHistoryItem {
    private int orderId;
    private String transactionId;
    private String payerId;
    private String payerFullname;
    private String paymentStatus;
    private String timeStamp;

    public PaymentHistoryItem(String transactionId, String payerId, String payerFullname, int orderId, String paymentStatus, String timeStamp) {
        this.transactionId = transactionId;
        this.payerId = payerId;
        this.payerFullname = payerFullname;
        this.orderId = orderId;
        this.paymentStatus = paymentStatus;
        this.timeStamp = timeStamp;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getPayerId() {
        return payerId;
    }

    public String getPayerFullname() {
        return payerFullname;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
}
