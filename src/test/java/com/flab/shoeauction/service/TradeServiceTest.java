package com.flab.shoeauction.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.flab.shoeauction.controller.dto.BrandDto.BrandInfo;
import com.flab.shoeauction.controller.dto.ProductDto;
import com.flab.shoeauction.controller.dto.TradeDto;
import com.flab.shoeauction.controller.dto.TradeDto.TradeResource;
import com.flab.shoeauction.domain.addressBook.Address;
import com.flab.shoeauction.domain.product.Currency;
import com.flab.shoeauction.domain.product.Product;
import com.flab.shoeauction.domain.product.ProductRepository;
import com.flab.shoeauction.domain.product.SizeClassification;
import com.flab.shoeauction.domain.product.SizeUnit;
import com.flab.shoeauction.domain.trade.Trade;
import com.flab.shoeauction.domain.trade.TradeStatus;
import com.flab.shoeauction.domain.users.common.UserLevel;
import com.flab.shoeauction.domain.users.common.UserStatus;
import com.flab.shoeauction.domain.users.user.User;
import com.flab.shoeauction.domain.users.user.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private TradeService tradeService;

    private User createUser() {
        return User.builder()
            .id(10L)
            .email("test123@test.com")
            .password("test1234")
            .phone("01011112222")
            .nickname("17171771")
            .nicknameModifiedDate(LocalDateTime.now())
            .userLevel(UserLevel.ADMIN)
            .userStatus(UserStatus.NORMAL)
            .build();
    }

    private User createAnotherUser() {
        return User.builder()
            .id(11L)
            .email("anotherUser@test.com")
            .password("test1234")
            .phone("01020180103")
            .nickname("18181881")
            .nicknameModifiedDate(LocalDateTime.now())
            .userLevel(UserLevel.ADMIN)
            .userStatus(UserStatus.NORMAL)
            .build();
    }

    private ProductDto.SaveRequest createProductDto() {
        return ProductDto.SaveRequest.builder()
            .nameKor("덩크 로우")
            .nameEng("Dunk Low")
            .modelNumber("DD1391-100")
            .color("WHITE/BLACK")
            .releaseDate(LocalDate.of(2021, 01, 04))
            .releasePrice(119000)
            .currency(Currency.KRW)
            .sizeClassification(SizeClassification.MENS)
            .sizeUnit(SizeUnit.MM)
            .minSize(240)
            .maxSize(320)
            .sizeGap(5)
            .brand(createBrandInfo())
            .originImagePath(
                "https://shoeauction-brands-origin.s3.ap-northeast-2.amazonaws.com/brand.png")
            .build();
    }

    private List<Trade> createTrades() {

        User user = createUser();
        User anotherUser = createAnotherUser();
        Address address = new Address(1L, "우리집", "땡땡땡로 123", "123동 456호", "12345");
        Product product = createProductDto().toEntity();
        List<Trade> list = new ArrayList<>();

        Trade sale1 = Trade.builder()
            .publisher(anotherUser)
            .seller(anotherUser)
            .buyer(null)
            .product(product)
            .status(TradeStatus.BID)
            .price(400000L)
            .productSize(260.0)
            .returnAddress(address)
            .shippingAddress(null)
            .build();
        list.add(sale1);

        Trade purchase1 = Trade.builder()
            .publisher(anotherUser)
            .seller(null)
            .buyer(anotherUser)
            .product(product)
            .status(TradeStatus.BID)
            .price(180000L)
            .productSize(260.0)
            .returnAddress(null)
            .shippingAddress(address)
            .build();
        list.add(purchase1);


        Trade sale2 = Trade.builder()
            .publisher(user)
            .seller(user)
            .buyer(null)
            .product(product)
            .status(TradeStatus.BID)
            .price(300000L)
            .productSize(260.0)
            .returnAddress(address)
            .shippingAddress(null)
            .build();
        list.add(sale2);

        Trade purchase2 = Trade.builder()
            .publisher(user)
            .seller(null)
            .buyer(user)
            .product(product)
            .status(TradeStatus.BID)
            .price(200000L)
            .productSize(260.0)
            .returnAddress(null)
            .shippingAddress(address)
            .build();
        list.add(purchase2);
        return list;
    }

    private BrandInfo createBrandInfo() {
        return BrandInfo.builder()
            .nameKor("나이키")
            .nameEng("Nike")
            .originImagePath(
                "https://shoeauction-brands-origin.s3.ap-northeast-2.amazonaws.com/brand.png")
            .thumbnailImagePath(
                "https://shoeauction-brands-thumbnail.s3.ap-northeast-2.amazonaws.com/brand.png")
            .build();
    }

    private Product createProduct() {
        return Product.builder()
            .id(1L)
            .nameKor("덩크 로우")
            .nameEng("Dunk Low")
            .modelNumber("DD1391-100")
            .color("WHITE/BLACK")
            .releaseDate(LocalDate.of(2021, 01, 04))
            .releasePrice(119000)
            .currency(Currency.KRW)
            .sizeClassification(SizeClassification.MENS)
            .sizeUnit(SizeUnit.MM)
            .minSize(240)
            .maxSize(320)
            .sizeGap(5)
            .brand(createBrandInfo().toEntity())
            .resizedImagePath(
                "https://shoeauction-brands-resized.s3.ap-northeast-2.amazonaws.com/brand.png")
            .trades(createTrades())
            .build();
    }

