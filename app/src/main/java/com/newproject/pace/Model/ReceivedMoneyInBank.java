package com.newproject.pace.Model;

public class ReceivedMoneyInBank {
    String received;
    String date;

    public ReceivedMoneyInBank() {
    }

    public ReceivedMoneyInBank(String received, String date) {
        this.received = received;
        this.date = date;

    }


    public String getReceived() {
        return received;
    }

    public void setReceived(String received) {
        this.received = received;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
