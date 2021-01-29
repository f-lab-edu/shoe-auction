package com.flab.shoeauction.domain.user;

import com.flab.shoeauction.domain.BaseEntity;
import com.flab.shoeauction.web.dto.UserDto.UserInfoDto;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String email;

    private String password;

    private String nickname;

    private String phone;

    @Embedded
    private Address address;

    @Embedded
    private Account account;


    public  UserInfoDto toUserInfoDto() {
        return UserInfoDto.builder()
            .email(this.getEmail())
            .nickname(this.getNickname())
            .phone(this.getPhone())
            .address(this.getAddress())
            .account(this.getAccount())
            .build();
    }

    // TODO: 2021-01-06 : Item 엔티티 생성 후 찜 목록(관심상품) 값 타입 컬렉션 추가하기


}
