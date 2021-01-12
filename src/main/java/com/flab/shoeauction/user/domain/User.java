package com.flab.shoeauction.user.domain;

import com.flab.shoeauction.common.baseEntity.BaseEntity;
import lombok.*;
import org.apache.tomcat.jni.Address;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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



    // TODO: 2021-01-06 : Item 엔티티 생성 후 찜 목록(관심상품) 값 타입 컬렉션 추가하기


}
