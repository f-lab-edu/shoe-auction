package com.flab.shoeauction.service;

import com.flab.shoeauction.domain.user.User;
import com.flab.shoeauction.domain.user.UserRepository;
import com.flab.shoeauction.exception.user.UserNotFoundException;
import com.flab.shoeauction.util.encrytion.EncryptionUtils;
import com.flab.shoeauction.web.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LoginService {

    private final UserRepository userRepository;


    private final EncryptionUtils encryptionUtils;

    public void checkLoginInfo(UserDto.LoginRequest requestDto) {
        requestDto.passwordEncryption(encryptionUtils);
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();

        if (!userRepository.existsByEmailAndPassword(email, password))
            throw new UserNotFoundException();
    }

    public UserDto.LoggedinUserResponse getLoggedinUser(String email) {
        User loggedinUser = userRepository.findByEmail(email);
        UserDto.LoggedinUserResponse loggedinUserResponse = new UserDto.LoggedinUserResponse(loggedinUser);
        return loggedinUserResponse;
    }
}