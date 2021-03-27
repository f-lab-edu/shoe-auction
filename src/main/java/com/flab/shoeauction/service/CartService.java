package com.flab.shoeauction.service;

import com.flab.shoeauction.controller.dto.ProductDto.IdRequest;
import com.flab.shoeauction.controller.dto.ProductDto.WishItemResponse;
import com.flab.shoeauction.domain.cart.CartProduct;
import com.flab.shoeauction.domain.cart.CartProductRepository;
import com.flab.shoeauction.domain.product.Product;
import com.flab.shoeauction.domain.product.ProductRepository;
import com.flab.shoeauction.domain.users.user.User;
import com.flab.shoeauction.domain.users.user.UserRepository;
import com.flab.shoeauction.exception.user.DuplicateCartItemException;
import com.flab.shoeauction.exception.user.UserNotFoundException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final UserRepository userRepository;
    private final CartProductRepository cartProductRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void addWishList(String email, IdRequest idRequest) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        Product product = productRepository.findById(idRequest.getId()).orElseThrow();
        CartProduct cartItem = cartProductRepository.save(new CartProduct(user.getCart(), product));

        if (user.checkCartItemDuplicate(cartItem)) {
            throw new DuplicateCartItemException("장바구니 중복");
        }

        user.addCartItem(cartItem);
    }


    public Set<WishItemResponse> getWishList(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        return user.getWishList();
    }

    @Transactional
    public void deleteWishList(IdRequest idRequest) {
        cartProductRepository.deleteById(idRequest.getId());
    }
}
