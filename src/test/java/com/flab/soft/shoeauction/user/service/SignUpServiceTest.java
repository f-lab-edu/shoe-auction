package com.flab.soft.shoeauction.user.service;
import com.flab.soft.shoeauction.user.dto.UserDto;
import com.flab.soft.shoeauction.user.exception.PasswordMissMatchException;
import com.flab.soft.shoeauction.user.exception.UserDuplicateException;
import com.flab.soft.shoeauction.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignUpServiceTest {


    @Mock
    UserRepository userRepository;
    @InjectMocks
    SignUpService signUpService;

    private UserDto userDto;

    @BeforeEach
    public void addUser() {
        userDto = UserDto.builder()
                .email("test123@test.com")
                .password("test1234")
                .confirmPassword("test1234")
                .phone("01011112222")
                .nickname("17171771")
                .build();
    }


    @Test
    @DisplayName("닉네임 중복으로 인한 회원가입 실패")
    public void nicknameDuplicate() {
        when(userRepository.existsByNickname("17171771")).thenReturn(true);

        assertThrows(UserDuplicateException.class, () -> signUpService.saveUser(userDto));

        verify(userRepository, atLeastOnce()).existsByNickname("17171771");
    }

    @Test
    @DisplayName("이메일 중복으로 인한 회원가입 실패")
    public void emailDuplicate() {
        when(userRepository.existsByEmail("test123@test.com")).thenReturn(true);

        assertThrows(UserDuplicateException.class, () -> signUpService.saveUser(userDto));

        verify(userRepository, atLeastOnce()).existsByEmail("test123@test.com");
    }


    @Test
    @DisplayName("비밀번호 불일치로 인한 회원가입 실패")
    public void passwordMissMatch() {
        UserDto userDto2 = UserDto.builder()
                .email("test123@test.com")
                .password("test1234")
                .password("test123")
                .phone("01011112222")
                .nickname("17171771")
                .certificationNumber("1234")
                .build();

        assertThrows(PasswordMissMatchException.class, () ->
                signUpService.saveUser(userDto2));
    }



}