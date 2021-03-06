package com.flab.shoeauction.service;

import static com.flab.shoeauction.domain.trade.TradeStatus.CANCEL;
import static com.flab.shoeauction.domain.trade.TradeStatus.PRE_CONCLUSION;
import static com.flab.shoeauction.domain.trade.TradeStatus.PRE_INSPECTION;
import static com.flab.shoeauction.domain.trade.TradeStatus.PRE_SHIPMENT;
import static com.flab.shoeauction.domain.trade.TradeStatus.PRE_WAREHOUSING;
import static com.flab.shoeauction.domain.trade.TradeStatus.SHIPPING;
import static com.flab.shoeauction.domain.trade.TradeStatus.TRADE_COMPLETE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.flab.shoeauction.controller.dto.BrandDto.BrandInfo;
import com.flab.shoeauction.controller.dto.ProductDto;
import com.flab.shoeauction.controller.dto.TradeDto;
import com.flab.shoeauction.controller.dto.TradeDto.ChangeRequest;
import com.flab.shoeauction.controller.dto.TradeDto.ImmediateTradeRequest;
import com.flab.shoeauction.controller.dto.TradeDto.TradeInfoResponse;
import com.flab.shoeauction.controller.dto.TradeDto.TradeResource;
import com.flab.shoeauction.controller.dto.TradeDto.TradeSearchCondition;
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
import com.flab.shoeauction.exception.trade.LowPointException;
import com.flab.shoeauction.exception.user.NotAuthorizedException;
import com.flab.shoeauction.service.message.MessageService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    @Mock
    private MessageService fcmService;

    @Mock
    private PointService pointService;

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
            .point(0L)
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
            .point(0L)
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

        Trade sale = Trade.builder()
            .id(2L)
            .publisher(user)
            .seller(user)
            .buyer(anotherUser)
            .product(product)
            .status(TRADE_COMPLETE)
            .price(300000L)
            .productSize(260.0)
            .returnAddress(address)
            .shippingAddress(null)
            .build();
        list.add(sale);

        Trade purchase = Trade.builder()
            .id(3L)
            .publisher(user)
            .seller(anotherUser)
            .buyer(user)
            .product(product)
            .status(TRADE_COMPLETE)
            .price(200000L)
            .productSize(260.0)
            .returnAddress(null)
            .shippingAddress(address)
            .build();
        list.add(purchase);
        return list;
    }

    private List<Trade> createTradesInBidStatus() {

        User user = createUser();
        User anotherUser = createAnotherUser();
        Address address = new Address(1L, "우리집", "땡땡땡로 123", "123동 456호", "12345");
        Product product = createProductDto().toEntity();
        List<Trade> list = new ArrayList<>();

        Trade sale = Trade.builder()
            .id(2L)
            .publisher(user)
            .seller(user)
            .buyer(null)
            .product(product)
            .status(TradeStatus.PRE_CONCLUSION)
            .price(300000L)
            .productSize(260.0)
            .returnAddress(address)
            .shippingAddress(null)
            .build();
        list.add(sale);

        Trade purchase = Trade.builder()
            .id(3L)
            .publisher(user)
            .seller(null)
            .buyer(user)
            .product(product)
            .status(TradeStatus.PRE_CONCLUSION)
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
            .trades(createTradesInBidStatus())
            .build();
    }

    private Trade createConcludedBuyersTrade() {
        User buyer = createUser();
        User seller = createAnotherUser();
        Product product = createProduct();
        Address address = new Address(1L, "우리집", "땡땡땡로 123", "123동 456호", "12345");
        return Trade.builder()
            .id(11L)
            .publisher(buyer)
            .seller(seller)
            .buyer(buyer)
            .product(product)
            .status(TradeStatus.PRE_CONCLUSION)
            .price(300000L)
            .productSize(260.0)
            .returnAddress(address)
            .shippingAddress(null)
            .build();
    }

    private List<TradeInfoResponse> createTradeInfoList() {

        List<TradeInfoResponse> list = new ArrayList<>();
        Long id = 1L;
        String email = id + "test.com";
        for (int i = 0; i < 5; i++) {

            TradeInfoResponse tradeInfoResponse = TradeInfoResponse.builder()
                .id(id++)
                .status(PRE_CONCLUSION)
                .build();

            list.add(tradeInfoResponse);

            TradeInfoResponse tradeInfoResponse2 = TradeInfoResponse.builder()
                .id(id++)
                .status(PRE_INSPECTION)
                .build();

            list.add(tradeInfoResponse2);

            TradeInfoResponse tradeInfoResponse3 = TradeInfoResponse.builder()
                .id(id++)
                .status(CANCEL)
                .build();

            list.add(tradeInfoResponse3);
        }
        return list;
    }

    private List<TradeInfoResponse> createTradeInfoSearchBySellerEmail(
        TradeSearchCondition tradeSearchCondition) {
        List<Trade> trades = createTrades();
        List<TradeInfoResponse> listSearchedBySellerEmail = new ArrayList<>();

        List<Long> tradeIds = trades.stream()
            .filter(s -> s.getSeller().getEmail().equals(tradeSearchCondition.getSellersEmail()))
            .map(Trade::getId)
            .collect(Collectors.toList());

        List<TradeStatus> tradeStatuses = trades.stream()
            .filter(s -> s.getSeller().getEmail().equals(tradeSearchCondition.getSellersEmail()))
            .map(Trade::getStatus)
            .collect(Collectors.toList());

        int length = tradeIds.size();

        for (int count = 0; count < length; count++) {
            listSearchedBySellerEmail.add(TradeInfoResponse.builder()
                .id(tradeIds.get(count))
                .status(tradeStatuses.get(count))
                .build());
        }
        return listSearchedBySellerEmail;
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

    @DisplayName("구매 입찰 생성에 성공한다.")
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
        user.chargingPoint(1000000L);
        Product product = createProduct();
        Address address = new Address(4L, "우리집", "땡땡땡로 123", "123동 456호", "12345");
        user.getAddressBook().addAddress(address);

        when(productRepository.findById(requestDto.getProductId()))
            .thenReturn(Optional.of(product));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        tradeService.createPurchaseBid(email, requestDto);

        assertThat(user.getPoint()).isEqualTo(1000000L - requestDto.getPrice());
    }

    @DisplayName("포인트 부족으로 구매 입찰 생성에 실패한다.")
    @Test
    public void createPurchasesBid_lowPoint() {
        TradeDto.SaveRequest requestDto = TradeDto.SaveRequest.builder()
            .price(180000L)
            .productId(3L)
            .addressId(4L)
            .productSize(260.0)
            .build();
        String email = "test123@test.com";
        User user = createUser();
        Address address = new Address(4L, "우리집", "땡땡땡로 123", "123동 456호", "12345");
        user.getAddressBook().addAddress(address);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(LowPointException.class,
            () -> tradeService.createPurchaseBid(email, requestDto));
    }


    @DisplayName("물품 즉시 구매에 성공한다.")
    @Test
    public void immediatePurchase() {
        Address address = new Address(4L, "우리집", "땡땡땡로 123", "123동 456호", "12345");
        String email = "test123@test.com";
        User user = createUser();
        user.chargingPoint(1000000L);
        User anotherUser = createAnotherUser();
        Product product = createProduct();

        Trade saleTrade = Trade.builder()
            .publisher(anotherUser)
            .seller(anotherUser)
            .buyer(null)
            .product(product)
            .status(TradeStatus.PRE_CONCLUSION)
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
        when(tradeRepository.findById(requestDto.getTradeId())).thenReturn(Optional.of(saleTrade));
        when(addressRepository.findById(requestDto.getAddressId()))
            .thenReturn(Optional.of(address));
        tradeService.immediatePurchase(email, requestDto);

        assertThat(saleTrade.getStatus()).isEqualTo(TradeStatus.PRE_SELLER_SHIPMENT);
        assertThat(saleTrade.getBuyer().getId()).isEqualTo(user.getId());
        assertThat(saleTrade.getShippingAddress().getId()).isEqualTo(address.getId());
        assertThat(user.getPoint()).isEqualTo(1000000L - saleTrade.getPrice());
    }

    @DisplayName("포인트 부족으로 물품 즉시 구매에 실패한다.")
    @Test
    public void immediatePurchase_lowPoint() {
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
            .status(TradeStatus.PRE_CONCLUSION)
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
        when(tradeRepository.findById(requestDto.getTradeId())).thenReturn(Optional.of(saleTrade));

        assertThrows(LowPointException.class,
            () -> tradeService.immediatePurchase(email, requestDto));
    }

    @DisplayName("물품을 즉시 판매한다.")
    @Test
    public void immediateSales() {
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
            .status(TradeStatus.PRE_CONCLUSION)
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
        tradeService.immediateSale(email, requestDto);

        assertThat(purchaseTrade.getStatus()).isEqualTo(TradeStatus.PRE_SELLER_SHIPMENT);
        assertThat(purchaseTrade.getSeller().getId()).isEqualTo(user.getId());
        assertThat(purchaseTrade.getShippingAddress().getId()).isEqualTo(address.getId());
    }

    @DisplayName("입찰 내역을 삭제한다")
    @Test
    public void deleteTrade() {
        Address address = new Address(4L, "우리집", "땡땡땡로 123", "123동 456호", "12345");
        User anotherUser = createAnotherUser();
        Product product = createProduct();
        Long nowPoint = 10000L;
        anotherUser.chargingPoint(nowPoint);

        Trade purchaseTrade = Trade.builder()
            .id(11L)
            .publisher(anotherUser)
            .seller(null)
            .buyer(anotherUser)
            .product(product)
            .status(TradeStatus.PRE_CONCLUSION)
            .price(200000L)
            .productSize(260.0)
            .returnAddress(null)
            .shippingAddress(address)
            .build();

        ChangeRequest changeRequest = ChangeRequest.builder()
            .tradeId(11L)
            .build();

        when(tradeRepository.findById(changeRequest.getTradeId())).thenReturn(
            Optional.ofNullable(purchaseTrade));

        tradeService.deleteTrade(changeRequest);

        assertThat(anotherUser.getPoint()).isEqualTo(nowPoint + purchaseTrade.getPrice());
        verify(tradeRepository).deleteById(any());
    }

    @DisplayName("판매자가 상품 발송 후 입고 운송장 번호를 입력한다.")
    @Test
    public void updateReceivingTrackingNumber() {
        Trade trade = createConcludedBuyersTrade();
        Long tradeId = trade.getId();
        String email = trade.getSeller().getEmail();
        String trackingNumber = "123456789";
        given(tradeRepository.findById(tradeId)).willReturn(Optional.of(trade));

        tradeService.updateReceivingTrackingNumber(tradeId, email, trackingNumber);

        assertThat(trade.getReceivingTrackingNumber()).isEqualTo(trackingNumber);
        assertThat(trade.getStatus()).isEqualTo(PRE_WAREHOUSING);
    }

    @DisplayName("판매자가 아닌 유저가 입고 운송장 번호 입력을 시도 시 실패한다.")
    @Test
    public void failToUpdateReceivingTrackingNumberIfUserIsNotSeller() {
        Trade trade = createConcludedBuyersTrade();
        Long tradeId = trade.getId();
        String email = "wrong@email.com";
        String trackingNumber = "123456789";
        given(tradeRepository.findById(tradeId)).willReturn(Optional.of(trade));

        assertThrows(NotAuthorizedException.class,
            () -> tradeService.updateReceivingTrackingNumber(tradeId, email, trackingNumber));
        assertThat(trade.getReceivingTrackingNumber()).isNull();
        assertThat(trade.getStatus()).isEqualTo(PRE_CONCLUSION);
    }

    @DisplayName("관리자가 상품의 입고를 확인하고 상품의 상태를 검수 대기로 변경한다.")
    @Test
    public void confirmWarehousing() {
        Trade trade = createConcludedBuyersTrade();
        Long tradeId = trade.getId();
        given(tradeRepository.findById(tradeId)).willReturn(Optional.of(trade));

        tradeService.confirmWarehousing(tradeId);

        assertThat(trade.getStatus()).isEqualTo(PRE_INSPECTION);
    }

    @DisplayName("특정 상품의 검수 결과를 적합으로 처리한다.")
    @Test
    public void inspectionSuccessful() {
        Trade trade = createConcludedBuyersTrade();
        Long tradeId = trade.getId();
        given(tradeRepository.findById(tradeId)).willReturn(Optional.of(trade));

        tradeService.inspectionSuccessful(tradeId);

        assertThat(trade.getStatus()).isEqualTo(PRE_SHIPMENT);
    }

    @DisplayName("특정 상품의 검수 결과를 부적합으로 처리한다.")
    @Test
    public void inspectionFailed() {
        Trade trade = createConcludedBuyersTrade();
        Long tradeId = trade.getId();
        User buyer = trade.getBuyer();
        Long buyersPoint = buyer.getPoint();
        String reason = "가죽 스크래치 및 박음질 불량";
        given(tradeRepository.findById(tradeId)).willReturn(Optional.of(trade));

        tradeService.inspectionFailed(tradeId, reason);

        assertThat(trade.getStatus()).isEqualTo(CANCEL);
        assertThat(trade.getCancelReason()).isEqualTo(reason);
        assertThat(buyer.getPoint()).isEqualTo(buyersPoint + trade.getPrice());
    }

    @DisplayName("부적합 처리된 상품을 반송하고 반송 운송장 번호를 입력한다.")
    @Test
    public void updateReturnTrackingNumber() {
        Trade trade = createConcludedBuyersTrade();
        Long tradeId = trade.getId();
        String trackingNumber = "987654321";
        given(tradeRepository.findById(tradeId)).willReturn(Optional.of(trade));

        tradeService.updateReturnTrackingNumber(tradeId, trackingNumber);

        assertThat(trade.getReturnTrackingNumber()).isEqualTo(trackingNumber);
    }

    @DisplayName("관리자가 모든 Trade를 조회한다.")
    @Test
    public void getTradeInfos() {
        List<TradeInfoResponse> tradeInfoList = createTradeInfoList();

        TradeSearchCondition tradeSearchCondition = TradeSearchCondition
            .builder()
            .tradeId(null)
            .buyersEmail(null)
            .sellersEmail(null)
            .build();

        Pageable pageable = PageRequest.of(0, 10);
        long total = tradeInfoList.size();

        Page<TradeInfoResponse> result = new PageImpl<>(tradeInfoList, pageable, total);
        given(tradeRepository.searchByTradeStatusAndTradeId(tradeSearchCondition, pageable))
            .willReturn(result);

        Page<TradeInfoResponse> tradeInfos = tradeService
            .getTradeInfos(tradeSearchCondition, pageable);

        assertThat(tradeInfos.getContent().size()).isEqualTo(tradeInfoList.size());
    }


    @DisplayName("관리자가 seller의 email로 Trade를 조회한다.")
    @Test
    public void getTradeInfosSearchBySellersEmail() {
        TradeSearchCondition tradeSearchCondition = TradeSearchCondition
            .builder()
            .tradeId(null)
            .buyersEmail(null)
            .sellersEmail("test123@test.com")
            .build();

        List<TradeInfoResponse> tradeInfos = createTradeInfoSearchBySellerEmail(
            tradeSearchCondition);
        long total = tradeInfos.size();
        Pageable pageable = PageRequest.of(0, 10);

        Page<TradeInfoResponse> searchResults = new PageImpl<>(tradeInfos, pageable, total);

        given(tradeRepository.searchByTradeStatusAndTradeId(tradeSearchCondition, pageable))
            .willReturn(searchResults);

        Page<TradeInfoResponse> executionResults = tradeService
            .getTradeInfos(tradeSearchCondition, pageable);

        assertThat(executionResults.getTotalElements()).isEqualTo(searchResults.getTotalElements());
        assertThat(executionResults.getContent().get(0).getStatus()).isEqualTo(searchResults.getContent().get(0).getStatus());
        assertThat(executionResults.getContent().get(0).getId()).isEqualTo(searchResults.getContent().get(0).getId());
    }

    @DisplayName("관리자가 상품 출고 후 운송장 번호를 입력한다.")
    @Test
    public void updateForwardingTrackingNumber() {
        Trade trade = createConcludedBuyersTrade();
        Long tradeId = trade.getId();
        String trackingNumber = "192837465";
        given(tradeRepository.findById(tradeId)).willReturn(Optional.of(trade));

        tradeService.updateForwardingTrackingNumber(tradeId, trackingNumber);

        assertThat(trade.getForwardingTrackingNumber()).isEqualTo(trackingNumber);
        assertThat(trade.getStatus()).isEqualTo(SHIPPING);
    }

    @DisplayName("구매자의 구매 확정 요청이 성공한다.")
    @Test
    public void confirmPurchase() {
        Trade trade = createConcludedBuyersTrade();
        Long tradeId = trade.getId();
        Long tradePrice = trade.getPrice();
        String email = trade.getBuyer().getEmail();
        Long preSellerPoint = trade.getSeller().getPoint();
        given(tradeRepository.findById(tradeId)).willReturn(Optional.of(trade));

        tradeService.confirmPurchase(tradeId, email);

        assertThat(trade.getStatus()).isEqualTo(TRADE_COMPLETE);
        assertThat(trade.getSeller().getPoint()).isEqualTo(preSellerPoint + tradePrice);
    }
}