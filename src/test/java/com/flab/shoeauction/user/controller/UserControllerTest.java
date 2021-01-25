package com.flab.shoeauction.user.controller;

import static com.flab.shoeauction.user.utils.UserConstants.USER_ID;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.shoeauction.user.dto.UserDto;
import com.flab.shoeauction.user.dto.UserDto.LoginDto;
import com.flab.shoeauction.user.dto.UserDto.UserInfoDto;
import com.flab.shoeauction.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;


    public UserDto setUser() {
        UserDto userDto = UserDto.builder()
            .email("test1@test.com")
            .password("test1234")
            .phone("01011112222")
            .nickname("17171771")
            .build();
        return userDto;
    }

    @Test
    @DisplayName("회원 가입 성공 - 모든 유효성 검증에 통과한 경우 회원가입 성공")
    public void signUpSuccess() throws Exception {

        UserDto userDto = setUser();
        mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/users/1"));

    }

    @Test
    @DisplayName("회원가입 실패 - 회원 정보 유효성 검증 실패로 회원가입 실패")
    public void signUpFailedByInformationEntryError() throws Exception {
        UserDto userDto2 = UserDto.builder()
            .email("test2@test.com")
            .password("t")
            .phone("01011112222")
            .nickname("a")
            .build();

        mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto2)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 이메일 및 닉네임으로 가입 시도")
    public void signUpFailedByExistEmailOrNickname() throws Exception {
        UserDto existUserDto = UserDto.builder()
            .email("test1@test.com")
            .password("test1234")
            .phone("01011112222")
            .nickname("17171771")
            .build();

        mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(existUserDto)))
            .andDo(print())
            .andExpect(status().isConflict());

    }


    @Test
    @DisplayName("로그인 성공 - id/pw 가 일치하면 로그인 성공")
    public void loginSuccessful() throws Exception {
        LoginDto loginDto = LoginDto.of("test1@test.com", "test1234");

        mockMvc.perform(post("/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDto)))
            .andDo(print())
            .andExpect(request().sessionAttribute(USER_ID, notNullValue()))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 id 또는 pw 불일치로 로그인 실패")
    public void loginFailed() throws Exception {

        LoginDto loginDto = LoginDto.of("testCase@test.com", "test1234");
        mockMvc.perform(post("/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDto)))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그아웃 성공 - 로그인 한 상태에서 로그아웃 요청시 로그아웃에 성공")
    public void logoutSuccessful() throws Exception {
        MockHttpSession mockHttpSession = new MockHttpSession();
        UserDto.UserInfoDto userInfoDto = UserInfoDto
            .of("test1@test.com", "17171771", "01011112222", null, null);
        mockHttpSession.setAttribute(USER_ID, userInfoDto);
        mockMvc.perform(delete("/users/logout")
            .session(mockHttpSession))
            .andDo(print())
            .andExpect(request().sessionAttribute(USER_ID,nullValue()))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("마이페이지 요청 성공 - 로그인 한 상태에서 유저의 정보를 요청하면 성공")
    public void requestUserInfo_Successful() throws Exception {
        MockHttpSession mockHttpSession = new MockHttpSession();
        UserDto.UserInfoDto userInfoDto = UserInfoDto
            .of("test1@test.com", "17171771", "01011112222", null, null);
        mockHttpSession.setAttribute(USER_ID, userInfoDto);

        mockMvc.perform(get("/users/profile")
            .session(mockHttpSession)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userInfoDto)))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("마이페이지 요청 실패 - 로그인하지 않은 상태로 유저 정보 요청하면 실패")
    public void requestUserInfo_failure() throws Exception {
        mockMvc.perform(get("/users/profile"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }
}