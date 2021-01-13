package com.flab.shoeauction.web;

import com.flab.shoeauction.service.users.UsersService;
import com.flab.shoeauction.web.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class UserApiController {

    private final UsersService usersService;

    @GetMapping("/duplication/email")
    public ResponseEntity<Boolean> checkEmailDuplicate(@RequestParam String email) {
        return ResponseEntity.ok(usersService.checkEmailDuplicate(email));
    }

    @GetMapping("/duplication/nickname")
    public ResponseEntity<Boolean> checkNicknameDuplicate(@RequestParam String nickname) {
        return ResponseEntity.ok(usersService.checkNicknameDuplicate(nickname));
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserDto.SaveRequest requestDto) {
        usersService.save(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/certification/sms")
    public ResponseEntity<Void> sendSms(@RequestParam String phone, HttpSession session) {
        usersService.sendSms(phone, session);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/certification/verification")
    public ResponseEntity<Void> phoneVerification(@RequestParam String phone, String certificationNumber, HttpSession session) {
        if (!usersService.phoneVerification(phone, certificationNumber, session))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}