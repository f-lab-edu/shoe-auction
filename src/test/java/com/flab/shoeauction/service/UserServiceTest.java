package com.flab.shoeauction.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.flab.shoeauction.controller.dto.UserDto.SaveRequest;
import com.flab.shoeauction.domain.user.UserRepository;
import com.flab.shoeauction.exception.user.DuplicateEmailException;
import com.flab.shoeauction.exception.user.DuplicateNicknameException;
import com.flab.shoeauction.exception.user.WrongPasswordException;
import com.flab.shoeauction.service.encrytion.EncryptionService;
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
class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    EncryptionService encryptionService;
    @InjectMocks
    UserService userService;

    private SaveRequest createUser() {
        SaveRequest saveRequest = SaveRequest.builder()
            .email("test123@test.com")
            .password("test1234")
            .phone("01011112222")
            .nickname("17171771")
            .build();
        return saveRequest;
    }

    @Test
    @DisplayName("이메일과 닉네임이 중복되지 않으면 회원가입에 성공한다.")
    public void signUp_Successful() {

        SaveRequest saveRequest = createUser();

        when(userRepository.existsByEmail("test123@test.com")).thenReturn(false);
        when(userRepository.existsByNickname("17171771")).thenReturn(false);

        userService.save(saveRequest);

        verify(userRepository, atLeastOnce()).save(any());
    }

    @Test
    @DisplayName("이메일 중복으로 회원가입에 실패한다.")
    public void emailDuplicate() {
        SaveRequest saveRequest = createUser();
        when(userRepository.existsByEmail("test123@test.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> userService.save(saveRequest));

        verify(userRepository, atLeastOnce()).existsByEmail("test123@test.com");
    }

    @Test
    @DisplayName("닉네임 중복으로 회원가입에 실패한다.")
    public void nicknameDuplicate() {
        SaveRequest saveRequest = createUser();
        when(userRepository.existsByNickname("17171771")).thenReturn(true);

        assertThrows(DuplicateNicknameException.class, () -> userService.save(saveRequest));

        verify(userRepository, atLeastOnce()).existsByNickname("17171771");
    }

    @DisplayName("비밀번호가 일치하여 회원 탈퇴 성공한다.")
    @Test
    public void deleteSuccess() {
        SaveRequest saveRequest = createUser();
        String email = saveRequest.getEmail();
        String password = saveRequest.getPassword();

        when(userRepository.existsByEmailAndPassword(email, encryptionService.encrypt(password)))
            .thenReturn(true);

        userService.delete(email, password);

        verify(userRepository, atLeastOnce()).deleteByEmail(email);
    }

    @DisplayName("비밀번호가 일치하지 않아 회원 탈퇴 실패한다.")
    @Test
    public void deleteFailure() {
        SaveRequest saveRequest = createUser();
        String email = saveRequest.getEmail();
        String password = saveRequest.getPassword();

        when(userRepository.existsByEmailAndPassword(email, encryptionService.encrypt(password)))
            .thenReturn(false);

        assertThrows(WrongPasswordException.class, () -> userService.delete(email, password));

        verify(userRepository, never()).deleteByEmail(email);
    }
}