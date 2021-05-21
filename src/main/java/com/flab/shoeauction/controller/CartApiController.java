package com.flab.shoeauction.controller;

import com.flab.shoeauction.common.annotation.CurrentUser;
import com.flab.shoeauction.common.annotation.LoginCheck;
import com.flab.shoeauction.controller.dto.ProductDto.IdRequest;
import com.flab.shoeauction.controller.dto.ProductDto.WishItemResponse;
import com.flab.shoeauction.service.CartService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("carts")
public class CartApiController {

    private final CartService cartService;

    @LoginCheck
    @GetMapping
    public Set<WishItemResponse> getWishList(@CurrentUser String email) {
        return cartService.getWishList(email);
    }

    @LoginCheck
    @PostMapping
    public void addWishList(@CurrentUser String email, @RequestBody IdRequest idRequest) {
        cartService.addWishList(email, idRequest);
    }

    @LoginCheck
    @DeleteMapping
    public void deleteWishList(@RequestBody IdRequest idRequest) {
        cartService.deleteWishList(idRequest);
    }
}
