package com.flab.shoeauction.domain.user;

import com.flab.shoeauction.controller.dto.UserDto.FindUserResponse;
import com.flab.shoeauction.controller.dto.UserDto.SaveRequest;
import com.flab.shoeauction.controller.dto.UserDto.UserInfoDto;
import com.flab.shoeauction.domain.AddressBook.Address;
import com.flab.shoeauction.domain.AddressBook.AddressBook;
import com.flab.shoeauction.domain.BaseTimeEntity;
import com.flab.shoeauction.exception.user.UnableToChangeNicknameException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@AllArgsConstructor
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "USER_ID")
    private Long id;

    private String nickname;

    private String email;

    private String password;

    private String phone;

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

    public void updateAddressBook(Address address) {
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


}
