package com.flab.shoeauction.user.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.flab.shoeauction.common.utils.encrytion.EncryptionUtils;
import com.flab.shoeauction.user.dto.UserDto;
import com.flab.shoeauction.user.exception.EmailDuplicateException;
import com.flab.shoeauction.user.exception.NicknameDuplicateException;
import com.flab.shoeauction.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @ExtendWith : Junit5의 확장 어노테이션을 사용할 수 있다.
 * @Mock : mock 객체를 생성한다.
 * @InjectMock : @Mock이 붙은 객체를 @InjectMock이 붙은 객체에 주입시킬 수 있다.
 */
@ExtendWith(MockitoExtension.class)
class SignUpServiceTest {


    @Mock
    UserRepository userRepository;
    @Mock
    EncryptionUtils encryptionUtils;
    @InjectMocks
    SignUpService signUpService;


    public UserDto createUser() {
        UserDto userDto = UserDto.builder()
            .email("test123@test.com")
            .password("test1234")
            .phone("01011112222")
            .nickname("17171771")
            .build();
        return userDto;
    }

    @Test
    @DisplayName("회원가입 성공")
    public void successfulSignUp() {
        UserDto userDto = createUser();
        when(userRepository.existsByNickname("17171771")).thenReturn(false);
        when(userRepository.existsByEmail("test123@test.com")).thenReturn(false);

        signUpService.saveUser(userDto);
        verify(userRepository).save(any());
    }


    @Test
    @DisplayName("닉네임 중복으로 인한 회원가입 실패")
    public void nicknameDuplicate() {
        UserDto userDto = createUser();
        when(userRepository.existsByNickname("17171771")).thenReturn(true);

        assertThrows(NicknameDuplicateException.class, () -> signUpService.saveUser(userDto));

        verify(userRepository, atLeastOnce()).existsByNickname("17171771");
    }

    @Test
    @DisplayName("이메일 중복으로 인한 회원가입 실패")
    public void emailDuplicate() {
        UserDto userDto = createUser();

        when(userRepository.existsByEmail("test123@test.com")).thenReturn(true);

        assertThrows(EmailDuplicateException.class, () -> signUpService.saveUser(userDto));

        verify(userRepository, atLeastOnce()).existsByEmail("test123@test.com");
    }

}