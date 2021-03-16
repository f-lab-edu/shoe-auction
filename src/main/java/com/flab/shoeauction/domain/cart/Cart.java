package com.flab.shoeauction.domain.cart;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "cart")
    private Set<CartProduct> wishList = new HashSet<>();

    public void addCartProducts(CartProduct cartItem) {
        wishList.add(cartItem);
    }


}