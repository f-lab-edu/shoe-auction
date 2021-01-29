package com.flab.shoeauction.web;

import com.flab.shoeauction.annotation.CheckLogin;
import com.flab.shoeauction.service.LoginService;
import com.flab.shoeauction.service.SessionService;
import com.flab.shoeauction.service.SmsCertificationService;
import com.flab.shoeauction.service.UserService;
import com.flab.shoeauction.util.response.ResponseConstants;
import com.flab.shoeauction.web.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.flab.shoeauction.util.response.ResponseConstants.CREATED;
import static com.flab.shoeauction.util.response.ResponseConstants.OK;

@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class UserApiController {

    private final UserService userService;

    private final LoginService loginService;

    private final SessionService sessionService;

    private final SmsCertificationService smsCertificationService;

    @GetMapping("/user-emails/{email}/exists")
    public ResponseEntity<Boolean> checkEmailDuplicate(@PathVariable String email) {
        return ResponseEntity.ok(userService.checkEmailDuplicate(email));
    }

    @GetMapping("/user-nicknames/{nickname}/exists")
    public ResponseEntity<Boolean> checkNicknameDuplicate(@PathVariable String nickname) {
        return ResponseEntity.ok(userService.checkNicknameDuplicate(nickname));
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserDto.SaveRequest requestDto) {
        userService.save(requestDto);
        return OK;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody UserDto.LoginRequest requestDto) {
        loginService.checkLoginInfo(requestDto);
        sessionService.saveLoginUserEmail(requestDto.getEmail());
        return CREATED;
    }

    @CheckLogin
    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout() {
        sessionService.removeLoginUserEmail();
        return OK;
    }

    @CheckLogin
    @GetMapping("/my-infos")
    public ResponseEntity<UserDto.InfoResponse> myInfo() {
        String email = sessionService.getLoginUserEmail();
        UserDto.InfoResponse responseDto = loginService.getMyInfo(email);
        return ResponseEntity.ok().body(responseDto);
    }

    @PostMapping("/sms-certification/confirms")
    public ResponseEntity<Void> SmsVerification(@RequestBody UserDto.SmsCertificationRequest requestDto) {
        if (!smsCertificationService.verifySms(requestDto))
            return ResponseConstants.BAD_REQUEST;
        return ResponseConstants.OK;
    }
}