package com.flab.soft.shoeauction.user.domain;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.*;
import org.apache.tomcat.jni.Address;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private Long id;

    private String email;

    private String password;

    private String nickname;

    private String phone;

    private LocalDateTime createdAt;

    @Embedded
    private Address address;

    @Embedded
    private Account account;

    @Getter
    public static class CertificationInfo {
        private String number;
    }

    // TODO: 2021-01-06 : Item 엔티티 생성 후 찜 목록(관심상품) 값 타입 컬렉션 추가하기


}
