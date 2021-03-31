package com.flab.shoeauction.service.message;

import com.flab.shoeauction.controller.dto.UserDto.LoginRequest;
import com.flab.shoeauction.dao.FCMTokenDao;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class FCMService implements MessageService<Message> {

    private final FCMTokenDao fcmTokenDao;

    public void sendSaleCompletedMessage(String token) {
        Message message = Message.builder()
            .putData("title", "판매 완료 알림")
            .putData("content", "등록하신 판매 입찰이 낙찰되었습니다.")
            .setToken(token)
            .build();

        send(message);
    }

    public void sendPurchaseCompletedMessage(String token) {
        Message message = Message.builder()
            .putData("title", "구매 완료 알림")
            .putData("content", "등록하신 구매 입찰이 낙찰되었습니다.")
            .setToken(token)
            .build();

        send(message);
    }

    @Override
    public void send(Message message) {
        FirebaseMessaging.getInstance().sendAsync(message);
    }

    public void saveToken(LoginRequest loginRequest) {
        fcmTokenDao.saveToken(loginRequest);
    }

    public void getToken(String email) {
        fcmTokenDao.getToken(email);
    }

    public void deleteToken(String email) {
        fcmTokenDao.deleteToken(email);
    }
}
