package com.flab.shoeauction.domain.users.admin;

import com.flab.shoeauction.domain.users.common.UserBase;
import com.flab.shoeauction.domain.users.common.UserLevel;
import javax.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends UserBase {

    @Builder
    public Admin(String email, String password, UserLevel userLevel) {
        this.email = email;
        this.password = password;
        this.userLevel = userLevel;
    }

}
