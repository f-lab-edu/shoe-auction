package com.flab.shoeauction.user.dto;
import com.flab.shoeauction.common.utils.EncryptionUtils;
import com.flab.shoeauction.user.domain.User;
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
    @Length(min = 8, max = 50)
    private String confirmPassword;


    @NotBlank
    @Length(min = 3, max = 20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{3,20}$")
    private String nickname;

    @NotBlank
    @Length(min = 10, max = 11)
    private String phone;


    public User toUser() {
        return User.builder()
                .email(this.email)
                .password(EncryptionUtils.encryptSHA256(this.password))
                .nickname(this.nickname)
                .phone(this.phone)
                .build();
    }
    @Getter
    public static class CertificationInfo {
        private String certificationNumber;
    }

}