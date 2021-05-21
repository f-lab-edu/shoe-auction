package com.flab.shoeauction.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.shoeauction.controller.dto.BrandDto.BrandInfo;
import com.flab.shoeauction.controller.dto.ProductDto.ProductInfoResponse;
import com.flab.shoeauction.controller.dto.ProductDto.SaveRequest;
import com.flab.shoeauction.controller.dto.ProductDto.SearchCondition;
import com.flab.shoeauction.controller.dto.ProductDto.ThumbnailResponse;
import com.flab.shoeauction.controller.dto.TradeDto.TradeBidResponse;
import com.flab.shoeauction.controller.dto.TradeDto.TradeCompleteInfo;
import com.flab.shoeauction.domain.product.common.Currency;
import com.flab.shoeauction.domain.product.common.OrderStandard;
import com.flab.shoeauction.domain.product.common.SizeClassification;
import com.flab.shoeauction.domain.product.common.SizeUnit;
import com.flab.shoeauction.service.BrandService;
import com.flab.shoeauction.service.ProductService;
import com.flab.shoeauction.service.SessionLoginService;
import java.nio.charset.StandardCharsets;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(ProductApiController.class)
@ActiveProfiles("test")
@MockBean(JpaMetamodelMappingContext.class)
class ProductApiControllerTest {

    @MockBean
    private ProductService productService;

    @MockBean
    BrandService brandService;

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

