package com.flab.shoeauction.service;

import com.flab.shoeauction.service.encrytion.EncryptionService;
import com.flab.shoeauction.domain.user.User;
import com.flab.shoeauction.web.dto.UserDto;
import com.flab.shoeauction.exception.user.EmailDuplicateException;
import com.flab.shoeauction.exception.user.NicknameDuplicateException;
import com.flab.shoeauction.domain.user.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class SignUpService {

    private final UserRepository userRepository;
    private final EncryptionService encryptionService;

    //데이터 조회용. 추후 삭제
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User saveUser(UserDto userDto) {
        if (emailDuplicateCheck(userDto.getEmail())) {
            throw new EmailDuplicateException("이미 존재하는 email 입니다. 다른 email을 사용해주세요.");
        }
        if (nicknameDuplicateCheck(userDto.getNickname())) {
            throw new NicknameDuplicateException("이미 존재하는 닉네임 입니다. 다른 닉네임을 사용해주세요.");
        }

        userDto.passwordEncryption(encryptionService);
        return userRepository.save(userDto.toUser());
    }

    public boolean emailDuplicateCheck(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean nicknameDuplicateCheck(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}
