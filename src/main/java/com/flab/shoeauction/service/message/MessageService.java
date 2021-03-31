package com.flab.shoeauction.service.message;

public interface MessageService <T>{

    void send(T message);
}
