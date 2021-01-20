package com.flab.shoeauction.user.controller;

import static com.flab.shoeauction.common.utils.httpStatus.ResponseConstants.RESPONSE_OK;

import com.flab.shoeauction.common.annotation.LoginCheck;
import com.flab.shoeauction.common.utils.encrytion.EncryptionUtils;
import com.flab.shoeauction.user.domain.User;
import com.flab.shoeauction.user.dto.UserDto;
import com.flab.shoeauction.user.dto.UserDto.LoginDto;
import com.flab.shoeauction.user.service.LoginService;
import com.flab.shoeauction.user.service.SignUpService;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 휴대폰 인증시 전송받은 인증번호를 session에 저장하여 User가 입력한 인증번호화 일치하는지 확인 인증번호 일치 여부에 따라 200 또는 400 반환 회원가입 완료 후
 * 마이페이지로 이동할 수 있는 URI를 HEADER의 Location에 제공하기 위해 HATEOAS 사용
 */

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final SignUpService signUpService;
    private final LoginService loginService;
    private final EncryptionUtils encryptionUtils;

    //테스트시 조회용 - 추후 삭제
    @GetMapping
    public List<User> allUsers() {
        return signUpService.findAll();
    }

    @GetMapping("/user-emails/{email}/exist")
    public boolean emailDuplicated(@PathVariable String email) {
        return signUpService.emailDuplicateCheck(email);
    }

    @GetMapping("/user-nicknames/{nickname}/exist")
    public boolean nickname(@PathVariable String nickname) {
        return signUpService.nicknameDuplicateCheck(nickname);
    }


    @PostMapping
    public ResponseEntity signUp(@RequestBody @Valid UserDto signUpDto) {
        User savedUser = signUpService.saveUser(signUpDto);
        URI uri = WebMvcLinkBuilder.linkTo(UserController.class).slash(savedUser.getId()).toUri();

        return ResponseEntity.created(uri).build();
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginDto loginDto) {
        String email = loginDto.getEmail();
        String password = encryptionUtils.encrypt(loginDto.getPassword());
        loginService.existByEmailAndPassword(email, password);
        loginService.login(email);
        return RESPONSE_OK;
    }

    @LoginCheck
    @DeleteMapping("/logout")
    public ResponseEntity logout() {
        loginService.logout();
        return RESPONSE_OK;
    }
}
