package com.flab.shoeauction.domain.addressBook;

import com.flab.shoeauction.controller.dto.AddressBookDto;
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
    private Long id;

    @Embedded
    private Address address;

    @Builder
    public AddressBook(Address address) {
        this.address = address;
    }

    public void updateAddressBook(AddressBookDto requestDto) {
        address.updateAddress(requestDto);
    }


}
