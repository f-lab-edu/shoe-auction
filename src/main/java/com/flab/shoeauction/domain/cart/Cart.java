package com.flab.shoeauction.domain.cart;

import com.flab.shoeauction.domain.users.user.User;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(mappedBy = "user")
    private User user;

    @OneToMany(mappedBy = "cart")
    private Set<CartProduct> wishList = new HashSet<>();

    public void addCartProducts(CartProduct cartItem) {
        wishList.add(cartItem);
    }
}