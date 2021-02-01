package com.flab.shoeauction.controller;

import static com.flab.shoeauction.common.util.response.ResponseConstants.BAD_REQUEST;
import static com.flab.shoeauction.common.util.response.ResponseConstants.CREATED;
import static com.flab.shoeauction.common.util.response.ResponseConstants.OK;

import com.flab.shoeauction.common.annotation.CurrentUser;
import com.flab.shoeauction.common.annotation.LoginCheck;
import com.flab.shoeauction.service.LoginService;
import com.flab.shoeauction.service.SmsCertificationService;
import com.flab.shoeauction.service.UserService;
import com.flab.shoeauction.controller.dto.UserDto.LoginRequest;
import com.flab.shoeauction.controller.dto.UserDto.SaveRequest;
import com.flab.shoeauction.controller.dto.UserDto.SmsCertificationRequest;
import com.flab.shoeauction.controller.dto.UserDto.UserInfoDto;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class UserApiController {

    private final LoginService loginService;

    private final UserService userService;

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
    public ResponseEntity<Void> createUser(@Valid @RequestBody SaveRequest requestDto) {
        userService.save(requestDto);
        return CREATED;
    }

    @PostMapping("/sms-certification/sends")
    public ResponseEntity<Void> sendSms(@RequestBody SmsCertificationRequest requestDto) {
        smsCertificationService.sendSms(requestDto.getPhone());
        return CREATED;
    }

    @PostMapping("/sms-certification/confirms")
    public ResponseEntity<Void> SmsVerification(@RequestBody SmsCertificationRequest requestDto) {
        if (!smsCertificationService.verifySms(requestDto)) {
            return BAD_REQUEST;
        }
        return OK;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest) {
        loginService.existByEmailAndPassword(loginRequest);
        loginService.login(loginRequest.getEmail());
        return OK;
    }

    @LoginCheck
    @DeleteMapping("/logout")
    public ResponseEntity logout() {
        loginService.logout();
        return OK;
    }

    @LoginCheck
    @GetMapping("/my-infos")
    public ResponseEntity<UserInfoDto> myPage(@CurrentUser String email) {
        UserInfoDto loginUser = loginService.getCurrentUser(email);
        return ResponseEntity.ok(loginUser);
    }


}