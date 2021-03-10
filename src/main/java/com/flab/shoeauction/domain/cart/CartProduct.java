package com.flab.shoeauction.domain.cart;

import com.flab.shoeauction.controller.dto.ProductDto.WishItemResponse;
import com.flab.shoeauction.domain.product.Product;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * CART에는 여러 PRODUCT를 담을 수 있고, PRODUCT 또한 여러 CART에 포함될 수 있다. 따라서 ManyToMany를 형성한다.
 * ManyToMany의 경우 정규화를 통해 1:N , N:1로 처리해야 한다.  따라서 중간테이블인 CartProduct라는 중간테이블을 생성해야 한다.
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartProduct {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "CART_ID")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    @Builder
    public CartProduct(Cart cart, Product product) {
        this.cart = cart;
        this.product = product;
    }

    public Long getProductId() {
        return product.getId();
    }

    public WishItemResponse toWishItemDto() {
        return WishItemResponse.builder()
            .id(this.id)
            .productId(product.getId())
            .nameKor(product.getNameKor())
            .nameEng(product.getNameEng())
            .brand(product.getBrand())
            .build();
    }
}
