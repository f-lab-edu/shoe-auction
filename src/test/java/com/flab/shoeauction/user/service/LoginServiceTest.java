package com.flab.shoeauction.user.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.flab.shoeauction.user.dto.UserDto.LoginDto;
import com.flab.shoeauction.user.exception.UserNotFoundException;
import com.flab.shoeauction.user.repository.UserRepository;
import com.flab.shoeauction.user.service.encrytion.EncryptionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    EncryptionService encryptionService;

    @InjectMocks
    LoginService loginService;



    public LoginDto createLoginDto() {
        return LoginDto.of("test@test.com", "test1234");

    }

    @Test
    @DisplayName("로그인 성공 - 아이디와 비밀번호 일치.")
    public void loginSuccess() {
        LoginDto loginDto = createLoginDto();

        when(userRepository.existsByEmailAndPassword(loginDto.getEmail(),
            encryptionService.encrypt(loginDto.getPassword())))
            .thenReturn(true);

        loginService.existByEmailAndPassword(loginDto);

        verify(userRepository, atLeastOnce())
            .existsByEmailAndPassword(loginDto.getEmail(),encryptionService.encrypt(loginDto.getPassword()));
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치 또는 존재하지 않는 아이디")
    public void FailedToLogin() {
        LoginDto loginDto = createLoginDto();

        when(userRepository.existsByEmailAndPassword(loginDto.getEmail(), encryptionService.encrypt(loginDto.getPassword())))
            .thenReturn(false);

        assertThrows(UserNotFoundException.class,
            () -> loginService
                .existByEmailAndPassword(loginDto));

        verify(userRepository, atLeastOnce())
            .existsByEmailAndPassword(loginDto.getEmail(), encryptionService.encrypt(loginDto.getPassword()));
    }
}