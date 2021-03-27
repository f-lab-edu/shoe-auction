package com.flab.shoeauction.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.flab.shoeauction.controller.dto.ProductDto.IdRequest;
import com.flab.shoeauction.controller.dto.UserDto;
import com.flab.shoeauction.domain.brand.Brand;
import com.flab.shoeauction.domain.cart.Cart;
import com.flab.shoeauction.domain.cart.CartProduct;
import com.flab.shoeauction.domain.cart.CartProductRepository;
import com.flab.shoeauction.domain.product.Currency;
import com.flab.shoeauction.domain.product.Product;
import com.flab.shoeauction.domain.product.ProductRepository;
import com.flab.shoeauction.domain.product.SizeClassification;
import com.flab.shoeauction.domain.product.SizeUnit;
import com.flab.shoeauction.domain.users.user.User;
import com.flab.shoeauction.domain.users.user.UserRepository;
import com.flab.shoeauction.exception.user.DuplicateCartItemException;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    CartProductRepository cartProductRepository;
    @Mock
    ProductRepository productRepository;

    @InjectMocks
    CartService cartService;

    private UserDto.SaveRequest createUserDto() {
        UserDto.SaveRequest saveRequest = UserDto.SaveRequest.builder()
            .email("test123@test.com")
            .password("test1234")
            .phone("01011112222")
            .nickname("17171771")
            .build();
        return saveRequest;
    }

    private Product createProduct() {
        Brand brand = new Brand(3L, "나이키", "nike", "123123213", "234234234");
        return Product.builder()
            .nameKor("조던")
            .nameEng("jordan")
            .modelNumber("12345")
            .color("RED")
            .releaseDate(LocalDate.now())
            .releasePrice(200000)
            .currency(Currency.KRW)
            .sizeClassification(SizeClassification.MENS)
            .sizeUnit(SizeUnit.MM)
            .minSize(230)
            .maxSize(320)
            .sizeGap(10)
            .brand(brand)
            .build();
    }

    @Test
    @DisplayName("중복된 상품이 아닌경우 위시리스트에 상품을 추가한다.")
    public void addWishList() {
        User user = createUserDto().toEntity();
        Product product = createProduct();
        Cart cart = new Cart();
        user.createCart(cart);
        CartProduct cartProduct = CartProduct.builder()
            .cart(cart)
            .product(product)
            .build();
        String email = "test123@test.com";
        IdRequest idRequest = IdRequest
            .builder()
            .id(2L)
            .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(productRepository.findById(idRequest.getId())).thenReturn(Optional.of(product));
        when(cartProductRepository.save(any())).thenReturn(cartProduct);

        cartService.addWishList(email, idRequest);
        assertThat(user.getWishList().size()).isEqualTo(1);
        verify(userRepository, atLeastOnce()).findByEmail(email);
        verify(productRepository, atLeastOnce()).findById(idRequest.getId());
        verify(cartProductRepository, atLeastOnce()).save(any());

    }
    @Test
    @DisplayName("장바구니에 이미 동일한 상품이 존재하는 경우  DuplicateCartItemException이 발생한다.")
    public void failToAddWishList() {
        User user = createUserDto().toEntity();
        Product product = createProduct();
        Cart cart = new Cart();
        user.createCart(cart);
        CartProduct cartProduct = CartProduct.builder()
            .cart(cart)
            .product(product)
            .build();
        String email = "test123@test.com";
        IdRequest idRequest = IdRequest
            .builder()
            .id(2L)
            .build();
        user.addCartItem(cartProduct);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(productRepository.findById(idRequest.getId())).thenReturn(Optional.of(product));
        when(cartProductRepository.save(any())).thenReturn(cartProduct);

        assertThrows(
            DuplicateCartItemException.class, () -> cartService.addWishList(email, idRequest));
        verify(userRepository, atLeastOnce()).findByEmail(email);
        verify(productRepository, atLeastOnce()).findById(idRequest.getId());
        verify(cartProductRepository, atLeastOnce()).save(any());
    }

    @Test
    @DisplayName("회원의 장바구니를 조회한다.")
    public void getWishList() {
        User user = createUserDto().toEntity();
        Cart cart = new Cart();
        user.createCart(cart);
        String email= "jungkh405@naver.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        cartService.getWishList(email);

        assertThat(user.getWishList().size()).isEqualTo(0);
        verify(userRepository, atLeastOnce()).findByEmail(email);
    }

    @Test
    @DisplayName("카트에서 해당 상품을 삭제한다.")
    public void deleteWishList() {
        IdRequest idRequest = IdRequest.builder()
            .id(1L).build();

        cartService.deleteWishList(idRequest);

        verify(cartProductRepository, atLeastOnce()).deleteById(any());
    }
}