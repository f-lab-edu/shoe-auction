package com.flab.soft.shoeauction.user.controller;

import com.flab.soft.shoeauction.user.domain.User;
import com.flab.soft.shoeauction.user.dto.UserDto;
import com.flab.soft.shoeauction.user.service.SignUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final SignUpService signUpService;

    @GetMapping
    public List<User> allUsers() {
        return signUpService.findAll();
    }

    @GetMapping("/duplicated/email")
    public boolean emailDuplicated(String email) {
        return signUpService.emailDuplicateCheck(email);
    }

    @GetMapping("/duplicated/nickname")
    public boolean nickNameDuplicated(String nickname) {
        return signUpService.emailDuplicateCheck(nickname);
    }

    @PostMapping("/certification/send")
    public ResponseEntity sendCertificationNumber() {
        signUpService.saveAuthenticationNumber();
        return ResponseEntity.ok().build();
    }


    @PostMapping("/new")
    public ResponseEntity<User> savedUser(@RequestBody @Valid UserDto signUpDto) {
        User savedUser = signUpService.saveUser(signUpDto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();

        return ResponseEntity.created(uri).build();
    }


}
