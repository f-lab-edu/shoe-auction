package com.flab.shoeauction.controller;

import static com.flab.shoeauction.common.utils.constants.ResponseConstants.CREATED;
import static com.flab.shoeauction.common.utils.constants.ResponseConstants.OK;

import com.flab.shoeauction.common.annotation.CurrentUser;
import com.flab.shoeauction.common.annotation.LoginCheck;
import com.flab.shoeauction.controller.dto.AddressDto;
import com.flab.shoeauction.controller.dto.AddressDto.SaveRequest;
import com.flab.shoeauction.controller.dto.UserDto.ChangePasswordRequest;
import com.flab.shoeauction.controller.dto.UserDto.EmailCertificationRequest;
import com.flab.shoeauction.controller.dto.UserDto.FindUserResponse;
import com.flab.shoeauction.controller.dto.UserDto.LoginRequest;
import com.flab.shoeauction.controller.dto.UserDto.PasswordRequest;
import com.flab.shoeauction.controller.dto.UserDto.SmsCertificationRequest;
import com.flab.shoeauction.controller.dto.UserDto.UserInfoDto;
import com.flab.shoeauction.domain.addressBook.Address;
import com.flab.shoeauction.domain.users.common.Account;
import com.flab.shoeauction.service.SessionLoginService;
import com.flab.shoeauction.service.UserService;
import com.flab.shoeauction.service.certification.EmailCertificationService;
import com.flab.shoeauction.service.certification.SmsCertificationService;
import com.flab.shoeauction.service.message.FCMService;
import java.util.List;
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

    private final SessionLoginService sessionLoginService;

    private final UserService userService;

    private final SmsCertificationService smsCertificationService;

    private final EmailCertificationService emailCertificationService;

    private final FCMService fcmService;

    @GetMapping("/user-emails/{email}/exists")
    public ResponseEntity<Boolean> checkEmailDuplicate(@PathVariable String email) {
        return ResponseEntity.ok(userService.checkEmailDuplicate(email));
    }

    @GetMapping("/user-nicknames/{nickname}/exists")
    public ResponseEntity<Boolean> checkNicknameDuplicate(@PathVariable String nickname) {
        return ResponseEntity.ok(userService.checkNicknameDuplicate(nickname));
    }

    @PostMapping
    public ResponseEntity<Void> createUser(
        @Valid @RequestBody com.flab.shoeauction.controller.dto.UserDto.SaveRequest requestDto) {
        userService.save(requestDto);
        emailCertificationService.sendEmailForEmailCheck(requestDto.getEmail());
        return CREATED;
    }

    @PostMapping("/sms-certification/sends")
    public ResponseEntity<Void> sendSms(@RequestBody SmsCertificationRequest requestDto) {
        smsCertificationService.sendSms(requestDto.getPhone());
        return CREATED;
    }

    @GetMapping("/email-check-token")
    public void emailCheck(String token, String email) {
        userService.updateEmailVerified(token, email);
    }

    @PostMapping("/resend-email-token")
    public void resendEmailCheck(@CurrentUser String email) {
        emailCertificationService.sendEmailForEmailCheck(email);
    }

    @PostMapping("/sms-certification/confirms")
    public ResponseEntity<Void> SmsVerification(@RequestBody SmsCertificationRequest requestDto) {
        smsCertificationService.verifySms(requestDto);
        return OK;
    }

    @PostMapping("/login")
    public void login(@RequestBody LoginRequest loginRequest) {
        sessionLoginService.login(loginRequest);
        fcmService.saveToken(loginRequest);
    }

    @LoginCheck
    @DeleteMapping("/logout")
    public void logout(@CurrentUser String email) {
        sessionLoginService.logout();
        fcmService.deleteToken(email);
    }

    @GetMapping("/my-infos")
    public ResponseEntity<UserInfoDto> myPage(@CurrentUser String email) {
        UserInfoDto loginUser = sessionLoginService.getCurrentUser(email);
        return ResponseEntity.ok(loginUser);
    }

    /**
     * 비밀번호 찾기 : 이메일 입력시, 존재하는 이메일이면 휴대폰인증과 이메일인증 중 택1 하도록 구현 휴대폰 인증 선택시 : sendSms / SmsVerification
     * 핸들러 이메일 인증 선택시 : sendEmail / emailVerification 핸들러
     */

    @GetMapping("/find/{email}")
    public ResponseEntity<FindUserResponse> findUser(@PathVariable String email) {
        FindUserResponse findUserResponse = userService.getUserResource(email);
        return ResponseEntity.ok(findUserResponse);
    }

    @PostMapping("/email-certification/sends")
    public ResponseEntity sendEmail(@RequestBody EmailCertificationRequest requestDto) {
        emailCertificationService.sendEmailForCertification(requestDto.getEmail());
        return CREATED;
    }

    @PostMapping("/email-certification/confirms")
    public void emailVerification(@RequestBody EmailCertificationRequest requestDto) {
        emailCertificationService.verifyEmail(requestDto);
    }

    @PatchMapping("/forget/password")
    public void changePasswordByForget(
        @Valid @RequestBody ChangePasswordRequest requestDto) {
        userService.updatePasswordByForget(requestDto);
    }

    @LoginCheck
    @DeleteMapping
    public ResponseEntity<Void> UserWithdrawal(@RequestBody PasswordRequest requestDto,
        @CurrentUser String email) {
        String password = requestDto.getPassword();
        userService.delete(email, password);
        sessionLoginService.logout();
        return OK;
    }

    @LoginCheck
    @PatchMapping("/password")
    public void changePassword(@CurrentUser String email,
        @Valid @RequestBody ChangePasswordRequest requestDto) {
        userService.updatePassword(email, requestDto);
    }

    @LoginCheck
    @PatchMapping("/account")
    public void changeAccount(@CurrentUser String email, @RequestBody
        Account account) {
        userService.updateAccount(email, account);
    }

    @LoginCheck
    @GetMapping("/account")
    public ResponseEntity<Account> getAccountResource(@CurrentUser String email) {
        Account account = userService.getAccount(email);
        return ResponseEntity.ok(account);
    }

    @LoginCheck
    @PostMapping("/addressBook")
    public void addAddressBook(@CurrentUser String email, @RequestBody SaveRequest saveRequest) {
        userService.addAddress(email, saveRequest);
    }

    @LoginCheck
    @GetMapping("/addressBook")
    public ResponseEntity<List<Address>> getAddressBookResource(@CurrentUser String email) {
        List<Address> addressList = userService.getAddressBook(email);
        return ResponseEntity.ok(addressList);
    }

    @LoginCheck
    @DeleteMapping("/addressBook")
    public void deleteAddressBook(@CurrentUser String email,
        @RequestBody AddressDto.IdRequest idRequest) {
        userService.deleteAddress(email, idRequest);
    }

    @LoginCheck
    @PatchMapping("/addressBook")
    public void updateAddressBook(@RequestBody SaveRequest requestDto) {
        userService.updateAddress(requestDto);
    }

    @LoginCheck
    @PatchMapping("/nickname")
    public void updateNickname(@CurrentUser String email,
        @RequestBody com.flab.shoeauction.controller.dto.UserDto.SaveRequest requestDto) {
        userService.updateNickname(email, requestDto);
    }
}
