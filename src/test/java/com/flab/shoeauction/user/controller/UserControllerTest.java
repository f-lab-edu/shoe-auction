package com.flab.shoeauction.user.controller;

import static com.flab.shoeauction.user.utils.UserConstants.USER_ID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.shoeauction.user.dto.UserDto;
import com.flab.shoeauction.user.dto.UserDto.LoginDto;
import com.flab.shoeauction.user.dto.UserDto.UserInfoDto;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


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
        UserDto userDto = setUser();
        LoginDto loginDto = LoginDto.of("test1@test.com", "test1234");

        mockMvc.perform(post("/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDto)))
            .andDo(print())
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
    @DisplayName("마이페이지 요청 실패 - 로그인하지 않은 상태로 유저 정보 요청하면 실패")
    public void requestUserInfo_failure() throws Exception {
        mockMvc.perform(get("/users/my-info"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }
}