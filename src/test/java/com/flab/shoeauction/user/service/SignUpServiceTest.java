package com.flab.shoeauction.user.service;

import com.flab.shoeauction.user.dto.UserDto;
import com.flab.shoeauction.user.exception.UserDuplicateException;
import com.flab.shoeauction.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @ExtendWith : Junit5의 확장 어노테이션을 사용할 수 있다.
 * @Mock : mock 객체를 생성한다.
 * @InjectMock : @Mock이 붙은 객체를 @InjectMock이 붙은 객체에 주입시킬 수 있다.
 */
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
                .phone("01011112222")
                .nickname("17171771")
                .build();
    }


    @Test
    @DisplayName("회원가입 실패 - 닉네임 중복")
    public void nicknameDuplicate() {
        when(userRepository.existsByNickname("17171771")).thenReturn(true);

        assertThrows(UserDuplicateException.class, () -> signUpService.saveUser(userDto));

        verify(userRepository, atLeastOnce()).existsByNickname("17171771");
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    public void emailDuplicate() {
        when(userRepository.existsByEmail("test123@test.com")).thenReturn(true);

        assertThrows(UserDuplicateException.class, () -> signUpService.saveUser(userDto));

        verify(userRepository, atLeastOnce()).existsByEmail("test123@test.com");
    }
}