package com.flab.shoeauction.domain.AddressBook;

import com.flab.shoeauction.controller.dto.UserDto.ChangeAddressRequest;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddressBook {

    @Id
    @GeneratedValue
    @Column(name="ADDRESSBOOK_ID")
    private Long id;

    @Embedded
    private Address address;

    @Builder
    public AddressBook(Address address) {
        this.address = address;
    }

    public void updateAddressBook(ChangeAddressRequest requestDto){
        address.updateAddress(requestDto);
    }


}
