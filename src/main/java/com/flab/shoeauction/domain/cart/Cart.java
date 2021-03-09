package com.flab.shoeauction.domain.cart;

import com.flab.shoeauction.domain.product.Product;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany
    @JoinColumn(name = "CART_ID")
    private Set<Product> wishList = new HashSet<>();

}