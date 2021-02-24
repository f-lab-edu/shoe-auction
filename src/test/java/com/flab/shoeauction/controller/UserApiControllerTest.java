package com.flab.shoeauction.controller;

import static com.flab.shoeauction.common.utils.constants.UserConstants.USER_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.shoeauction.controller.dto.UserDto.LoginRequest;
import com.flab.shoeauction.controller.dto.UserDto.SaveRequest;
import com.flab.shoeauction.controller.dto.UserDto.SmsCertificationRequest;
import com.flab.shoeauction.controller.dto.UserDto.UserInfoDto;
import com.flab.shoeauction.domain.addressBook.Address;
import com.flab.shoeauction.domain.addressBook.AddressBook;
import com.flab.shoeauction.domain.users.common.Account;
import com.flab.shoeauction.exception.user.DuplicateEmailException;
import com.flab.shoeauction.exception.user.TokenExpiredException;
import com.flab.shoeauction.exception.user.UserNotFoundException;
import com.flab.shoeauction.service.SessionLoginService;
import com.flab.shoeauction.service.UserService;
import com.flab.shoeauction.service.certification.EmailCertificationService;
import com.flab.shoeauction.service.certification.SmsCertificationService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(UserApiController.class)
@ActiveProfiles("test")
@MockBean(JpaMetamodelMappingContext.class)
class UserApiControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private SmsCertificationService smsCertificationService;

    @MockBean
    private EmailCertificationService emailCertificationService;

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

    @Test
    @DisplayName("회원가입 - 모든 유효성 검사에 통과했다면 회원가입에 성공한다.")
    void createUser_successful() throws Exception {
        SaveRequest saveRequest = SaveRequest.builder()
            .email("jungkh405@naver.com")
            .password("test1234")
            .nickname("17171771")
            .phone("01012345678")
            .build();

        doNothing().when(userService).save(saveRequest);

        mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(saveRequest)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(document("users/create/successful", requestFields(
                fieldWithPath("email").type(JsonFieldType.STRING)
                    .description("The user's email address"),
                fieldWithPath("password").type(JsonFieldType.STRING)
                    .description("The user's password"),
                fieldWithPath("nickname").type(JsonFieldType.STRING)
                    .description("The user's nickname"),
                fieldWithPath("phone").type(JsonFieldType.STRING).description("The user's phone")
            )));
    }

    @Test
    @DisplayName("회원가입 - 중복된 닉네임 또는 중복된 이메일로 회원가입에 실패한다.")
    void createUser_failure() throws Exception {
        SaveRequest saveRequest = SaveRequest.builder()
            .email("jungkh405@naver.com")
            .password("test1234")
            .nickname("17171771")
            .phone("01012345678")
            .build();

        doThrow(new DuplicateEmailException()).when(userService).save(any());

        mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(saveRequest)))
            .andDo(print())
            .andExpect(status().isConflict())
            .andDo(document("users/create/failure", requestFields(
                fieldWithPath("email").type(JsonFieldType.STRING)
                    .description("로그인시 사용할 이메일"),
                fieldWithPath("password").type(JsonFieldType.STRING)
                    .description("8자 이상 20자 이하의 비밀번호"),
                fieldWithPath("nickname").type(JsonFieldType.STRING)
                    .description("닉네임"),
                fieldWithPath("phone").type(JsonFieldType.STRING).description("휴대폰 번호")
            )));
    }


    @Test
    @DisplayName("이메일 중복 검사 - 중복된 이메일")
    void DuplicateEmailCheck_failure() throws Exception {
        String email = "jungkh405@naver.com";
        given(userService.checkEmailDuplicate(email)).willReturn(true);

        mockMvc.perform(get("/users/user-emails/{email}/exists", email))
            .andExpect(status().isOk())
            .andExpect(content().string("true"))
            .andDo(document("users/duplicateEmail/failure",
                pathParameters(
                    parameterWithName("email").description("이메일"))));
    }

    @Test
    @DisplayName("이메일 중복 검사 - 사용 가능한 이메일")
    void DuplicateEmailCheck_successful() throws Exception {
        String email = "jungkh405@naver.com";
        given(userService.checkEmailDuplicate(email)).willReturn(false);

        mockMvc.perform(get("/users/user-emails/{email}/exists", email))
            .andExpect(status().isOk())
            .andExpect(content().string("false"))
            .andDo(document("users/duplicateEmail/successful",
                pathParameters(
                    parameterWithName("email").description("이메일"))));
    }

    @Test
    @DisplayName("닉네임 중복 검사 - 중복된 닉네임")
    void DuplicateNicknameCheck_failure() throws Exception {
        String nickname = "Lewandowski";
        given(userService.checkNicknameDuplicate(nickname)).willReturn(true);

        mockMvc.perform(get("/users/user-nicknames/{nickname}/exists", nickname))
            .andExpect(status().isOk())
            .andExpect(content().string("true"))
            .andDo(document("users/duplicateNickname/failure",
                pathParameters(
                    parameterWithName("nickname").description("닉네임"))));
    }

    @Test
    @DisplayName("닉네임 중복 검사 - 사용가능한 닉네임")
    void DuplicateNicknameCheck_successful() throws Exception {
        String nickname = "Lewandowski";
        given(userService.checkNicknameDuplicate(nickname)).willReturn(false);

        mockMvc.perform(get("/users/user-nicknames/{nickname}/exists", nickname))
            .andExpect(status().isOk())
            .andExpect(content().string("false"))
            .andDo(document("users/duplicateNickname/successful",
                pathParameters(
                    parameterWithName("nickname").description("닉네임"))));
    }

    @Test
    @DisplayName("이메일 인증 - 회원가입시 발송된 이메일에서 토큰 링크를 클릭하면 정상적으로 인증에 성공한다.")
    void emailTokenCertification_successful() throws Exception {
        String token = UUID.randomUUID().toString();
        String email = "jungkh405@naver.com";

        doNothing().when(userService).updateEmailVerified(token, email);

        mockMvc.perform(get("/users/email-check-token")
            .param("token", token)
            .param("email", email))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("users/emailAuth/successful", requestParameters(
                parameterWithName("token").description("회원가입시 발송되는 랜덤 토큰"),
                parameterWithName("email").description("회원가입시 입력한 이메일")
            )));
    }

    @Test
    @DisplayName("이메일 인증 - 24시간 내에 인증을 완료하지 않으면 인증 토큰이 만료되어 이메일 인증에 실패한다.")
    void emailTokenCertification_failure() throws Exception {
        String token = UUID.randomUUID().toString();
        String email = "jungkh405@naver.com";

        doThrow(new TokenExpiredException("인증 토큰이 만료되었습니다.")).when(userService)
            .updateEmailVerified(token, email);

        mockMvc.perform(get("/users/email-check-token")
            .param("token", token)
            .param("email", email))
            .andDo(print())
            .andExpect(status().isUnauthorized())
            .andDo(document("users/emailAuth/failure", requestParameters(
                parameterWithName("token").description("회원가입시 발송되는 랜덤 토큰"),
                parameterWithName("email").description("회원가입시 입력한 이메일")
            )));
    }

    @Test
    @DisplayName("이메일 인증 토큰을 재전송한다.")
    void resendEmailToken() throws Exception {

        String email = "test@test123.com";
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(USER_ID, email);

        doNothing().when(emailCertificationService).sendEmailForEmailCheck(email);

        mockMvc.perform(post("/users/resend-email-token")
            .session(session))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("users/emailAuth/resend"));
    }

    @Test
    @DisplayName("사용자가 입력한 번호로 인증 문자를 전송한다.")
    void sendSMS() throws Exception {
        SmsCertificationRequest requestDto = SmsCertificationRequest.builder()
            .phone("01012345678")
            .certificationNumber(null)
            .build();

        String phone = requestDto.getPhone();

        doNothing().when(smsCertificationService).sendSms(phone);

        mockMvc.perform(post("/users/sms-certification/sends")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(document("users/certification/sms", requestFields(
                fieldWithPath("phone").type(JsonFieldType.STRING).description("인증번호를 받을 휴대폰 번호"),
                fieldWithPath("certificationNumber").type(null)
                    .description("null : 인증 번호 발송시 사용하지 않는 값")
            )));
    }

    @Test
    @DisplayName("휴대폰 인증 - 인증번호가 일치하면 휴대폰 인증에 성공한다.")
    void smsCertification_successful() throws Exception {
        SmsCertificationRequest requestDto = SmsCertificationRequest.builder()
            .phone("01012345678")
            .certificationNumber("123456")
            .build();

        doNothing().when(smsCertificationService).verifySms(requestDto);

        mockMvc.perform(post("/users/sms-certification/confirms")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("users/certification/sms/successful", requestFields(
                fieldWithPath("phone").type(JsonFieldType.STRING).description("인증번호를 받은 휴대폰 번호"),
                fieldWithPath("certificationNumber").type(JsonFieldType.STRING)
                    .description("사용자가 입력한 인증번호")
            )));
    }

    @Test
    @DisplayName("로그인 - 등록된 ID와 일치하는 PW 입력시 로그인에 성공한다.")
    void login_successful() throws Exception {
        LoginRequest requestDto = LoginRequest.builder()
            .email("test@test.com")
            .password("test1234")
            .build();

        doNothing().when(sessionLoginService).login(requestDto);

        mockMvc.perform(post("/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("users/login/successful", requestFields(
                fieldWithPath("email").type(JsonFieldType.STRING).description("login ID (email)"),
                fieldWithPath("password").type(JsonFieldType.STRING).description("password")
            )));
    }

    @Test
    @DisplayName("로그인 - 존재하지 않는 id 또는 비밀번호 불일치시 로그인에 실패한다.")
    void login_failure() throws Exception {
        LoginRequest requestDto = LoginRequest.builder()
            .email("test@test.com")
            .password("test1234")
            .build();

        doThrow(new UserNotFoundException("아이디 또는 비밀번호가 일치하지 않습니다.")).when(sessionLoginService)
            .login(any());

        mockMvc.perform(post("/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andDo(document("users/login/failure", requestFields(
                fieldWithPath("email").type(JsonFieldType.STRING).description("login ID (email)"),
                fieldWithPath("password").type(JsonFieldType.STRING).description("password")
            )));
    }

    @Test
    @DisplayName("회원 정보 - 마이페이지(회원 정보)를 리턴한다.")
    void myPage() throws Exception {
        List<AddressBook> addressBooks = new ArrayList<>();
        Address address = new Address
            ("우리집", "행복로19", "700동 100호", "12345");
        AddressBook addressBook = new AddressBook(address);
        addressBooks.add(addressBook);

        UserInfoDto userInfoDto = UserInfoDto.builder()
            .email("jungkh405@naver.com")
            .nickname("17171771")
            .phone("01012345678")
            .account(new Account("카카오뱅크", "123456789", "정기혁"))
            .addressBooks(addressBooks)
            .emailVerified(true).build();






    }


    @Test
    @DisplayName("이메일 인증(비밀번호 찾기 전용) - 비밀번호 찾기에서 이메일 인증을 선택하면 이메일로 인증번호가 발송된다.")
    void sendEMail() throws Exception {

    }
}