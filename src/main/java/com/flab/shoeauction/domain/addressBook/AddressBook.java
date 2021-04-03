package com.flab.shoeauction.domain.addressBook;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class AddressBook {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ADDRESSBOOK_ID")
    private List<Address> addressList = new ArrayList<>();

    public void addAddress(Address address) {
        addressList.add(address);
    }

    public void deleteAddress(Address address) {
        addressList.remove(address);
    }

    public Address findAddress(Long addressId) {
        return addressList.stream()
            .filter(address -> address.getId() == addressId)
            .findAny()
            .orElseThrow();
    }
}