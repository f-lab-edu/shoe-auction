package com.flab.shoeauction.user.dto;

import com.flab.shoeauction.user.domain.Account;
import com.flab.shoeauction.user.domain.Address;
import com.flab.shoeauction.user.service.encrytion.EncryptionService;
import com.flab.shoeauction.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Builder
public class UserDto {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Length(min = 8, max = 50)
    private String password;

    @NotBlank
    @Length(min = 3, max = 20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{3,20}$")
    private String nickname;

    @NotBlank
    @Length(min = 10, max = 11)
    private String phone;


    public void passwordEncryption(EncryptionService encryptionService) {
        this.password = encryptionService.encrypt(password);
    }

    public User toUser() {
        return User.builder()
            .email(this.email)
            .password(this.password)
            .nickname(this.nickname)
            .phone(this.phone)
            .build();
    }


    @Getter
    @AllArgsConstructor
    public static class LoginDto {

        private String email;
        private String password;

        public static LoginDto of(String email, String password) {
            return new LoginDto(email, password);
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

        public static UserInfoDto of(String email, String nickname, String phone, Address address, Account account){
            return new UserInfoDto(email,nickname,phone,address,account);
        }
    }

}