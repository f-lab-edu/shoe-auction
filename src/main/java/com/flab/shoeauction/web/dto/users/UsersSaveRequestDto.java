package com.flab.shoeauction.web.dto.users;

import com.flab.shoeauction.domain.users.Users;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Getter
@NoArgsConstructor
public class UsersSaveRequestDto {

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
    private String nickname;

    @NotBlank(message = "이메일 주소를 입력해주세요.")
    @Email(message = "올바른 이메일 주소를 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    private String password;

    private String confirmPassword;

    @NotBlank(message = "휴대폰 번호를 입력해주세요.")
    private String phone;

    @Builder
    public UsersSaveRequestDto(String nickName, String email, String password, String confirmPassword, String phone) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.phone = phone;
    }

    public Users toEntity() {
        return Users.builder()
                .nickname(this.nickname)
                .email(this.email)
                .password(this.password)
                .phone(this.phone)
                .build();
    }
}