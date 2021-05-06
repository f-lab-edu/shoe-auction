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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.shoeauction.controller.dto.PointDto.ChargeRequest;
import com.flab.shoeauction.controller.dto.PointDto.PointHistoryDto;
import com.flab.shoeauction.controller.dto.PointDto.WithdrawalRequest;
import com.flab.shoeauction.domain.point.PointDivision;
import com.flab.shoeauction.service.PointService;
import com.flab.shoeauction.service.SessionLoginService;
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
@WebMvcTest(PointApiController.class)
@ActiveProfiles("test")
@MockBean(JpaMetamodelMappingContext.class)
class PointApiControllerTest {

    @MockBean
    private PointService pointService;
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

    private List<PointHistoryDto> createPointChargeHistoryList() {

        List<PointHistoryDto> pointHistoryDtoList = new ArrayList<>();

        PointHistoryDto chargeMockData = PointHistoryDto.builder()
            .time(LocalDateTime.now())
            .division(PointDivision.CHARGE)
            .amount(30000L)
            .build();

        pointHistoryDtoList.add(chargeMockData);

        PointHistoryDto saleMockData = PointHistoryDto.builder()
            .time(LocalDateTime.now())
            .division(PointDivision.SALES_REVENUE)
            .amount(40000L)
            .build();

        pointHistoryDtoList.add(saleMockData);

        return pointHistoryDtoList;
    }

    private List<PointHistoryDto> createPointDeductionHistoryList() {
        List<PointHistoryDto> pointHistoryDtoList = new ArrayList<>();

        PointHistoryDto withdrawMockData = PointHistoryDto.builder()
            .time(LocalDateTime.now())
            .division(PointDivision.WITHDRAW)
            .amount(10000L)
            .build();

        pointHistoryDtoList.add(withdrawMockData);

        PointHistoryDto purchaseMockData = PointHistoryDto.builder()
            .time(LocalDateTime.now())
            .division(PointDivision.PURCHASE_DEDUCTION)
            .amount(20000L)
            .build();

        pointHistoryDtoList.add(purchaseMockData);

        return pointHistoryDtoList;
    }

    @DisplayName("포인트를 충전한다.")
    @Test
    public void payment() throws Exception {
        ChargeRequest chargeRequest = ChargeRequest.builder()
            .chargeAmount(100000L)
            .build();

        String email = "test123@test.com";

        doNothing().when(pointService).charging(email, chargeRequest);

        mockMvc.perform(post("/points/charging")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(chargeRequest)))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("points/charging", requestFields(
                fieldWithPath("chargeAmount").type(JsonFieldType.NUMBER).description("충전할 포인트")
            )));
    }

    @DisplayName("포인트를 출금한다.")
    @Test
    public void withdrawal() throws Exception {
        WithdrawalRequest withdrawalRequest = WithdrawalRequest.builder()
            .password("test1234")
            .withdrawalAmount(30000L)
            .build();

        String email = "test123@test.com";

        doNothing().when(pointService).withdrawal(email, withdrawalRequest);

        mockMvc.perform(post("/points/withdrawal")
            .content(objectMapper.writeValueAsString(withdrawalRequest))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("points/withdrawal", requestFields(
                fieldWithPath("withdrawalAmount").type(JsonFieldType.NUMBER).description("출금할 포인트"),
                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
            )));
    }

    @DisplayName("포인트 추가 내역(판매 대금 / 충전)을 리턴한다.")
    @Test
    public void chargingHistory() throws Exception {
        List<PointHistoryDto> chargingHistory = createPointChargeHistoryList();
        given(pointService.getChargingHistory(any())).willReturn(chargingHistory);

        mockMvc.perform(get("/points/charging-details")
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("points/history/charging",
                responseFields(
                    fieldWithPath("[].time").type(JsonFieldType.STRING).description("생성 날짜"),
                    fieldWithPath("[].amount").type(JsonFieldType.NUMBER).description("충전 금액"),
                    fieldWithPath("[].division").type(JsonFieldType.STRING).description("종류")
                )));
    }

    @DisplayName("포인트 차감 내역(구매 대금 / 출금)을 리턴한다.")
    @Test
    public void deductionHistory() throws Exception {
        List<PointHistoryDto> deductionHistory = createPointDeductionHistoryList();
        given(pointService.getDeductionHistory(any())).willReturn(deductionHistory);

        mockMvc.perform(get("/points/deduction-details")
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("points/history/deduction",
                responseFields(
                    fieldWithPath("[].time").type(JsonFieldType.STRING).description("생성 날짜"),
                    fieldWithPath("[].amount").type(JsonFieldType.NUMBER).description("차감 금액"),
                    fieldWithPath("[].division").type(JsonFieldType.STRING).description("종류")
                )));
    }


}