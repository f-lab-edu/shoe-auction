package com.flab.shoeauction.domain.users.user;

import com.flab.shoeauction.controller.dto.UserDto.FindUserResponse;
import com.flab.shoeauction.controller.dto.UserDto.SaveRequest;
import com.flab.shoeauction.controller.dto.UserDto.UserInfoDto;
import com.flab.shoeauction.domain.addressBook.Address;
import com.flab.shoeauction.domain.addressBook.AddressBook;
import com.flab.shoeauction.domain.users.common.Account;
import com.flab.shoeauction.domain.users.common.UserBase;
import com.flab.shoeauction.domain.users.common.UserLevel;
import com.flab.shoeauction.exception.user.UnableToChangeNicknameException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends UserBase {

    private String nickname;

    private String phone;

    private boolean emailVerified;

    @Embedded
    private Account account;

    private LocalDateTime nicknameModifiedDate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "USER_ID")
    private List<AddressBook> addressesBook = new ArrayList<>();

    public UserInfoDto toUserInfoDto() {
        return UserInfoDto.builder()
            .email(this.getEmail())
            .nickname(this.getNickname())
            .phone(this.getPhone())
            .account(this.getAccount())
            .emailVerified(this.getEmailVerified())
            .build();
    }

    public FindUserResponse toFindUserDto() {
        return FindUserResponse.builder()
            .email(this.getEmail())
            .phone(this.getPhone())
            .build();
    }

    public void updatePassword(String password) {
        this.password = password;

    }

    public void updateAccount(Account account) {
        this.account = account;
    }

    public void addAddressBook(Address address) {
        this.addressesBook.add(new AddressBook(address));
    }

    public void updateNickname(SaveRequest requestDto) {
        if (canModifiedNickname()) {
            throw new UnableToChangeNicknameException("닉네임은 7일에 한번만 변경할 수 있습니다.");
        }
        String nickname = requestDto.getNickname();
        this.nickname = nickname;
        this.nicknameModifiedDate = LocalDateTime.now();
    }

    private boolean canModifiedNickname() {
        return !(this.nicknameModifiedDate.isBefore(LocalDateTime.now().minusDays(7)));
    }

    public void updateEmailVerified() {
        this.emailVerified = true;
    }

    public boolean getEmailVerified() {
        return this.emailVerified;
    }

    @Builder
    public User(String email, String password, UserLevel userLevel, String nickname, String phone,
        LocalDateTime nicknameModifiedDate, List<AddressBook> addressBooks) {
        super(email, password, userLevel);
        this.nickname = nickname;
        this.phone = phone;
        this.userLevel = userLevel;
        this.nicknameModifiedDate = nicknameModifiedDate;
        this.addressesBook = addressBooks;
    }
}
