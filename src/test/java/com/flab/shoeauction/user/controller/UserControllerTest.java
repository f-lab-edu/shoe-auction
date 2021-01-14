package com.flab.shoeauction.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.shoeauction.user.dto.UserDto;
import com.flab.shoeauction.user.dto.UserDto.LoginDto;
import com.flab.shoeauction.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    private UserDto userDto;

    @BeforeEach
    public void setUser(){
        userDto = UserDto.builder()
                .email("test1@test.com")
                .password("test1234")
                .phone("01011112222")
                .nickname("17171771")
                .build();
    }

    @Test
    @DisplayName("회원 가입 성공")
    public void signUpSuccess() throws Exception {


        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/users/1"));

    }

    @Test
    @DisplayName("회원가입 실패 - 정보 입력 오류")
    public void signUpFailedByInformationEntryError() throws Exception {
        UserDto userDto = UserDto.builder()
                .email("test2@test.com")
                .password("t")
                .phone("01011112222")
                .nickname("a")
                .build();

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
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
    @DisplayName("로그인 성공")
    public void loginSuccessful() throws Exception {

        LoginDto loginDto = LoginDto.of("test1@test.com", "test1234");
        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인 실패 - id/pw 불일치")
    public void loginFailed() throws Exception {

        LoginDto loginDto = LoginDto.of("testCase@test.com", "test1234");
        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}