    private SaveRequest createSaveRequest() {
        return SaveRequest.builder()
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

    private ProductInfoResponse createProductInfo() {
        return ProductInfoResponse.builder()
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
            .brand(createBrandInfo())
            .resizedImagePath(
                "https://shoeauction-brands-resized.s3.ap-northeast-2.amazonaws.com/brand.png")
            .purchaseBids(createPurchaseBids())
            .saleBids(createSales())
            .tradeCompleteInfos(createCompleteTrades())
            .build();
    }

    private List<TradeCompleteInfo> createCompleteTrades() {
        List<TradeCompleteInfo> list = new ArrayList<>();
        TradeCompleteInfo tradeCompleteInfo = TradeCompleteInfo.builder()
            .completeTime(LocalDateTime.now())
            .price(300000L)
            .productSize(280.0)
            .build();
        list.add(tradeCompleteInfo);
        return list;
    }

    private ThumbnailResponse createProductThumbnail() {
        return ThumbnailResponse.builder()
            .id(99L)
            .productThumbnailImagePath(
                "https://shoeauction-products-thumbnail.s3.ap-northeast-2.amazonaws.com/product.png")
            .brandThumbnailImagePath(
                "https://shoeauction-brands-thumbnail.s3.ap-northeast-2.amazonaws.com/brand.png")
            .nameKor("사카이")
            .nameEng("Sakai")
            .lowestPrice(500000L)
            .build();
    }

    private Pageable createPageable() {
        return PageRequest.of(0, 10);
    }

    private Page<ThumbnailResponse> createProductThumbnailsPage() {
        List<ThumbnailResponse> thumbnailList = new ArrayList<>();
        thumbnailList.add(createProductThumbnail());
        Pageable pageable = createPageable();

        return new PageImpl<>(thumbnailList, pageable, 1);
    }

    private SearchCondition createSearchCondition() {
        return SearchCondition.builder()
            .brandId(1L)
            .keyword("ka")
            .orderStandard(OrderStandard.LOW_PRICE).build();
    }

    private List<TradeBidResponse> createPurchaseBids() {

        List<TradeBidResponse> purchaseBids = new ArrayList<>();

        TradeBidResponse tradeBidResponse = TradeBidResponse.builder()
            .tradeId(5L)
            .productId(1L)
            .productSize(260.0)
            .price(300000L)
            .build();

        purchaseBids.add(tradeBidResponse);

        return purchaseBids;
    }

    private List<TradeBidResponse> createSales() {

        List<TradeBidResponse> saleBids = new ArrayList<>();

        TradeBidResponse tradeBidResponse = TradeBidResponse.builder()
            .tradeId(5L)
            .productId(1L)
            .productSize(260.0)
            .price(200000L)
            .build();

        saleBids.add(tradeBidResponse);

        return saleBids;
    }


    private MockMultipartFile createImageFile() {
        return new MockMultipartFile("productImage", "productImage", MediaType.IMAGE_PNG_VALUE,
            "sample".getBytes());
    }

    private MockMultipartFile convertMultipartFile(Object dto)
        throws JsonProcessingException {
        return new MockMultipartFile("requestDto", "requestDto", MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsString(dto).getBytes(
                StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("상품을 생성한다.")
    public void createProduct() throws Exception {
        SaveRequest saveRequest = createSaveRequest();
        MockMultipartFile requestDto = convertMultipartFile(saveRequest);
        MockMultipartFile productImage = createImageFile();

        doNothing().when(productService).saveProduct(saveRequest, productImage);

        mockMvc.perform(
            multipart("/products")
                .file(requestDto)
                .file(productImage)
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(document("products/create",
                requestPartFields("requestDto",
                    fieldWithPath("nameKor").type(JsonFieldType.STRING).description("저장할 상품의 한글명"),
                    fieldWithPath("nameEng").type(JsonFieldType.STRING).description("저장할 상품의 영문명"),
                    fieldWithPath("modelNumber").type(JsonFieldType.STRING)
                        .description("저장할 상품의 모델 넘버"),
                    fieldWithPath("color").type(JsonFieldType.STRING).description("저장할 상품의 색상"),
                    fieldWithPath("releaseDate").type(JsonFieldType.STRING)
                        .description("저장할 상품의 발매일"),
                    fieldWithPath("releasePrice").type(JsonFieldType.NUMBER)
                        .description("저장할 상품의 발매가"),
                    fieldWithPath("currency").type(JsonFieldType.STRING)
                        .description("저장할 상품의 발매 통화"),
                    fieldWithPath("sizeClassification").type(JsonFieldType.STRING)
                        .description("저장할 상품의 사이즈 분류"),
                    fieldWithPath("sizeUnit").type(JsonFieldType.STRING)
                        .description("저장할 상품의 사이즈 단위"),
                    fieldWithPath("minSize").type(JsonFieldType.NUMBER)
                        .description("저장할 상품의 최소 사이즈"),
                    fieldWithPath("maxSize").type(JsonFieldType.NUMBER)
                        .description("저장할 상품의 최대 사이즈"),
                    fieldWithPath("sizeGap").type(JsonFieldType.NUMBER)
                        .description("저장할 상품의 사이즈 간격"),
                    fieldWithPath("brand").type(JsonFieldType.OBJECT).description("저장할 상품의 브랜드"),
                    fieldWithPath("brand.id").ignored(),
                    fieldWithPath("brand.nameKor").ignored(),
                    fieldWithPath("brand.nameEng").ignored(),
                    fieldWithPath("brand.originImagePath").ignored(),
                    fieldWithPath("brand.thumbnailImagePath").ignored(),
                    fieldWithPath("originImagePath").ignored(),
                    fieldWithPath("thumbnailImagePath").ignored(),
                    fieldWithPath("resizedImagePath").ignored()),
                requestParts(
                    partWithName("requestDto").ignored(),
                    partWithName("productImage").description("저장할 상품의 이미지 파일").optional())
            ));
    }

    @Test
    @DisplayName("상품의 상세 정보를 조회한다.")
    void getProductInfo() throws Exception {
        ProductInfoResponse productInfo = createProductInfo();
        Long id = productInfo.getId();
        given(productService.getProductInfo(id)).willReturn(productInfo);

        mockMvc.perform(
            get("/products/{id}", id)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("products/get/details",
                pathParameters(
                    parameterWithName("id").description("조회할 상품의 ID")
                ),
                responseFields(
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("조회한 상품의 ID"),
                    fieldWithPath("nameKor").type(JsonFieldType.STRING).description("조회한 상품의 한글명"),
                    fieldWithPath("nameEng").type(JsonFieldType.STRING).description("조회한 상품의 영문명"),
                    fieldWithPath("modelNumber").type(JsonFieldType.STRING)
                        .description("조회한 상품의 모델 넘버"),
                    fieldWithPath("color").type(JsonFieldType.STRING).description("조회한 상품의 색상"),
                    fieldWithPath("releaseDate").type(JsonFieldType.STRING)
                        .description("조회한 상품의 출시일"),
                    fieldWithPath("releasePrice").type(JsonFieldType.NUMBER)
                        .description("조회한 상품의 출시가"),
                    fieldWithPath("currency").type(JsonFieldType.STRING)
                        .description("조회한 상품의 발매 통화"),
                    fieldWithPath("sizeClassification").type(JsonFieldType.STRING)
                        .description("조회한 상품의 사이즈 분류"),
                    fieldWithPath("sizeUnit").type(JsonFieldType.STRING)
                        .description("조회한 상품의 사이즈 단위"),
                    fieldWithPath("minSize").type(JsonFieldType.NUMBER)
                        .description("조회한 상품의 최소 사이즈"),
                    fieldWithPath("maxSize").type(JsonFieldType.NUMBER)
                        .description("조회한 상품의 최대 사이즈"),
                    fieldWithPath("sizeGap").type(JsonFieldType.NUMBER)
                        .description("조회한 상품의 사이즈 간격"),
                    fieldWithPath("brand").type(JsonFieldType.OBJECT).description("조회한 상품의 브랜드"),
                    fieldWithPath("brand.id").ignored(),
                    fieldWithPath("brand.nameKor").ignored(),
                    fieldWithPath("brand.nameEng").ignored(),
                    fieldWithPath("brand.originImagePath").ignored(),
                    fieldWithPath("brand.thumbnailImagePath").ignored(),
                    fieldWithPath("resizedImagePath").type(JsonFieldType.STRING)
                        .description("조회한 상품의 이미지 경로"),
                    fieldWithPath("saleBids.[].tradeId").type(JsonFieldType.NUMBER)
                        .description("판매 입찰 ID"),
                    fieldWithPath("saleBids.[].productId").type(JsonFieldType.NUMBER)
                        .description("판매 입찰 productID"),
                    fieldWithPath("saleBids.[].productSize").type(JsonFieldType.NUMBER)
                        .description("판매 입찰 물품 사이즈"),
                    fieldWithPath("saleBids.[].price").type(JsonFieldType.NUMBER)
                        .description("판매 입찰 물품 가격"),
                    fieldWithPath("purchaseBids.[].tradeId").type(JsonFieldType.NUMBER)
                        .description("구매 입찰  ID"),
                    fieldWithPath("purchaseBids.[].productId").type(JsonFieldType.NUMBER)
                        .description("구매 입찰  productId"),
                    fieldWithPath("purchaseBids.[].productSize").type(JsonFieldType.NUMBER)
                        .description("구매 입찰 물품 사이즈"),
                    fieldWithPath("purchaseBids.[].price").type(JsonFieldType.NUMBER)
                        .description("구매 입찰 물품 가격"),
                    fieldWithPath("tradeCompleteInfos.[].productSize").type(JsonFieldType.NUMBER)
                        .description("완료된 거래의 물품 사이즈"),
                    fieldWithPath("tradeCompleteInfos.[].price").type(JsonFieldType.NUMBER)
                        .description("완료된 거래의 물품 가격"),
                    fieldWithPath("tradeCompleteInfos.[].completeTime").type(JsonFieldType.STRING)
                        .description("거래 완료 시간")
                )
            ));
    }

    @Test
    @DisplayName("검색 조건 별 상품들의 썸네일을 조회한다.")
    void findProducts() throws Exception {
        Page<ThumbnailResponse> productThumbnailsPage = createProductThumbnailsPage();
        given(productService.findProducts(any(), any()))
            .willReturn(productThumbnailsPage);

        mockMvc.perform(
            get("/products?brandId=1&keyword=ka&orderStandard=LOW_PRICE")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("products/get",
                requestParameters(
                    parameterWithName("brandId").description("조회할 상품 썸네일의 브랜드 ID").optional(),
                    parameterWithName("keyword").description("조회할 상품의 검색 키워드").optional(),
                    parameterWithName("orderStandard").description("조회할 상품의 정렬 조건").optional()
                ),
                responseFields(
                    fieldWithPath("content.[].id").type(JsonFieldType.NUMBER)
                        .description("조회한 상품의 ID"),
                    fieldWithPath("content.[].productThumbnailImagePath").type(JsonFieldType.STRING)
                        .description("조회한 상품의 썸네일 이미지 경로"),
                    fieldWithPath("content.[].brandThumbnailImagePath").type(JsonFieldType.STRING)
                        .description("조회한 상품의 브랜드 썸네일 이미지 경로"),
                    fieldWithPath("content.[].nameKor").type(JsonFieldType.STRING)
                        .description("조회한 상품의 한글명"),
                    fieldWithPath("content.[].nameEng").type(JsonFieldType.STRING)
                        .description("조회한 상품의 영문명"),
                    fieldWithPath("content.[].lowestPrice").type(JsonFieldType.NUMBER)
                        .description("조회한 상품의 즉시 구매 최저가"),
                    fieldWithPath("pageable.offset").ignored(),
                    fieldWithPath("pageable.pageSize").ignored(),
                    fieldWithPath("pageable.pageNumber").ignored(),
                    fieldWithPath("pageable.paged").ignored(),
                    fieldWithPath("pageable.unpaged").ignored(),
                    fieldWithPath("pageable.sort.sorted").ignored(),
                    fieldWithPath("pageable.sort.unsorted").ignored(),
                    fieldWithPath("pageable.sort.empty").ignored(),
                    fieldWithPath("sort.empty").ignored(),
                    fieldWithPath("sort.sorted").ignored(),
                    fieldWithPath("sort.unsorted").ignored(),
                    fieldWithPath("totalPages").ignored(),
                    fieldWithPath("size").ignored(),
                    fieldWithPath("number").ignored(),
                    fieldWithPath("first").ignored(),
                    fieldWithPath("last").ignored(),
                    fieldWithPath("numberOfElements").ignored(),
                    fieldWithPath("empty").ignored(),
                    fieldWithPath("totalElements").ignored()
                )
            ));
    }

    @Test
    @DisplayName("상품의 정보를 수정한다.")
    void updateProduct() throws Exception {
        Long id = 1L;
        SaveRequest updateRequest = createSaveRequest();
        MockMultipartFile requestDto = convertMultipartFile(updateRequest);
        MockMultipartFile productImage = createImageFile();

        MockMultipartHttpServletRequestBuilder builder =
            RestDocumentationRequestBuilders.fileUpload("/products/{id}", id);
        builder.with(request -> {
            request.setMethod("PATCH");
            return request;
        });

        mockMvc.perform(
            builder
                .file(requestDto)
                .file(productImage)
                .characterEncoding("utf-8")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("products/update",
                pathParameters(
                    parameterWithName("id").description("수정할 상품의 ID")
                ),
                requestPartFields("requestDto",
                    fieldWithPath("nameKor").type(JsonFieldType.STRING).description("수정할 상품의 한글명"),
                    fieldWithPath("nameEng").type(JsonFieldType.STRING).description("수정할 상품의 영문명"),
                    fieldWithPath("modelNumber").type(JsonFieldType.STRING)
                        .description("수정할 상품의 모델 넘버"),
                    fieldWithPath("color").type(JsonFieldType.STRING).description("수정할 상품의 색상"),
                    fieldWithPath("releaseDate").type(JsonFieldType.STRING)
                        .description("수정할 상품의 발매일"),
                    fieldWithPath("releasePrice").type(JsonFieldType.NUMBER)
                        .description("수정할 상품의 발매가"),
                    fieldWithPath("currency").type(JsonFieldType.STRING)
                        .description("수정할 상품의 발매 통화"),
                    fieldWithPath("sizeClassification").type(JsonFieldType.STRING)
                        .description("수정할 상품의 사이즈 분류"),
                    fieldWithPath("sizeUnit").type(JsonFieldType.STRING)
                        .description("수정할 상품의 사이즈 단위"),
                    fieldWithPath("minSize").type(JsonFieldType.NUMBER)
                        .description("수정할 상품의 최소 사이즈"),
                    fieldWithPath("maxSize").type(JsonFieldType.NUMBER)
                        .description("수정할 상품의 최대 사이즈"),
                    fieldWithPath("sizeGap").type(JsonFieldType.NUMBER)
                        .description("수정할 상품의 사이즈 간격"),
                    fieldWithPath("brand").type(JsonFieldType.OBJECT).description("수정할 상품의 브랜드"),
                    fieldWithPath("brand.id").ignored(),
                    fieldWithPath("brand.nameKor").ignored(),
                    fieldWithPath("brand.nameEng").ignored(),
                    fieldWithPath("brand.originImagePath").ignored(),
                    fieldWithPath("brand.thumbnailImagePath").ignored(),
                    fieldWithPath("originImagePath").type(JsonFieldType.STRING)
                        .description("기존 상품 원본 이미지 경로(null이라면 기존 이미지 삭제)"),
                    fieldWithPath("thumbnailImagePath").ignored(),
                    fieldWithPath("resizedImagePath").ignored()),
                requestParts(
                    partWithName("requestDto").ignored(),
                    partWithName("productImage").description("수정할 상품의 이미지 파일").optional())
            ));
    }

    @Test
    @DisplayName("상품의 정보를 삭제한다.")
    void deleteProduct() throws Exception {
        Long id = 1L;

        doNothing().when(productService).deleteProduct(id);

        mockMvc.perform(
            delete("/products/{id}", id))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document(
                "products/delete",
                pathParameters(
                    parameterWithName("id").description("삭제할 상품의 ID"))
            ));
    }

    @Test
    @DisplayName("상품의 통화 단위들을 조회한다.")
    void getCurrencies() throws Exception {
        Currency[] currencies = Currency.values();

        mockMvc.perform(
            get("/products/currencies"))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document(
                "products/get/currencies",
                responseFields(
                    fieldWithPath("[]").type(JsonFieldType.ARRAY)
                        .description("상품의 통화 단위 목록"))
            ));
    }

    @Test
    @DisplayName("상품의 사이즈 분류들을 조회한다.")
    void getSizeClassifications() throws Exception {
        mockMvc.perform(
            get("/products/size-classifications"))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document(
                "products/get/size-classifications",
                responseFields(
                    fieldWithPath("[]").type(JsonFieldType.ARRAY)
                        .description("상품의 사이즈 분류 목록"))
            ));
    }

    @Test
    @DisplayName("상품의 사이즈 단위들을 조회한다.")
    void getSizeUnits() throws Exception {
        mockMvc.perform(
            get("/products/size-units"))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document(
                "products/get/size-units",
                responseFields(
                    fieldWithPath("[]").type(JsonFieldType.ARRAY)
                        .description("상품의 사이즈 단위 목록"))
            ));
    }

    @Test
    @DisplayName("상품의 정렬 기준들을 조회한다.")
    void getOrderStandard() throws Exception {
        mockMvc.perform(
            get("/products/order-standards"))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document(
                "products/get/order-standards",
                responseFields(
                    fieldWithPath("[]").type(JsonFieldType.ARRAY)
                        .description("상품의 정렬 기준 목록"))
            ));
    }
}
