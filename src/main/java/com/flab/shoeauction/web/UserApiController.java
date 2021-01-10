package com.flab.shoeauction.web;

import com.flab.shoeauction.service.users.UsersService;
import com.flab.shoeauction.web.dto.users.UsersSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class UserApiController {

    private final UsersService usersService;

    // 이메일 중복 체크
    @GetMapping("/check/email")
    public boolean checkEmail(@RequestParam String email) {
        return usersService.checkEmailUnique(email);
    }

    // 닉네임 중복 체크
    @GetMapping("/check/nickname")
    public boolean checkNicknameUnique(@RequestParam String nickname) {
        return usersService.checkNicknameUnique(nickname);
    }

    // 유저 등록
    @PostMapping("/new")
    public ResponseEntity<Void> createUser(@Valid @RequestBody UsersSaveRequestDto requestDto) {
        usersService.save(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}