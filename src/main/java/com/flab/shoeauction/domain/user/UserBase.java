package com.flab.shoeauction.domain.user;

import com.flab.shoeauction.domain.BaseTimeEntity;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorColumn
public abstract class UserBase extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "USER_ID")
    private Long id;

    protected String email;

    protected String password;

    @Enumerated(EnumType.STRING)
    protected UserLevel userLevel;

    public UserBase(String email, String password, UserLevel userLevel) {
        this.email = email;
        this.password = password;
        this.userLevel = userLevel;
    }
}
