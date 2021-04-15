package com.flab.shoeauction.service.message;

public interface MessageService {

    void sendSaleCompletedMessage(String token);

    void sendPurchaseCompletedMessage(String token);
}
