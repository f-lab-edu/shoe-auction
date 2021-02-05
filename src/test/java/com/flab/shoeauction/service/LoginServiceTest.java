package com.flab.shoeauction.service;

import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.flab.shoeauction.domain.user.User;
import com.flab.shoeauction.domain.user.UserRepository;
import com.flab.shoeauction.exception.user.UserNotFoundException;
import com.flab.shoeauction.service.encrytion.EncryptionService;
import com.flab.shoeauction.controller.dto.UserDto.LoginRequest;
import com.flab.shoeauction.controller.dto.UserDto.SaveRequest;
import com.flab.shoeauction.controller.dto.UserDto.UserInfoDto;
import org.junit.jupiter.api.BeforeEach;
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

    private User user;

    private SaveRequest userDto;


    @BeforeEach
    void setUp() {
        userDto = SaveRequest.builder()
            .email("test@test.com")
            .password("test1234")
            .nickname("17171771")
            .phone("01011112222")
            .build();

        user = userDto.toEntity();


    }

    public LoginRequest createLoginDto() {
        return LoginRequest.of("test@test.com", "test1234");

    }

    @Test
    @DisplayName("로그인 성공 - 아이디와 비밀번호 일치.")
    public void loginSucceed() {
        LoginRequest loginRequest = createLoginDto();

        when(userRepository.existsByEmailAndPassword(loginRequest.getEmail(),
            encryptionService.encrypt(loginRequest.getPassword())))
            .thenReturn(true);

        loginService.existByEmailAndPassword(loginRequest);

        verify(userRepository, atLeastOnce())
            .existsByEmailAndPassword(loginRequest.getEmail(),
                encryptionService.encrypt(loginRequest.getPassword()));
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호가 일치하지 않거나 존재하지 않는 ID를 요청할 경우 UserNotFoundException이 발생한다.")
    public void loginFailed() {
        LoginRequest loginRequest = createLoginDto();

        when(userRepository.existsByEmailAndPassword(loginRequest.getEmail(),
            encryptionService.encrypt(loginRequest.getPassword())))
            .thenReturn(false);

        assertThrows(UserNotFoundException.class,
            () -> loginService
                .existByEmailAndPassword(loginRequest));

        verify(userRepository, atLeastOnce())
            .existsByEmailAndPassword(loginRequest.getEmail(),
                encryptionService.encrypt(loginRequest.getPassword()));
    }

    @Test
    @DisplayName("내 정보 - 로그인 한 상태에서 my-infos를 요청하면 정상적으로 내 정보가 리턴된다.")
    public void getMyInfo_Success() {

        LoginRequest loginRequest = createLoginDto();

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(
            ofNullable(user));

        UserInfoDto userInfoDto = loginService.getCurrentUser(loginRequest.getEmail());

        assertThat(userInfoDto).isNotNull();
        assertThat(userInfoDto.getEmail()).isEqualTo(user.getEmail());
        assertThat(userInfoDto.getNickname()).isEqualTo(user.getNickname());

        verify(userRepository, atLeastOnce())
            .findByEmail(any());
    }

}