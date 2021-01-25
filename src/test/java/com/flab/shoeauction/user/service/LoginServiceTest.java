package com.flab.shoeauction.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.flab.shoeauction.user.domain.User;
import com.flab.shoeauction.user.dto.UserDto;
import com.flab.shoeauction.user.dto.UserDto.LoginDto;
import com.flab.shoeauction.user.dto.UserDto.UserInfoDto;
import com.flab.shoeauction.user.exception.UserNotFoundException;
import com.flab.shoeauction.user.repository.UserRepository;
import com.flab.shoeauction.user.service.encrytion.EncryptionService;
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

    private UserDto userDto;


    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
            .email("test@test.com")
            .password("test1234")
            .nickname("17171771")
            .phone("01011112222")
            .build();

        user = userDto.toUser();

    }


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
            .existsByEmailAndPassword(loginDto.getEmail(),
                encryptionService.encrypt(loginDto.getPassword()));
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호가 일치하지 않거나 존재하지 않는 ID를 요청할 경우 UserNotFoundException이 발생한다.")
    public void FailedToLogin() {
        LoginDto loginDto = createLoginDto();

        when(userRepository.existsByEmailAndPassword(loginDto.getEmail(),
            encryptionService.encrypt(loginDto.getPassword())))
            .thenReturn(false);

        assertThrows(UserNotFoundException.class,
            () -> loginService
                .existByEmailAndPassword(loginDto));

        verify(userRepository, atLeastOnce())
            .existsByEmailAndPassword(loginDto.getEmail(),
                encryptionService.encrypt(loginDto.getPassword()));
    }

    @Test
    @DisplayName("내 정보 - 로그인 한 상태에서 my-info를 요청하면 정상적으로 내 정보가 리턴된다.")
    public void getMyInfo_Success() {

        LoginDto loginDto = createLoginDto();

        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(user);

        UserInfoDto userInfoDto = loginService.findByEmail(loginDto.getEmail());

        assertThat(userInfoDto).isNotNull();
        assertThat(userInfoDto.getEmail()).isEqualTo(user.getEmail());
        assertThat(userInfoDto.getNickname()).isEqualTo(user.getNickname());

        verify(userRepository, atLeastOnce())
            .findByEmail(any());

    }

}