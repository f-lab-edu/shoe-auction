package com.flab.soft.shoeauction.user.controller;

import com.flab.soft.shoeauction.user.domain.User;
import com.flab.soft.shoeauction.user.dto.UserDto;
import com.flab.soft.shoeauction.user.service.UserService;
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

    private final UserService userService;

    @GetMapping
    public List<User> allUsers() {
        return userService.findAll();
    }

    @GetMapping("/duplicated/email")
    public boolean emailDuplicated(String email) {
        return userService.emailDuplicateCheck(email);
    }

    @GetMapping("/duplicated/nickname")
    public boolean nickNameDuplicated(String nickname) {
        return userService.emailDuplicateCheck(nickname);
    }

    @PostMapping("/verification/send")
    public ResponseEntity<String> sendAuthenticationNumber() {
        String authenticationNumber = userService.getRandomNumber();
        return ResponseEntity.ok().body(authenticationNumber);
    }


    @PostMapping("/new")
    public ResponseEntity<User> createdUser(@RequestBody @Valid UserDto signUpDto) {
        User savedUser = userService.saveUser(signUpDto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();

        return ResponseEntity.created(uri).build();
    }


}
