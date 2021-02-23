package com.flab.shoeauction.controller.dto;

import com.flab.shoeauction.domain.addressBook.Address;
import com.flab.shoeauction.domain.users.common.Account;
import com.flab.shoeauction.domain.users.user.User;
import com.flab.shoeauction.domain.users.common.UserLevel;
import com.flab.shoeauction.service.encrytion.EncryptionService;
import java.time.LocalDateTime;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
public class UserDto {

    @Getter
    @NoArgsConstructor
    public static class SaveRequest {

        @NotBlank(message = "닉네임을 입력해주세요.")
        @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
        private String nickname;

        @NotBlank(message = "이메일 주소를 입력해주세요.")
        @Email(message = "올바른 이메일 주소를 입력해주세요.")
        private String email;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
        private String password;

        @NotBlank(message = "휴대폰 번호를 입력해주세요.")
        @Pattern(regexp = "(01[016789])(\\d{3,4})(\\d{4})", message = "올바른 휴대폰 번호를 입력해주세요.")
        private String phone;

        @Builder
        public SaveRequest(String nickname, String email, String password, String phone) {
            this.nickname = nickname;
            this.email = email;
            this.password = password;
            this.phone = phone;
        }

        public void passwordEncryption(EncryptionService encryptionService) {
            this.password = encryptionService.encrypt(password);
        }

        public User toEntity() {

            return User.builder()
                .nickname(this.nickname)
                .email(this.email)
                .password(this.password)
                .nicknameModifiedDate(LocalDateTime.now())
                .phone(this.phone)
                .userLevel(UserLevel.USER)
                .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class SmsCertificationRequest {

        private String phone;
        private String certificationNumber;
    }

    @Getter
    @NoArgsConstructor
    public static class EmailCertificationRequest {

        private String email;
        private String certificationNumber;
    }

    @Getter
    @AllArgsConstructor
    public static class LoginRequest {

        private String email;
        private String password;

        public static LoginRequest of(String email, String password) {
            return new LoginRequest(email, password);
        }

        public void passwordEncryption(EncryptionService encryptionService) {
            this.password = encryptionService.encrypt(password);
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class UserInfoDto {

        private String email;
        private String nickname;
        private String phone;
        private Address address;
        private Account account;
        private boolean emailVerified;

        public static UserInfoDto of(String email, String nickname, String phone, Address address,
            Account account, boolean emailVerified) {
            return new UserInfoDto(email, nickname, phone, address, account, emailVerified);
        }
    }

    @Getter
    @Builder
    public static class FindUserResponse {

        private String email;
        private String phone;
    }

    @Getter
    @AllArgsConstructor
    public static class ChangePasswordRequest {

        private String email;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
        private String passwordAfter;

        private String passwordBefore;

        public void passwordEncryption(EncryptionService encryptionService) {
            this.passwordAfter = encryptionService.encrypt(passwordAfter);
            this.passwordBefore = encryptionService.encrypt(passwordBefore);
        }

        public static ChangePasswordRequest of(String email, String passwordAfter, String passwordBefore) {
            return new ChangePasswordRequest(email, passwordAfter,passwordBefore);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class PasswordRequest {

        private String password;
    }
}