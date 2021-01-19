package com.flab.shoeauction.web;

import com.flab.shoeauction.service.SmsCertificationService;
import com.flab.shoeauction.service.UserService;
import com.flab.shoeauction.util.response.ResponseConstants;
import com.flab.shoeauction.web.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class UserApiController {

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
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserDto.SaveRequest requestDto) {
        userService.save(requestDto);
        return ResponseConstants.CREATED;
    }

    @PostMapping("/sms-certification/sends")
    public ResponseEntity<Void> sendSms(@RequestBody UserDto.SmsCertificationRequest requestDto) {
        smsCertificationService.sendSms(requestDto.getPhone());
        return ResponseConstants.CREATED;
    }

    @PostMapping("/sms-certification/confirms")
    public ResponseEntity<Void> SmsVerification(@RequestBody UserDto.SmsCertificationRequest requestDto) {
        if (!smsCertificationService.verifySms(requestDto.getCertificationNumber()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return ResponseConstants.OK;
    }
}