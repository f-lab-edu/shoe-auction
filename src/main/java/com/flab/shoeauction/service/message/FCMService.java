package com.flab.shoeauction.service.message;

import com.flab.shoeauction.controller.dto.UserDto.LoginRequest;
import com.flab.shoeauction.dao.FCMTokenDao;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FCMService implements MessageService {

    private final FCMTokenDao fcmTokenDao;

    public void sendSaleCompletedMessage(String email) {
        if (!hasKey(email)) {
            return;
        }

        String token = getToken(email);
        Message message = Message.builder()
            .putData("title", "판매 완료 알림")
            .putData("content", "등록하신 판매 입찰이 낙찰되었습니다.")
            .setToken(token)
            .build();

        send(message);
    }

    public void sendPurchaseCompletedMessage(String email) {
        if (!hasKey(email)) {
            return;
        }

        String token = getToken(email);
        Message message = Message.builder()
            .putData("title", "구매 완료 알림")
            .putData("content", "등록하신 구매 입찰이 낙찰되었습니다.")
            .setToken(token)
            .build();

        send(message);
    }

    public void send(Message message) {
        FirebaseMessaging.getInstance().sendAsync(message);
    }

    public void saveToken(LoginRequest loginRequest) {
        fcmTokenDao.saveToken(loginRequest);
    }

    public void deleteToken(String email) {
        fcmTokenDao.deleteToken(email);
    }

    private boolean hasKey(String email) {
        return fcmTokenDao.hasKey(email);
    }

    private String getToken(String email) {
        return fcmTokenDao.getToken(email);
    }
}
