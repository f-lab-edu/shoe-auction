package com.flab.soft.shoeauction.user.service;

import com.flab.soft.shoeauction.user.domain.User;
import com.flab.soft.shoeauction.user.dto.UserDto;
import com.flab.soft.shoeauction.user.exception.UserDuplicateException;
import com.flab.soft.shoeauction.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User saveUser(UserDto signUpDto) {
        if (emailDuplicateCheck(signUpDto.getEmail()) || nicknameDuplicateCheck(signUpDto.getNickname())) {
            throw new UserDuplicateException("이메일 또는 닉네임을 확인하세요.");
        }

        User user = signUpDto.toUser();
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }


    public boolean emailDuplicateCheck(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean nicknameDuplicateCheck(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public String getRandomNumber() {
        Random rand = new Random();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < 6; i++) {
            stringBuffer.append((rand.nextInt(10)));
        }
        return stringBuffer.toString();
    }


    public boolean phoneCertification(String certificationNumber) {
        return getRandomNumber().equals(certificationNumber);
    }
}