//    private ProductInfoByTrade createProductInfoByTrade() {
//        TradeBidResponse immediatePurchasePrice = createTradeBidResponse(300000L);
//        TradeBidResponse immediateSalePrice = createTradeBidResponse(200000L);
//        BrandInfo brandInfo = createBrandInfo();

//        return ProductInfoByTrade.builder()
//            .id(2L)
//            .nameKor("나이키 덩크")
//            .nameEng("nike dunk")
//            .color("red")
//            .brand(brandInfo)
//            .modelNumber("DW1105")
//            .immediatePurchasePrice(immediatePurchasePrice)
//            .immediateSalePrice(immediateSalePrice)
//            .build();
//
//    }

//    private TradeBidResponse createTradeBidResponse(Long productPrice) {
//        return TradeBidResponse.builder()
//            .id(3L)
//            .productId(2L)
//            .productSize(260.0)
//            .price(productPrice)
//            .build();
//
//
//    }


//    private TradeUserInfo createTradeUserInfo() {
//        Account account = new Account("카카오뱅크", "123456789", "루루삐");
//        return TradeUserInfo.builder()
//            .account(account)
//            .addressBook(new AddressBook())
//            .build();
//    }
//}

    @DisplayName("상품 거래 화면에 보여질 리소스들을 리턴한다.")
    @Test
    public void getResourceForTrade() {
        String email = "anotherUser@test.com";
        Long productId = 1L;
        double size = 260.0;
        User user = createUser();
        Product product = createProduct();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        TradeResource resourceForTrade = tradeService.getResourceForBid(email, productId, size);

        assertThat(resourceForTrade.getProductInfoByTrade().getImmediatePurchasePrice().getPrice()).isEqualTo(400000L);
        assertThat(resourceForTrade.getProductInfoByTrade().getImmediateSalePrice().getPrice()).isEqualTo(180000L);
        assertThat(resourceForTrade.getProductInfoByTrade().getColor())
            .isEqualTo(product.getColor());
    }

//    @DisplayName("판매 입찰을 생성한다.")
//    @Test
//    public void createSalesBid() {
//        TradeDto.SaveRequest requestDto = TradeDto.SaveRequest.builder()
//            .price(300000L)
//            .productId(3L)
//            .addressId(4L)
//            .productSize(260.0)
//            .build();
//        String email = "test123@test.com";
//        User user = createUser();
//        Product product = createProduct();
//
//        when(productRepository.findById(requestDto.getProductId()))
//            .thenReturn(Optional.of(product));
//        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
////        Address address = new Address(1L, "우리집", "땡땡땡로 123", "123동 456호", "12345");
//
//        tradeService.createSalesBid(email, requestDto);
//    }
}