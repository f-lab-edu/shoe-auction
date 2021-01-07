package com.flab.soft.shoeauction.user.service;

import com.flab.soft.shoeauction.common.util.SessionUtils;
import com.flab.soft.shoeauction.user.domain.User;
import com.flab.soft.shoeauction.user.dto.UserDto;
import com.flab.soft.shoeauction.user.exception.CertificationNumberMissMatchException;
import com.flab.soft.shoeauction.user.exception.PasswordMissMatchException;
import com.flab.soft.shoeauction.user.exception.UserDuplicateException;
import com.flab.soft.shoeauction.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * 휴대폰 인증시 전송받은 인증번호를 session에 저장하여 User가 입력한 인증번호화 일치하는지 확인
 */
@Service
@RequiredArgsConstructor
public class SignUpService {
    private final UserRepository userRepository;
    private final SessionUtils sessionUtils;

    //데이터 조회용. 추후 삭제
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User saveUser(UserDto userDto) {
        signUpValid(userDto);
        sessionUtils.removeCertificationSession();
        User user = userDto.toUser();
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    private void signUpValid(UserDto userDto) {

        if (emailDuplicateCheck(userDto.getEmail()) || nicknameDuplicateCheck(userDto.getNickname())) {
            throw new UserDuplicateException("이메일 또는 닉네임을 확인하세요.");
        }

        if (checkPassword(userDto.getPassword(), userDto.getConfirmPassword())) {
            throw new PasswordMissMatchException("비밀번호가 일치하지 않습니다.");
        }

        if (certificationNumberInspection(userDto.getCertificationNumber())) {
            throw new CertificationNumberMissMatchException("인증번호가 일치하지 않습니다.");
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

    private boolean certificationNumberInspection(String certificationNumber) {
        return !sessionUtils.getCertificationSession().equals(certificationNumber);
    }

    public void saveAuthenticationNumber() {
        Random rand = new Random();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < 6; i++) {
            stringBuffer.append((rand.nextInt(10)));
        }
        sessionUtils.setCertificationSession(stringBuffer.toString());
    }
}
