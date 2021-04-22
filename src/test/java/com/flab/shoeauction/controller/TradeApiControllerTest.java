package com.flab.shoeauction.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.shoeauction.controller.dto.BrandDto.BrandInfo;
import com.flab.shoeauction.controller.dto.ProductDto;
import com.flab.shoeauction.controller.dto.ProductDto.ProductInfoByTrade;
import com.flab.shoeauction.controller.dto.TradeDto;
import com.flab.shoeauction.controller.dto.TradeDto.ChangeRequest;
import com.flab.shoeauction.controller.dto.TradeDto.ImmediateTradeRequest;
import com.flab.shoeauction.controller.dto.TradeDto.TradeBidResponse;
import com.flab.shoeauction.controller.dto.TradeDto.TradeResource;
import com.flab.shoeauction.controller.dto.UserDto.TradeUserInfo;
import com.flab.shoeauction.domain.addressBook.Address;
import com.flab.shoeauction.domain.addressBook.AddressBook;
import com.flab.shoeauction.domain.product.Product;
import com.flab.shoeauction.domain.product.common.Currency;
import com.flab.shoeauction.domain.product.common.SizeClassification;
import com.flab.shoeauction.domain.product.common.SizeUnit;
import com.flab.shoeauction.domain.trade.Trade;
import com.flab.shoeauction.domain.trade.TradeStatus;
import com.flab.shoeauction.domain.users.common.Account;
import com.flab.shoeauction.domain.users.common.UserLevel;
import com.flab.shoeauction.domain.users.common.UserStatus;
import com.flab.shoeauction.domain.users.user.User;
import com.flab.shoeauction.service.SessionLoginService;
import com.flab.shoeauction.service.TradeService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(TradeApiController.class)
@ActiveProfiles("test")
@MockBean(JpaMetamodelMappingContext.class)
class TradeApiControllerTest {

    @MockBean
    private TradeService tradeService;

