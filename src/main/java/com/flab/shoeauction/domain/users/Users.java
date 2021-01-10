package com.flab.shoeauction.domain.users;

import com.flab.shoeauction.common.util.EncryptionUtils;
import com.flab.shoeauction.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@NoArgsConstructor
@Entity
public class Users extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String nickname;

    private String email;

    private String password;

    private String phone;

    @Builder
    public Users(String nickname, String email, String password, String phone) {
        this.nickname = nickname;
        this.email = email;
        this.password = EncryptionUtils.encryptSHA256(password);
        this.phone = phone;
    }
}