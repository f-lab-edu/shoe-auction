package com.flab.shoeauction.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.flab.shoeauction.controller.dto.BrandDto.BrandInfo;
import com.flab.shoeauction.controller.dto.ProductDto;
import com.flab.shoeauction.controller.dto.TradeDto;
import com.flab.shoeauction.controller.dto.TradeDto.ChangeRequest;
import com.flab.shoeauction.controller.dto.TradeDto.ImmediateTradeRequest;
import com.flab.shoeauction.controller.dto.TradeDto.TradeResource;
import com.flab.shoeauction.domain.addressBook.Address;
import com.flab.shoeauction.domain.addressBook.AddressBook;
import com.flab.shoeauction.domain.addressBook.AddressRepository;
import com.flab.shoeauction.domain.product.Product;
import com.flab.shoeauction.domain.product.ProductRepository;
import com.flab.shoeauction.domain.product.common.Currency;
import com.flab.shoeauction.domain.product.common.SizeClassification;
import com.flab.shoeauction.domain.product.common.SizeUnit;
import com.flab.shoeauction.domain.trade.Trade;
import com.flab.shoeauction.domain.trade.TradeRepository;
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

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private AddressRepository addressRepository;

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
            .addressBook(new AddressBook())
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
            .addressBook(new AddressBook())
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
        Address address = new Address(1L, "우리집", "땡땡땡로 123", "123동 456호", "12345");
        Product product = createProductDto().toEntity();
        List<Trade> list = new ArrayList<>();

        Trade sale = Trade.builder()
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
        list.add(sale);

        Trade purchase = Trade.builder()
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
        list.add(purchase);
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

    private Trade createTrade() {
        User user = createUser();
        Product product = createProduct();
        Address address = new Address(1L, "우리집", "땡땡땡로 123", "123동 456호", "12345");
        return Trade.builder()
            .id(11L)
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
    }


    @DisplayName("상품 거래 화면에 보여질 리소스들을 리턴한다.")
    @Test
    public void getResourceForTrade() {
        String email = "anotherUser@test.com";
        Long productId = 1L;
        double size = 260.0;
        User user = createAnotherUser();
        Product product = createProduct();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        TradeResource resourceForTrade = tradeService.getResourceForBid(email, productId, size);

        assertThat(resourceForTrade.getProductInfoByTrade().getImmediatePurchasePrice().getPrice())
            .isEqualTo(300000L);
        assertThat(resourceForTrade.getProductInfoByTrade().getImmediateSalePrice().getPrice())
            .isEqualTo(200000L);
        assertThat(resourceForTrade.getProductInfoByTrade().getColor())
            .isEqualTo(product.getColor());
    }

    @DisplayName("상품 거래 화면에 보여질 리소스들을 리턴한다. - 자기 자신의 입찰 내역은 리턴되는 리소스에서 제외된다.")
    @Test
    public void getResourceForTrade_canNotSee() {
        String email = "test123@test.com";
        Long productId = 1L;
        double size = 260.0;
        User user = createUser();
        Product product = createProduct();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        TradeResource resourceForTrade = tradeService.getResourceForBid(email, productId, size);

        assertThat(resourceForTrade.getProductInfoByTrade().getImmediatePurchasePrice()).isNull();
        assertThat(resourceForTrade.getProductInfoByTrade().getImmediateSalePrice()).isNull();
        assertThat(resourceForTrade.getProductInfoByTrade().getColor())
            .isEqualTo(product.getColor());
    }

    @DisplayName("판매 입찰을 생성한다.")
    @Test
    public void createSalesBid() {
        TradeDto.SaveRequest requestDto = TradeDto.SaveRequest.builder()
            .price(300000L)
            .productId(3L)
            .addressId(4L)
            .productSize(260.0)
            .build();
        String email = "test123@test.com";
        User user = createUser();
        Product product = createProduct();
        Address address = new Address(4L, "우리집", "땡땡땡로 123", "123동 456호", "12345");
        user.getAddressBook().addAddress(address);

        when(productRepository.findById(requestDto.getProductId()))
            .thenReturn(Optional.of(product));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        tradeService.createSalesBid(email, requestDto);
    }

    @DisplayName("구매 입찰을 생성한다.")
    @Test
    public void createPurchasesBid() {
        TradeDto.SaveRequest requestDto = TradeDto.SaveRequest.builder()
            .price(180000L)
            .productId(3L)
            .addressId(4L)
            .productSize(260.0)
            .build();
        String email = "test123@test.com";
        User user = createUser();
        Product product = createProduct();
        Address address = new Address(4L, "우리집", "땡땡땡로 123", "123동 456호", "12345");
        user.getAddressBook().addAddress(address);

        when(productRepository.findById(requestDto.getProductId()))
            .thenReturn(Optional.of(product));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        tradeService.createPurchaseBid(email, requestDto);
    }

    @DisplayName("물품을 즉시 구매한다.")
    @Test
    public void immediateSales() {
        Address address = new Address(4L, "우리집", "땡땡땡로 123", "123동 456호", "12345");
        String email = "test123@test.com";
        User user = createUser();
        User anotherUser = createAnotherUser();
        Product product = createProduct();

        Trade saleTrade = Trade.builder()
            .publisher(anotherUser)
            .seller(anotherUser)
            .buyer(null)
            .product(product)
            .status(TradeStatus.BID)
            .price(300000L)
            .productSize(260.0)
            .returnAddress(address)
            .shippingAddress(null)
            .build();

        ImmediateTradeRequest requestDto = ImmediateTradeRequest.builder()
            .tradeId(5L)
            .addressId(4L)
            .productId(1L)
            .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(addressRepository.findById(requestDto.getAddressId()))
            .thenReturn(Optional.of(address));
        when(tradeRepository.findById(requestDto.getTradeId())).thenReturn(Optional.of(saleTrade));
        tradeService.immediatePurchase(email, requestDto);

        assertThat(saleTrade.getStatus()).isEqualTo(TradeStatus.PROGRESS);
        assertThat(saleTrade.getBuyer().getId()).isEqualTo(user.getId());
        assertThat(saleTrade.getShippingAddress().getId()).isEqualTo(address.getId());
    }

    @DisplayName("물품을 즉시 판매한다..")
    @Test
    public void immediatePurchase() {
        Address address = new Address(4L, "우리집", "땡땡땡로 123", "123동 456호", "12345");
        String email = "test123@test.com";
        User user = createUser();
        User anotherUser = createAnotherUser();
        Product product = createProduct();

        Trade purchaseTrade = Trade.builder()
            .publisher(anotherUser)
            .seller(null)
            .buyer(anotherUser)
            .product(product)
            .status(TradeStatus.BID)
            .price(200000L)
            .productSize(260.0)
            .returnAddress(null)
            .shippingAddress(address)
            .build();

        ImmediateTradeRequest requestDto = ImmediateTradeRequest.builder()
            .tradeId(5L)
            .addressId(4L)
            .productId(1L)
            .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(addressRepository.findById(requestDto.getAddressId()))
            .thenReturn(Optional.of(address));
        when(tradeRepository.findById(requestDto.getTradeId()))
            .thenReturn(Optional.of(purchaseTrade));
        tradeService.immediatePurchase(email, requestDto);

        assertThat(purchaseTrade.getStatus()).isEqualTo(TradeStatus.PROGRESS);
        assertThat(purchaseTrade.getBuyer().getId()).isEqualTo(user.getId());
        assertThat(purchaseTrade.getShippingAddress().getId()).isEqualTo(address.getId());
    }

    @DisplayName("입찰 가격을 수정한다.")
    @Test
    public void updateTrade() {
        Trade trade = createTrade();
        ChangeRequest changeRequest = ChangeRequest.builder()
            .tradeId(11L)
            .price(700000L)
            .build();

        when(tradeRepository.findById(changeRequest.getTradeId())).thenReturn(Optional.of(trade));
        tradeService.updateTrade(changeRequest);

        assertThat(trade.getPrice()).isEqualTo(changeRequest.getPrice());
    }

    @DisplayName("입찰 내역을 삭제한다")
    @Test
    public void deleteTrade() {
        ChangeRequest changeRequest = ChangeRequest.builder()
            .tradeId(11L)
            .build();
        tradeService.deleteTrade(changeRequest);

        verify(tradeRepository).deleteById(any());
    }
}