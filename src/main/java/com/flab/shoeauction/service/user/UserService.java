package com.flab.shoeauction.service.user;

import com.flab.shoeauction.domain.user.UserRepository;
import com.flab.shoeauction.exception.user.DuplicateEmailException;
import com.flab.shoeauction.exception.user.DuplicateNicknameException;
import com.flab.shoeauction.web.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public boolean checkEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean checkNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public void save(UserDto.SaveRequest requestDto) {
        if (checkEmailDuplicate(requestDto.getEmail()))
            throw new DuplicateEmailException();
        if (checkNicknameDuplicate(requestDto.getNickname()))
            throw new DuplicateNicknameException();
        userRepository.save(requestDto.toEntity());
    }
}