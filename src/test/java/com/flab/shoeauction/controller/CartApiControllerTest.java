package com.flab.shoeauction.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.shoeauction.controller.dto.ProductDto.IdRequest;
import com.flab.shoeauction.controller.dto.ProductDto.WishItemResponse;
import com.flab.shoeauction.domain.brand.Brand;
import com.flab.shoeauction.service.CartService;
import com.flab.shoeauction.service.SessionLoginService;
import java.util.HashSet;
import java.util.Set;
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
@WebMvcTest(CartApiController.class)
@ActiveProfiles("test")
@MockBean(JpaMetamodelMappingContext.class)
class CartApiControllerTest {

    @MockBean
    private CartService cartService;
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

    private Set<WishItemResponse> createWishList() {
        Set<WishItemResponse> set = new HashSet<>();

        WishItemResponse wishItemResponse = WishItemResponse.builder()
            .id(1L)
            .nameKor("조던")
            .nameEng("Jordan")
            .productId(2L)
            .brand(new Brand(3L, "나이키", "nike", "1234", "5678")).build();

        set.add(wishItemResponse);

        return set;
    }


    @Test
    @DisplayName("카트 - 위시리스트를 조회한다.")
    void getWishList() throws Exception {
        Set<WishItemResponse> wishList = createWishList();
        given(cartService.getWishList(any())).willReturn(wishList);

        mockMvc.perform(get("/carts"))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("users/carts/getWishList", responseFields(
                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("CartProduct ID"),
                fieldWithPath("[].productId").type(JsonFieldType.NUMBER).description("상품의 ID[PK]"),
                fieldWithPath("[].nameKor").type(JsonFieldType.STRING).description("상품 이름(한글)"),
                fieldWithPath("[].nameEng").type(JsonFieldType.STRING).description("상품 이름(영어"),
                fieldWithPath("[].brand.createdDate").type(JsonFieldType.VARIES)
                    .description("브랜드 생성 날짜"),
                fieldWithPath("[].brand.modifiedDate").type(JsonFieldType.VARIES)
                    .description("브랜드 정보 수정 날짜"),
                fieldWithPath("[].brand.id").type(JsonFieldType.NUMBER).description("브랜드 ID[PK]"),
                fieldWithPath("[].brand.nameKor").type(JsonFieldType.STRING)
                    .description("브랜드 이름(한글)"),
                fieldWithPath("[].brand.nameEng").type(JsonFieldType.STRING)
                    .description("브랜드 이름(영어)"),
                fieldWithPath("[].brand.originImagePath").type(JsonFieldType.STRING)
                    .description("브랜드 이미지 경로"),
                fieldWithPath("[].brand.thumbnailImagePath").type(JsonFieldType.STRING)
                    .description("브랜드 이미지 경로(썸네일)")
            )));
    }

    @Test
    @DisplayName("카트 - 위시리스트에 상품을 추가한다.")
    void addWishList() throws Exception {
        String email = "jungkh405@naver.com";
        IdRequest idRequest = IdRequest.builder()
            .id(1L).build();

        doNothing().when(cartService).addWishList(email, idRequest);

        mockMvc.perform(post("/carts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(idRequest)))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("users/carts/addWishList", requestFields(
                fieldWithPath("id").type(JsonFieldType.NUMBER).description("상품(Product)의 ID[PK]")
            )));
    }

    @Test
    @DisplayName("카트 - 위시리스트에 등록된 상품을 삭제한다.")
    void deleteWishList() throws Exception {
        IdRequest idRequest = IdRequest.builder()
            .id(1L).build();

        doNothing().when(cartService).deleteWishList(idRequest);

        mockMvc.perform(delete("/carts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(idRequest)))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("users/carts/deleteWishList", requestFields(
                fieldWithPath("id").type(JsonFieldType.NUMBER)
                    .description("카트상품(CartProduct)의 ID[PK]")
            )));
    }
}