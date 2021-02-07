package com.flab.shoeauction.controller;

import static com.flab.shoeauction.common.utils.response.ResponseConstants.CREATED;
import static com.flab.shoeauction.common.utils.response.ResponseConstants.OK;

import com.flab.shoeauction.common.annotation.CurrentUser;
import com.flab.shoeauction.common.annotation.LoginCheck;
import com.flab.shoeauction.controller.dto.UserDto.ChangePasswordRequest;
import com.flab.shoeauction.controller.dto.UserDto.EmailCertificationRequest;
import com.flab.shoeauction.controller.dto.UserDto.FindUserResponse;
import com.flab.shoeauction.controller.dto.UserDto.LoginRequest;
import com.flab.shoeauction.controller.dto.UserDto.SaveRequest;
import com.flab.shoeauction.controller.dto.UserDto.SmsCertificationRequest;
import com.flab.shoeauction.controller.dto.UserDto.UserInfoDto;
import com.flab.shoeauction.service.certification.EmailCertificationService;
import com.flab.shoeauction.service.LoginService;
import com.flab.shoeauction.service.certification.SmsCertificationService;
import com.flab.shoeauction.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    private final EmailCertificationService emailCertificationService;

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
        smsCertificationService.verifySms(requestDto);
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

    /**
     비밀번호 찾기 : 이메일 입력시, 존재하는 이메일이면 휴대폰인증과 이메일인증 중 택1 하도록 구현
     휴대폰 인증 선택시 : sendSms / SmsVerification 핸들러
     이메일 인증 선택시 : sendEmail / emailVerification 핸들러
     */
    @GetMapping("/find/{email}")
    public ResponseEntity<FindUserResponse> findUser(@PathVariable String email) {
        FindUserResponse findUserResponse = userService.getUserResource(email);
        return ResponseEntity.ok(findUserResponse);
    }

    @PostMapping("/email-certification/sends")
    public ResponseEntity sendEmail(@RequestBody EmailCertificationRequest requestDto) {
        emailCertificationService.sendEmail(requestDto.getEmail());
        return CREATED;
    }

    @PostMapping("/email-certification/confirms")
    public ResponseEntity emailVerification(@RequestBody EmailCertificationRequest requestDto) {
        emailCertificationService.verifyEmail(requestDto);
        return OK;

    }
    // TODO: 2021-02-03 : URI 네이밍 고민
    @PatchMapping("/password-nonLogin")
    public ResponseEntity changePassword_nonLogin(@Valid @RequestBody ChangePasswordRequest requestDto) {
        userService.updatePasswordByForget(requestDto);
        return OK;
    }

    @LoginCheck
    @PatchMapping("/password")
    public ResponseEntity changePassword(@CurrentUser String email, @Valid @RequestBody ChangePasswordRequest requestDto) {
        userService.updatePassword(email,requestDto);
        return OK;
    }



}