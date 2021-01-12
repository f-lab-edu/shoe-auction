package com.flab.shoeauction.user.service;

import com.flab.shoeauction.user.utils.AuthenticationSessionUtils;
import com.flab.shoeauction.user.domain.User;
import com.flab.shoeauction.user.dto.UserDto;
import com.flab.shoeauction.user.exception.PasswordMissMatchException;
import com.flab.shoeauction.user.exception.UserDuplicateException;
import com.flab.shoeauction.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

import static com.flab.shoeauction.user.utils.UserConstants.NUMBER_GENERATION_COUNT;


@Service
@Slf4j
@RequiredArgsConstructor
public class SignUpService {
    private final UserRepository userRepository;
    private final AuthenticationSessionUtils authenticationSessionUtils;

    //데이터 조회용. 추후 삭제
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User saveUser(UserDto userDto) {
        signUpValid(userDto);
        authenticationSessionUtils.removeCertificationSession();
        User user = userDto.toUser();
        return userRepository.save(user);
    }

    private void signUpValid(UserDto userDto) {

        if (emailDuplicateCheck(userDto.getEmail()) || nicknameDuplicateCheck(userDto.getNickname())) {
            throw new UserDuplicateException("이메일 또는 닉네임을 확인하세요.");
        }

        if (checkPassword(userDto.getPassword(), userDto.getConfirmPassword())) {
            throw new PasswordMissMatchException("비밀번호가 일치하지 않습니다.");
        }
    }

    private boolean checkPassword(String password, String confirmPassword) {
        return !password.equals(confirmPassword);
    }


    public boolean emailDuplicateCheck(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean nicknameDuplicateCheck(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public boolean certificationNumberInspection(String certificationNumber) {
        return authenticationSessionUtils.getCertificationSession().equals(certificationNumber);
    }

    public void saveAuthenticationNumber() {
        Random rand = new Random();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < NUMBER_GENERATION_COUNT; i++) {
            stringBuffer.append((rand.nextInt(10)));
        }
        authenticationSessionUtils.setCertificationSession(stringBuffer.toString());
        log.info(authenticationSessionUtils.getCertificationSession());
    }
}
