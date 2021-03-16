package com.flab.shoeauction.controller;

import static com.flab.shoeauction.common.utils.constants.UserConstants.USER_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.shoeauction.controller.dto.AddressBookDto;
import com.flab.shoeauction.controller.dto.ProductDto.IdRequest;
import com.flab.shoeauction.controller.dto.ProductDto.WishItemResponse;
import com.flab.shoeauction.controller.dto.UserDto.ChangePasswordRequest;
import com.flab.shoeauction.controller.dto.UserDto.EmailCertificationRequest;
import com.flab.shoeauction.controller.dto.UserDto.FindUserResponse;
import com.flab.shoeauction.controller.dto.UserDto.LoginRequest;
import com.flab.shoeauction.controller.dto.UserDto.PasswordRequest;
import com.flab.shoeauction.controller.dto.UserDto.SaveRequest;
import com.flab.shoeauction.controller.dto.UserDto.SmsCertificationRequest;
import com.flab.shoeauction.controller.dto.UserDto.UserInfoDto;
import com.flab.shoeauction.domain.addressBook.Address;
import com.flab.shoeauction.domain.addressBook.AddressBook;
import com.flab.shoeauction.domain.brand.Brand;
import com.flab.shoeauction.domain.users.common.Account;
import com.flab.shoeauction.domain.users.common.UserLevel;
import com.flab.shoeauction.exception.user.AuthenticationNumberMismatchException;
import com.flab.shoeauction.exception.user.DuplicateEmailException;
import com.flab.shoeauction.exception.user.TokenExpiredException;
import com.flab.shoeauction.exception.user.UnableToChangeNicknameException;
import com.flab.shoeauction.exception.user.UserNotFoundException;
import com.flab.shoeauction.exception.user.WrongPasswordException;
import com.flab.shoeauction.service.SessionLoginService;
import com.flab.shoeauction.service.UserService;
import com.flab.shoeauction.service.certification.EmailCertificationService;
import com.flab.shoeauction.service.certification.SmsCertificationService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
            .andDo(document("users/certification/sms/send", requestFields(
                fieldWithPath("phone").type(JsonFieldType.STRING).description("인증번호를 받을 휴대폰 번호"),
                fieldWithPath("certificationNumber").ignored()
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
    @DisplayName("휴대폰 인증 - 인증번호가 일치 하지 않으면 휴대폰 인증에 실패한다.")
    void smsCertification_failure() throws Exception {
        SmsCertificationRequest requestDto = SmsCertificationRequest.builder()
            .phone("01012345678")
            .certificationNumber("123456")
            .build();

        doThrow(new AuthenticationNumberMismatchException("인증번호가 일치하지 않습니다."))
            .when(smsCertificationService).verifySms(any());

        mockMvc.perform(post("/users/sms-certification/confirms")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andDo(document("users/certification/sms/failure", requestFields(
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
                fieldWithPath("email").type(JsonFieldType.STRING)
                    .description("login ID (email)"),
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
            .andExpect(status().isNotFound())
            .andDo(document("users/login/failure", requestFields(
                fieldWithPath("email").type(JsonFieldType.STRING)
                    .description("login ID (email)"),
                fieldWithPath("password").type(JsonFieldType.STRING).description("password")
            )));
    }

    @Test
    @DisplayName("로그아웃 - 로그아웃에 성공한다.")
    void logout() throws Exception {
        doNothing().when(sessionLoginService).logout();

        mockMvc.perform(delete("/users/logout"))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("users/logout"));

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
            .userLevel(UserLevel.UNAUTH)
            .build();

        String email = "jungkh405@naver.com";

        given(sessionLoginService.getCurrentUser(any())).willReturn(userInfoDto);

        mockMvc.perform(get("/users/my-infos")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("users/my-infos",
                responseFields(
                    fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일"),
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                    fieldWithPath("phone").type(JsonFieldType.STRING).description("회원 휴대폰 번호"),
                    subsectionWithPath("account").type(JsonFieldType.OBJECT)
                        .description("회원 환급 계좌정보"),
                    subsectionWithPath("addressBooks").type(JsonFieldType.ARRAY)
                        .description("회원 주소록"),
                    fieldWithPath("userLevel").type(JsonFieldType.STRING)
                        .description("이메일 인증 여부")
                )));
    }

    @Test
    @DisplayName("비밀번호 찾기 - 존재하는 email 입력시 비밀번호를 찾기 위한 리소스(email,phone)을 리턴한다.")
    void getUerResource_successful() throws Exception {
        FindUserResponse responseDto = FindUserResponse.builder()
            .email("jungkh405@naver.com")
            .phone("01012345678")
            .build();

        String email = "jungkh405@naver.com";

        given(userService.getUserResource(any())).willReturn(responseDto);

        mockMvc.perform(get("/users/find/{email}", email))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("users/forgetPassword/resource/successful", responseFields(
                fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일"),
                fieldWithPath("phone").type(JsonFieldType.STRING).description("회원 휴대폰 번호")
                ),
                pathParameters(
                    parameterWithName("email").description("이메일")
                )
            ));
    }

    @Test
    @DisplayName("비밀번호 찾기 - 존재하지 않는 email 입력시 비밀번호를 찾기 위한 리소스(email,phone) 리턴에 실패한다.")
    void getUserResource_failure() throws Exception {
        String email = "jungkh405@naver.com";

        doThrow(new UserNotFoundException("존재하지 않는 email 입니다.")).when(userService)
            .getUserResource(any());
        mockMvc.perform(get("/users/find/{email}", email))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andDo(document("users/forgetPassword/resource/failure", pathParameters(
                parameterWithName("email").description("이메일")
            )));
    }

    @Test
    @DisplayName("비밀번호 찾기 - 비밀번호 찾기에서 email 인증을 선택하면 email로 인증번호가 발송된다.")
    void sendEmailCertification() throws Exception {
        EmailCertificationRequest requestDto = EmailCertificationRequest.builder()
            .email("jungkh405@naver.com")
            .certificationNumber(null)
            .build();

        String email = requestDto.getEmail();

        doNothing().when(emailCertificationService).sendEmailForCertification(email);

        mockMvc.perform(post("/users/email-certification/sends")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(document("users/certification/email/send", requestFields(
                fieldWithPath("email").type(JsonFieldType.STRING).description("인증번호를 받을 이메일"),
                fieldWithPath("certificationNumber").ignored()
            )));
    }

    @Test
    @DisplayName("비밀번호 찾기 - 인증번호가 일치하면 이메일 인증에 성공한다.")
    void emailCertification_successful() throws Exception {
        EmailCertificationRequest requestDto = EmailCertificationRequest.builder()
            .email("jungkh405@naver.com")
            .certificationNumber("123456")
            .build();

        doNothing().when(emailCertificationService).verifyEmail(requestDto);

        mockMvc.perform(post("/users/email-certification/confirms")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("users/certification/email/successful", requestFields(
                fieldWithPath("email").type(JsonFieldType.STRING)
                    .description("비밀번호 찾기를 원하는 이메일"),
                fieldWithPath("certificationNumber").type(JsonFieldType.STRING)
                    .description("사용자가 입력한 인증번호")
            )));
    }

    @Test
    @DisplayName("비밀번호 찾기 - 인증번호가 일치하지 않으면 이메일 인증에 실패한다.")
    void emailCertification_failure() throws Exception {
        EmailCertificationRequest requestDto = EmailCertificationRequest.builder()
            .email("jungkh405@naver.com")
            .certificationNumber("123456")
            .build();

        doThrow(new AuthenticationNumberMismatchException("인증번호가 일치하지 않습니다."))
            .when(emailCertificationService).verifyEmail(any());

        mockMvc.perform(post("/users/email-certification/confirms")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andDo(document("users/certification/email/failure", requestFields(
                fieldWithPath("email").type(JsonFieldType.STRING)
                    .description("비밀번호 찾기를 원하는 이메일"),
                fieldWithPath("certificationNumber").type(JsonFieldType.STRING)
                    .description("사용자가 입력한 인증번호")
            )));

    }

    @Test
    @DisplayName("비밀번호 찾기 - 인증이 완료되면 비밀번호를 변경한다.")
    void changePasswordByForget() throws Exception {
        ChangePasswordRequest requestDto = ChangePasswordRequest.builder()
            .email("jungkh405@naver.com")
            .passwordAfter("test12345")
            .passwordBefore(null)
            .build();

        doNothing().when(userService).updatePasswordByForget(requestDto);

        mockMvc.perform(patch("/users/forget/password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("users/forgetPassword/updatePassword", requestFields(
                fieldWithPath("email").type(JsonFieldType.STRING)
                    .description("비밀번호를 변경할 회원 ID(email)"),
                fieldWithPath("passwordAfter").type(JsonFieldType.STRING).description("변경할 비밀번호"),
                fieldWithPath("passwordBefore").ignored()
            )))
        ;
    }

    @Test
    @DisplayName("회원 탈퇴 - 비밀번호가 일치하면 회원 탈퇴가 성공한다.")
    void UserWithdrawal_successful() throws Exception {
        PasswordRequest requestDto = PasswordRequest.builder()
            .password("test12345").build();
        String email = "jungkh405@naver.com";

        String password = requestDto.getPassword();

        doNothing().when(userService).delete(email, password);
        doNothing().when(sessionLoginService).logout();

        mockMvc.perform(delete("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("users/withdrawal/successful", requestFields(
                fieldWithPath("password").type(JsonFieldType.STRING).description("회원 비밀번호")
            )));
    }

    @Test
    @DisplayName("회원 탈퇴 - 비밀번호가 일치 하지 않으면 회원 탈퇴에 실패한다.")
    void UserWithdrawal_failure() throws Exception {
        PasswordRequest requestDto = PasswordRequest.builder()
            .password("test12345").build();

        doThrow(new WrongPasswordException()).when(userService).delete(any(), any());
        doNothing().when(sessionLoginService).logout();

        mockMvc.perform(delete("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andDo(print())
            .andExpect(status().isUnauthorized())
            .andDo(document("users/withdrawal/failure", requestFields(
                fieldWithPath("password").type(JsonFieldType.STRING).description("회원 비밀번호")
            )));
    }

    @Test
    @DisplayName("비밀번호 변경 - 이전 비밀번호가 일치하면 비밀번호 변경에 성공한다.")
    void changePassword_successful() throws Exception {
        ChangePasswordRequest requestDto = ChangePasswordRequest.builder()
            .email("jungkh405@naver.com")
            .passwordAfter("test12345")
            .passwordBefore("newPassword1234")
            .build();

        String currentUer = "jungkh405@naver.com";

        doNothing().when(userService).updatePassword(currentUer, requestDto);

        mockMvc.perform(patch("/users/password")
            .content(objectMapper.writeValueAsString(requestDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("users/changeUserInfo/password/successful", requestFields(
                fieldWithPath("email").type(JsonFieldType.STRING)
                    .description("비밀번호 변경을 원하는 회원 ID(email"),
                fieldWithPath("passwordAfter").type(JsonFieldType.STRING).description("변경할 비밀번호"),
                fieldWithPath("passwordBefore").type(JsonFieldType.STRING).description("이전 비밀번호")
            )));
    }

    @Test
    @DisplayName("비밀번호 변경 - 이전 비밀번호가 일치하지 않으면 비밀번호 변경에 실패한다.")
    void changePassword_failure() throws Exception {
        ChangePasswordRequest requestDto = ChangePasswordRequest.builder()
            .email("jungkh405@naver.com")
            .passwordAfter("test12345")
            .passwordBefore("newPassword1234")
            .build();

        doThrow(new WrongPasswordException()).when(userService)
            .updatePassword(any(), any());

        mockMvc.perform(patch("/users/password")
            .content(objectMapper.writeValueAsString(requestDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isUnauthorized())
            .andDo(document("users/changeUserInfo/password/failure", requestFields(
                fieldWithPath("email").type(JsonFieldType.STRING)
                    .description("비밀번호 변경을 원하는 회원 ID(email"),
                fieldWithPath("passwordAfter").type(JsonFieldType.STRING).description("변경할 비밀번호"),
                fieldWithPath("passwordBefore").type(JsonFieldType.STRING).description("이전 비밀번호")
            )));
    }

    @Test
    @DisplayName("환급 계좌 - 환급 계좌를 설정/변경한다.")
    void changeAccount() throws Exception {
        Account account = new Account("농협", "35212345678", "정기혁");
        String currentUser = "jungkh405@naver.com";

        doNothing().when(userService).updateAccount(currentUser, account);

        mockMvc.perform(patch("/users/account")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(account)))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("users/changeUserInfo/account/change", requestFields(
                fieldWithPath("bankName").type(JsonFieldType.STRING).description("은행명"),
                fieldWithPath("accountNumber").type(JsonFieldType.STRING).description("계좌 번호"),
                fieldWithPath("depositor").type(JsonFieldType.STRING).description("예금주")
            )));
    }

    @Test
    @DisplayName("환급 계좌 - USER의 환급 계좌 정보를 리턴한다.")
    void getAccountResource() throws Exception {
        Account account = new Account("농협", "35212345678", "정기혁");

        given(userService.getAccount(any())).willReturn(account);

        mockMvc.perform(get("/users/account")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("users/changeUserInfo/account/Resource",
                responseFields(
                    fieldWithPath("bankName").type(JsonFieldType.STRING).description("은행명"),
                    fieldWithPath("accountNumber").type(JsonFieldType.STRING).description("계좌 번호"),
                    fieldWithPath("depositor").type(JsonFieldType.STRING).description("예금주")
                )));
    }

    @Test
    @DisplayName("닉네임 변경 - 닉네임을 변경한지 7일이 초과되었고, 닉네임 중복 검사에 통과하면 닉네임 변경에 성공한다.")
    void changeNickname_successful() throws Exception {
        SaveRequest requestDto = SaveRequest.builder()
            .email(null)
            .password(null)
            .phone(null)
            .nickname("newNickname123")
            .build();

        String currentUser = "jungkh405@naver.com";

        doNothing().when(userService).updateNickname(currentUser, requestDto);

        mockMvc.perform(patch("/users/nickname")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("users/changeUserInfo/nickname/changeSuccessful", requestFields(
                fieldWithPath("email").ignored(),
                fieldWithPath("password").ignored(),
                fieldWithPath("phone").ignored(),
                fieldWithPath("nickname").type(JsonFieldType.STRING).description("새로운 닉네임")
            )));
    }

    @Test
    @DisplayName("닉네임 변경 - 닉네임을 변경한지 7일이 초과되지 않았다면 닉네임 변경에 실패한다.")
    void changeNickname_failure() throws Exception {
        SaveRequest requestDto = SaveRequest.builder()
            .email(null)
            .password(null)
            .phone(null)
            .nickname("newNickname123")
            .build();

        doThrow(new UnableToChangeNicknameException("닉네임은 7일에 한번만 변경이 가능합니다.")).when(userService)
            .updateNickname(any(), any());

        mockMvc.perform(patch("/users/nickname")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andDo(document("users/changeUserInfo/nickname/changeFailure", requestFields(
                fieldWithPath("email").ignored(),
                fieldWithPath("password").ignored(),
                fieldWithPath("phone").ignored(),
                fieldWithPath("nickname").type(JsonFieldType.STRING).description("새로운 닉네임")
            )));
    }

    @Test
    @DisplayName("주소록 - 주소록에 주소를 추가한다.")
    void addAddressBook() throws Exception {
        Address address = new Address
            ("우리집", "행복로19", "700동 100호", "12345");
        String currentUser = "jungkh405@naver.com";

        doNothing().when(userService).addAddressBook(currentUser, address);

        mockMvc.perform(post("/users/addressBook")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(address)))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("users/changeUserInfo/addressBook/add/successful", requestFields(
                fieldWithPath("addressName").type(JsonFieldType.STRING).description("주소록 이름"),
                fieldWithPath("roadNameAddress").type(JsonFieldType.STRING).description("도로명 주소"),
                fieldWithPath("detailedAddress").type(JsonFieldType.STRING).description("상세 주소"),
                fieldWithPath("postalCode").type(JsonFieldType.STRING).description("우편번호")
            )));
    }

    @Test
    @DisplayName("주소록 - 회원의 주소록 정보를 가져온다.")
    void getAddressBook() throws Exception {

        List<AddressBook> addressBooks = new ArrayList<>();
        Address address = new Address
            ("우리집", "행복로19", "700동 100호", "12345");
        AddressBook addressBook = new AddressBook(address);
        addressBooks.add(addressBook);

        given(userService.getAddressBooks(any())).willReturn(addressBooks);

        mockMvc.perform(get("/users/addressBook")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("users/changeUserInfo/addressBook/Resource"));
    }

    @Test
    @DisplayName("주소록 - 주소록을 삭제한다.")
    void deleteAddressBook() throws Exception {
        AddressBookDto addressBookDto = AddressBookDto.builder()
            .id(1L)
            .addressName(null)
            .roadNameAddress(null)
            .detailedAddress(null)
            .postalCode(null).build();

        doNothing().when(userService).deleteAddressBook(addressBookDto);

        mockMvc.perform(delete("/users/addressBook")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(addressBookDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("users/changeUserInfo/addressBook/delete", requestFields(
                fieldWithPath("id").type(JsonFieldType.NUMBER).description("ID"),
                fieldWithPath("addressName").ignored(),
                fieldWithPath("roadNameAddress").ignored(),
                fieldWithPath("detailedAddress").ignored(),
                fieldWithPath("postalCode").ignored()
            )));
    }

    @Test
    @DisplayName("주소록 - 주소록에 있는 주소 중 하나를 수정한다.")
    void updateAddressBook() throws Exception {
        AddressBookDto addressBookDto = AddressBookDto.builder()
            .id(1L)
            .addressName("우리집")
            .roadNameAddress("행복로123")
            .detailedAddress("123동123호")
            .postalCode("12345").build();

        doNothing().when(userService).updateAddressBook(addressBookDto);

        mockMvc.perform(patch("/users/addressBook")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(addressBookDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("users/changeUserInfo/addressBook/update", requestFields(
                fieldWithPath("id").type(JsonFieldType.NUMBER).description("ID"),
                fieldWithPath("addressName").type(JsonFieldType.STRING).description("주소록 이름"),
                fieldWithPath("roadNameAddress").type(JsonFieldType.STRING).description("도로명 주소"),
                fieldWithPath("detailedAddress").type(JsonFieldType.STRING).description("상세 주소"),
                fieldWithPath("postalCode").type(JsonFieldType.STRING).description("우편번호")
            )));
    }

    @Test
    @DisplayName("카트 - 위시리스트를 조회한다.")
    void getWishList() throws Exception {
        Set<WishItemResponse> wishList = createWishList();
        given(userService.getWishList(any())).willReturn(wishList);

        mockMvc.perform(get("/users/carts"))
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

        doNothing().when(userService).addWishList(email, idRequest);

        mockMvc.perform(post("/users/carts")
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

        doNothing().when(userService).deleteWishList(idRequest);

        mockMvc.perform(delete("/users/carts")
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