    @MockBean
    private SessionLoginService sessionLoginService;

    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    public void setup(WebApplicationContext webApplicationContext,
        RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .apply(sharedHttpSession())
            .build();
    }

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
            .status(TradeStatus.PRE_CONCLUSION)
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
            .trades(createTrades())
            .build();
    }

    private Trade createTrade() {
        User user = createUser();
        Product product = createProduct();
        Address address = new Address(2L, "우리집", "땡땡땡로 123", "123동 456호", "12345");
        return Trade.builder()
            .id(11L)
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
    }

    private TradeResource createTradeResource() {
        TradeUserInfo tradeUserInfo = TradeUserInfo
            .builder()
            .account(new Account("카카오뱅크", "123456789", "루루삐"))
            .addressBook(new AddressBook())
            .build();

        TradeBidResponse immediatePurchasePrice = TradeBidResponse
            .builder()
            .tradeId(11L)
            .productId(1L)
            .productSize(260.0)
            .price(300000L)
            .build();

        TradeBidResponse immediateSalePrice = TradeBidResponse
            .builder()
            .tradeId(11L)
            .productId(1L)
            .productSize(260.0)
            .price(200000L)
            .build();

        ProductInfoByTrade productInfoByTrade = ProductInfoByTrade
            .builder()
            .id(1L)
            .nameKor("덩크 로우")
            .nameEng("Dunk Low")
            .modelNumber("DD1391-100")
            .color("WHITE/BLACK")
            .brand(createBrandInfo())
            .immediatePurchasePrice(immediatePurchasePrice)
            .immediateSalePrice(immediateSalePrice)
            .build();

        return TradeResource
            .builder()
            .productInfoByTrade(productInfoByTrade)
            .tradeUserInfo(tradeUserInfo)
            .build();
    }


    @DisplayName("판매 또는 구매 입찰시 필요한 리소스 리턴")
    @Test
    public void obtainResourceForBid() throws Exception {

        String email = "test123@test.com";
        Long productId = 1L;
        double size = 260.0;

        TradeResource tradeResource = createTradeResource();

        given(tradeService.getResourceForBid(email, productId, size)).willReturn(tradeResource);

        mockMvc.perform(get("/trades/{productId}", productId)
            .accept(MediaType.APPLICATION_JSON)
            .param("size", "260.0"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("trade/getResource",
                pathParameters(
                    parameterWithName("productId").description("물품 ID[PK]")
                ),
                requestParameters(
                    parameterWithName("size").description("신발 사이즈")
                )
            ));
    }

    @DisplayName("판매 입찰을 등록한다.")
    @Test
    public void saleBid() throws Exception {
        String email = "test123@test.com";
        TradeDto.SaveRequest requestDto = TradeDto.SaveRequest.builder()
            .price(200000L)
            .productSize(260.0)
            .productId(1L)
            .addressId(4L)
            .build();

        doNothing().when(tradeService).createSalesBid(email, requestDto);

        mockMvc.perform(post("/trades/sell/bid")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated())
            .andDo(print())
            .andDo(document("trade/sell/bid", requestFields(
                fieldWithPath("price").description(JsonFieldType.NUMBER).description("입찰 가격"),
                fieldWithPath("productSize").description(JsonFieldType.NUMBER)
                    .description("신발 사이즈"),
                fieldWithPath("productId").description(JsonFieldType.NUMBER).description("물품 ID"),
                fieldWithPath("addressId").description(JsonFieldType.NUMBER).description("반송 주소")
            )));
    }

    @DisplayName("구매 입찰을 등록한다.")
    @Test
    public void purchaseBid() throws Exception {
        String email = "test123@test.com";
        TradeDto.SaveRequest requestDto = TradeDto.SaveRequest.builder()
            .price(180000L)
            .productSize(260.0)
            .productId(1L)
            .addressId(4L)
            .build();

        doNothing().when(tradeService).createPurchaseBid(email, requestDto);

        mockMvc.perform(post("/trades/buy/bid")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated())
            .andDo(print())
            .andDo(document("trade/buy/bid", requestFields(
                fieldWithPath("price").description(JsonFieldType.NUMBER).description("입찰 가격"),
                fieldWithPath("productSize").description(JsonFieldType.NUMBER)
                    .description("신발 사이즈"),
                fieldWithPath("productId").description(JsonFieldType.NUMBER).description("물품 ID"),
                fieldWithPath("addressId").description(JsonFieldType.NUMBER).description("반송 주소")
            )));
    }

    @DisplayName("물품을 즉시 판매한다.")
    @Test
    public void immediateSales() throws Exception {
        String email = "test123@test.com";
        ImmediateTradeRequest requestDto = ImmediateTradeRequest.builder()
            .tradeId(11L)
            .addressId(2L)
            .productId(1L)
            .build();

        doNothing().when(tradeService).immediateSale(email, requestDto);

        mockMvc.perform(post("/trades/sell")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("trade/sell/immediately",
                requestFields(
                    fieldWithPath("tradeId").description(JsonFieldType.NUMBER)
                        .description("Trade ID"),
                    fieldWithPath("addressId").description(JsonFieldType.NUMBER)
                        .description("반송 주소 ID"),
                    fieldWithPath("productId").description(JsonFieldType.NUMBER)
                        .description("Product ID")
                )
            ));
    }


    @DisplayName("물품을 즉시 구매한다.")
    @Test
    public void immediatePurchase() throws Exception {
        String email = "test123@test.com";
        ImmediateTradeRequest requestDto = ImmediateTradeRequest.builder()
            .tradeId(11L)
            .addressId(2L)
            .productId(1L)
            .build();

        doNothing().when(tradeService).immediatePurchase(email, requestDto);

        mockMvc.perform(post("/trades/buy")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("trade/buy/immediately",
                requestFields(
                    fieldWithPath("tradeId").description(JsonFieldType.NUMBER)
                        .description("Trade ID"),
                    fieldWithPath("addressId").description(JsonFieldType.NUMBER)
                        .description("배송지 ID"),
                    fieldWithPath("productId").description(JsonFieldType.NUMBER)
                        .description("Product ID")
                )
            ));
    }

    @DisplayName("등록한 입찰 내역을 삭제한다.")
    @Test
    public void deleteTrade() throws Exception {
        ChangeRequest requestDto = ChangeRequest.builder()
            .tradeId(11L)
            .build();

        doNothing().when(tradeService).deleteTrade(requestDto);

        mockMvc.perform(delete("/trades")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("trade/delete", requestFields(
                fieldWithPath("tradeId").type(JsonFieldType.NUMBER).description("Trade ID"),
                fieldWithPath("price").ignored()
            )));
    }
}