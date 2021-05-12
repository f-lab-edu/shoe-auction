package com.flab.shoeauction.controller.dto;

import com.flab.shoeauction.domain.addressBook.Address;
import com.flab.shoeauction.domain.trade.TradeStatus;
import com.flab.shoeauction.domain.users.common.Account;
import com.flab.shoeauction.domain.users.common.UserLevel;
import com.flab.shoeauction.domain.users.common.UserStatus;
import com.flab.shoeauction.domain.users.user.User;
import com.flab.shoeauction.service.encrytion.EncryptionService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
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
                .userLevel(UserLevel.ADMIN)
                .userStatus(UserStatus.NORMAL)
                .point(0L)
                .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SmsCertificationRequest {

        private String phone;
        private String certificationNumber;

        @Builder
        public SmsCertificationRequest(String phone, String certificationNumber) {
            this.phone = phone;
            this.certificationNumber = certificationNumber;
        }

    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class EmailCertificationRequest {

        private String email;
        private String certificationNumber;

        @Builder
        public EmailCertificationRequest(String email, String certificationNumber) {
            this.email = email;
            this.certificationNumber = certificationNumber;
        }

    }


    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LoginRequest {

        private String email;
        private String password;
        private String token;

        public void passwordEncryption(EncryptionService encryptionService) {
            this.password = encryptionService.encrypt(password);
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UserInfoDto {

        private String email;
        private String nickname;
        private String phone;
        private UserLevel userLevel;

        @Builder
        public UserInfoDto(String email, String nickname, String phone,
            UserLevel userLevel) {
            this.email = email;
            this.nickname = nickname;
            this.phone = phone;
            this.userLevel = userLevel;
        }

    }

    @Getter
    public static class FindUserResponse {

        private String email;
        private String phone;

        @Builder
        public FindUserResponse(String email, String phone) {
            this.email = email;
            this.phone = phone;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
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

        @Builder

        public ChangePasswordRequest(String email,
            @NotBlank(message = "비밀번호를 입력해주세요.") @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.") String passwordAfter,
            String passwordBefore) {
            this.email = email;
            this.passwordAfter = passwordAfter;
            this.passwordBefore = passwordBefore;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PasswordRequest {

        private String password;

        @Builder
        public PasswordRequest(String password) {
            this.password = password;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class UserListResponse {

        private Long id;
        private String email;
        private UserLevel userLevel;

        @Builder
        public UserListResponse(Long id, String email, UserLevel userLevel) {
            this.id = id;
            this.email = email;
            this.userLevel = userLevel;
        }
    }

    @Getter
    public static class UserSearchCondition {

        private Long id;
        private String email;
        private UserLevel userLevel;

        @Builder
        public UserSearchCondition(Long id, String email, UserLevel userLevel) {
            this.id = id;
            this.email = email;
            this.userLevel = userLevel;
        }
    }

    @Getter
    public static class UserDetailsResponse {

        private Long id;
        private String email;
        private String nickname;
        private String phone;
        private Account account;
        private LocalDateTime modifiedDate;
        private LocalDateTime createDate;
        private UserLevel userLevel;
        private UserStatus userStatus;

        @Builder
        public UserDetailsResponse(Long id, String email, String nickname, String phone,
            Account account, LocalDateTime modifiedDate, LocalDateTime createDate,
            UserLevel userLevel, UserStatus userStatus) {
            this.id = id;
            this.email = email;
            this.nickname = nickname;
            this.phone = phone;
            this.account = account;
            this.modifiedDate = modifiedDate;
            this.createDate = createDate;
            this.userLevel = userLevel;
            this.userStatus = userStatus;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UserBanRequest {

        private Long id;
        private UserStatus userStatus;

        @Builder
        public UserBanRequest(Long id, UserStatus userStatus) {
            this.id = id;
            this.userStatus = userStatus;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class TradeUserInfo {

        private List<Address> addressBook = new ArrayList<>();
        private Account account;

        @Builder
        public TradeUserInfo(List<Address> addressBook,
            Account account) {
            this.addressBook = addressBook;
            this.account = account;
        }
    }


}