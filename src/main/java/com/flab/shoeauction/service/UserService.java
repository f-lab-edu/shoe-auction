package com.flab.shoeauction.service;

import com.flab.shoeauction.domain.user.UserRepository;
import com.flab.shoeauction.exception.user.DuplicateEmailException;
import com.flab.shoeauction.exception.user.DuplicateNicknameException;
import com.flab.shoeauction.service.encrytion.EncryptionService;
import com.flab.shoeauction.controller.dto.UserDto.SaveRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final EncryptionService encryptionService;

    public boolean checkEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean checkNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public void save(SaveRequest requestDto) {
        if (checkEmailDuplicate(requestDto.getEmail()))
            throw new DuplicateEmailException();
        if (checkNicknameDuplicate(requestDto.getNickname()))
            throw new DuplicateNicknameException();
        requestDto.passwordEncryption(encryptionService);

        userRepository.save(requestDto.toEntity());
    }
}