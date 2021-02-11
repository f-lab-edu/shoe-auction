package com.flab.shoeauction.service;

import static com.flab.shoeauction.controller.dto.UserDto.ChangePasswordRequest.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.flab.shoeauction.controller.dto.UserDto.ChangePasswordRequest;
import com.flab.shoeauction.controller.dto.UserDto.FindUserResponse;
import com.flab.shoeauction.controller.dto.UserDto.SaveRequest;
import com.flab.shoeauction.domain.user.User;
import com.flab.shoeauction.domain.user.UserRepository;
import com.flab.shoeauction.exception.user.DuplicateEmailException;
import com.flab.shoeauction.exception.user.DuplicateNicknameException;
import com.flab.shoeauction.exception.user.UserNotFoundException;
import com.flab.shoeauction.service.encrytion.EncryptionService;
import java.util.Optional;
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

    @Test
    @DisplayName("비밀번호 찾기 성공 - 전달받은 객체(이메일)가 회원이라면 비밀번호 변경에 성공한다.")
    public void updatePassword() {
        ChangePasswordRequest changePasswordRequest = of("test123.test.com", "test12345");
        User user = createUser().toEntity();

        when(userRepository.findByEmail(changePasswordRequest.getEmail())).thenReturn(Optional.of(user));

        userService.updatePasswordByForget(changePasswordRequest);

        assertThat(user.getPassword()).isEqualTo(changePasswordRequest.getPasswordAfter());

        verify(userRepository, atLeastOnce()).findByEmail(changePasswordRequest.getEmail());
    }

    @Test
    @DisplayName("가입된 이메일 입력시 비밀번호 찾기(재설정)에 필요한 리소스를 리턴한다.")
    public void SuccessToGetUserResource() {
        String email = "test123@test.com";
        User user = createUser().toEntity();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        FindUserResponse userResource = userService.getUserResource(email);

        assertThat(userResource.getEmail()).isEqualTo(user.getEmail());
        assertThat(userResource.getPhone()).isEqualTo(user.getPhone());

    }

    @Test
    @DisplayName("존재하지 않는 이메일 입력시 비밀번호 찾기(재설정)에 필요한 리소스 리턴에 실패한다.")
    public void failToGetUserResource() {

        String email = "non@test.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserResource("non@test.com"));

        verify(userRepository, atLeastOnce()).findByEmail(email);
    }



}