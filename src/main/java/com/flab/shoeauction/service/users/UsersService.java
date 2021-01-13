package com.flab.shoeauction.service.users;

import com.flab.shoeauction.domain.users.UsersRepository;
import com.flab.shoeauction.exception.user.DuplicateEmailException;
import com.flab.shoeauction.exception.user.DuplicateNicknameException;
import com.flab.shoeauction.web.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Service
public class UsersService {

    private final UsersRepository usersRepository;

    public boolean checkEmailDuplicate(String email) {
        return usersRepository.existsByEmail(email);
    }

    public boolean checkNicknameDuplicate(String nickname) {
        return usersRepository.existsByNickname(nickname);
    }

    public void save(UserDto.SaveRequest requestDto) {
        if (checkEmailDuplicate(requestDto.getEmail()))
            throw new DuplicateEmailException();
        if (checkNicknameDuplicate(requestDto.getNickname()))
            throw new DuplicateNicknameException();
        usersRepository.save(requestDto.toEntity());
    }

    // sms 보내기 (log 찍는것으로 대체)
    public void sendSms(String phone, HttpSession session) {
        String randomNumber = makeRandomNumber();
        session.setAttribute(phone, randomNumber);
        log.info("인증번호: " + randomNumber);
    }

    // 6자리 난수 생성
    public String makeRandomNumber() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000));
    }

    // 인증번호가 세션에 발급된 인증번호와 동일한지 체크
    public boolean phoneVerification(String phone, String certificationNumber, HttpSession session) {
        return session.getAttribute(phone).equals(certificationNumber);
    }
}