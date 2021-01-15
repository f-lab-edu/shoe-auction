package com.flab.shoeauction.web;

import com.flab.shoeauction.util.response.ResponseConstants;
import com.flab.shoeauction.service.SmsCertificationService;
import com.flab.shoeauction.service.UserService;
import com.flab.shoeauction.web.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class UserApiController {

    private final UserService userService;

    private final SmsCertificationService smsCertificationService;

    @GetMapping("/duplication/email")
    public ResponseEntity<Boolean> checkEmailDuplicate(@RequestParam String email) {
        return ResponseEntity.ok(userService.checkEmailDuplicate(email));
    }

    @GetMapping("/duplication/nickname")
    public ResponseEntity<Boolean> checkNicknameDuplicate(@RequestParam String nickname) {
        return ResponseEntity.ok(userService.checkNicknameDuplicate(nickname));
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserDto.SaveRequest requestDto) {
        userService.save(requestDto);
        return ResponseConstants.CREATED_RESPONSE;
    }

    @PostMapping("/certification/sms")
    public ResponseEntity<Void> sendSms(@RequestBody Map<String, String> map) {
        smsCertificationService.sendSms(map.get("phone"));
        return ResponseConstants.CREATED_RESPONSE;
    }

    @PostMapping("/certification/verification")
    public ResponseEntity<Void> phoneVerification(@RequestBody UserDto.CertificationRequest requestDto) {
        if (!smsCertificationService.phoneVerification(requestDto))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return ResponseConstants.OK_RESPONSE;
    }
}