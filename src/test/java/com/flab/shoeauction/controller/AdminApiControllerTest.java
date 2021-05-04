package com.flab.shoeauction.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.shoeauction.controller.dto.UserDto.UserBanRequest;
import com.flab.shoeauction.controller.dto.UserDto.UserDetailsResponse;
import com.flab.shoeauction.controller.dto.UserDto.UserListResponse;
import com.flab.shoeauction.domain.users.common.Account;
import com.flab.shoeauction.domain.users.common.UserLevel;
import com.flab.shoeauction.domain.users.common.UserStatus;
import com.flab.shoeauction.service.AdminService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@WebMvcTest(AdminApiController.class)
@ActiveProfiles("test")
@MockBean(JpaMetamodelMappingContext.class)
class AdminApiControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;
    @MockBean
    private SessionLoginService sessionLoginService;

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

    private List<UserListResponse> setUsers() {
        List<UserListResponse> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            UserListResponse userListResponse = UserListResponse.builder()
                .id((long) i)
                .email("jungkh405@naver.com" + i)
                .userLevel(UserLevel.AUTH)
                .build();
            list.add(userListResponse);
        }
        return list;
    }


    @DisplayName("관리자가 회원 전체를 조회한다.")
    @Test
    public void getAllUsers() throws Exception {
        List<UserListResponse> list = setUsers();

        long total = list.size();
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserListResponse> result = new PageImpl<>(list, pageable, total);

        given(adminService.findUsers(any(), any())).willReturn(result);

        mockMvc.perform(get("/admin/users")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.[0].id").value(list.get(0).getId()))
            .andExpect(jsonPath("$.content.[0].email").value(list.get(0).getEmail()))
            .andExpect(jsonPath("$.content.[0].userLevel").value("AUTH"))
            .andExpect(jsonPath("$.content.[1].id").value(list.get(1).getId()))
            .andExpect(jsonPath("$.content.[1].email").value(list.get(1).getEmail()))
            .andExpect(jsonPath("$.content.[1].userLevel").value("AUTH"))
            .andDo(print())
            .andDo(document("admin/get/findAll",
                responseFields(
                    fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("ID"),
                    fieldWithPath("content.[].email").type(JsonFieldType.STRING)
                        .description("email"),
                    fieldWithPath("content.[].userLevel").type(JsonFieldType.STRING)
                        .description("userLevel"),
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
    //TODO id로 검색, email로 검색, userLevel로 검색 , BAN 때리기 (ADMIN) / BAN 상태 접속 불가(USER)


    @DisplayName("관리자가 ID[PK]로 회원을 검색한다. ")
    @Test
    public void findById() throws Exception {

        List<UserListResponse> list = setUsers();
        long total = list.size();
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserListResponse> result = new PageImpl<>(list, pageable, total);

        given(adminService.findUsers(any(), any())).willReturn(result);

        mockMvc.perform(get("/admin/users?id=1")
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.[0].id").value(list.get(0).getId()))
            .andExpect(jsonPath("$.content.[0].email").value(list.get(0).getEmail()))
            .andExpect(jsonPath("$.content.[0].userLevel").value("AUTH"))
            .andDo(document("admin/get/findById",
                requestParameters(
                    parameterWithName("id").description("검색할 회원의 ID[PK]")
                ),
                responseFields(
                    fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("ID"),
                    fieldWithPath("content.[].email").type(JsonFieldType.STRING).description("email"),
                    fieldWithPath("content.[].userLevel").ignored(),
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

    @DisplayName("관리자가 이메일로 회원을 검색한다. ")
    @Test
    public void findByEmail() throws Exception {

        List<UserListResponse> list = setUsers();
        long total = list.size();
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserListResponse> result = new PageImpl<>(list, pageable, total);

        given(adminService.findUsers(any(), any())).willReturn(result);

        mockMvc.perform(get("/admin/users?email=jungkh405@naver.com0")
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.[0].id").value(list.get(0).getId()))
            .andExpect(jsonPath("$.content.[0].email").value(list.get(0).getEmail()))
            .andExpect(jsonPath("$.content.[0].userLevel").value("AUTH"))
            .andDo(document("admin/get/findByEmail",
                requestParameters(
                    parameterWithName("email").description("검색할 회원의 email")
                ),
                responseFields(
                    fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("ID"),
                    fieldWithPath("content.[].email").type(JsonFieldType.STRING)
                        .description("email"),
                    fieldWithPath("content.[].userLevel").type(JsonFieldType.STRING)
                        .description("userLevel"),
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


    @DisplayName("관리자가 UserLevel로 회원을 검색한다. ")
    @Test
    public void findByUserLevel() throws Exception {

        List<UserListResponse> list = setUsers();
        long total = list.size();
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserListResponse> result = new PageImpl<>(list, pageable, total);

        given(adminService.findUsers(any(), any())).willReturn(result);

        mockMvc.perform(get("/admin/users?userLevel=AUTH")
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.[0].id").value(list.get(0).getId()))
            .andExpect(jsonPath("$.content.[0].email").value(list.get(0).getEmail()))
            .andExpect(jsonPath("$.content.[0].userLevel").value("AUTH"))
            .andExpect(jsonPath("$.content.[1].id").value(list.get(1).getId()))
            .andExpect(jsonPath("$.content.[1].email").value(list.get(1).getEmail()))
            .andExpect(jsonPath("$.content.[1].userLevel").value("AUTH"))
            .andDo(document("admin/get/findByUserLevel",
                requestParameters(
                    parameterWithName("userLevel").description("검색할 회원의 userLevel")
                ),
                responseFields(
                    fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("ID"),
                    fieldWithPath("content.[].email").type(JsonFieldType.STRING)
                        .description("email"),
                    fieldWithPath("content.[].userLevel").type(JsonFieldType.STRING)
                        .description("userLevel"),
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

    @DisplayName("관리자가 회원의 상세정보를 조회한다.")
    @Test
    public void getUserDetails() throws Exception {
        UserDetailsResponse userDetailsResponse = UserDetailsResponse.builder()
            .id(1L)
            .email("jungkh405@naver.com")
            .nickname("17171771")
            .phone("01012345678")
            .account(new Account("카카오뱅크", "333333333333", "마리아"))
            .modifiedDate(LocalDateTime.now())
            .createDate(LocalDateTime.now())
            .userLevel(UserLevel.UNAUTH)
            .userStatus(UserStatus.NORMAL)
            .build();

        given(adminService.getUser(any())).willReturn(userDetailsResponse);

        mockMvc.perform(get("/admin/users/{id}", 1)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("admin/get/details", pathParameters(
                parameterWithName("id").description("상세 정보를 조회할 회원의 ID[PK]")
                ),
                responseFields(
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 ID[PK]"),
                    fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일"),
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                    fieldWithPath("phone").type(JsonFieldType.STRING).description("회원 휴대폰번호"),
                    subsectionWithPath("account").type(JsonFieldType.OBJECT)
                        .description("회원의 계좌정보"),
                    fieldWithPath("createDate").type(JsonFieldType.VARIES).description("회원 가입 일시"),
                    fieldWithPath("modifiedDate").type(JsonFieldType.VARIES)
                        .description("회원 정보 수정 일시"),
                    fieldWithPath("userLevel").type(JsonFieldType.STRING)
                        .description("회원 권한[UNAUTH/AUTH/ADMIN"),
                    fieldWithPath("userStatus").type(JsonFieldType.STRING)
                        .description("회원 상태[BAN/NORMAL]")
                )

            ));

    }

    @DisplayName("관리자가 회원의 status를 BAN또는 NORMAL로 변경한다.")
    @Test
    public void restrictUsers() throws Exception {
        UserBanRequest userBanRequest = UserBanRequest.builder()
            .id(1L)
            .userStatus(UserStatus.BAN)
            .build();

        mockMvc.perform(post("/admin/users/ban")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userBanRequest)))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("admin/ban", requestFields(
                fieldWithPath("id").type(JsonFieldType.NUMBER)
                    .description("BAN또는 NORMAL 처리할 회원의 IP[PK]"),
                fieldWithPath("userStatus").type(JsonFieldType.STRING).description("BAN / NORMAL")
            )));
    }
}