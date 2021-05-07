package com.flab.shoeauction.controller;

import com.flab.shoeauction.common.annotation.LoginCheck;
import com.flab.shoeauction.controller.dto.UserDto.UserBanRequest;
import com.flab.shoeauction.controller.dto.UserDto.UserDetailsResponse;
import com.flab.shoeauction.controller.dto.UserDto.UserListResponse;
import com.flab.shoeauction.controller.dto.UserDto.UserSearchCondition;
import com.flab.shoeauction.domain.users.common.UserLevel;
import com.flab.shoeauction.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminApiController {

    private final AdminService adminService;

    @LoginCheck(authority = UserLevel.ADMIN)
    @GetMapping("/users")
    public Page<UserListResponse> findByUsers(UserSearchCondition requestDto, Pageable pageable) {
        return adminService.findUsers(requestDto, pageable);
    }

    @GetMapping("/temp")
    public String temp() {
        return "check deploy";
    }

    @LoginCheck(authority = UserLevel.ADMIN)
    @GetMapping("/users/{id}")
    public UserDetailsResponse getUserDetails(@PathVariable Long id) {
        return adminService.getUser(id);
    }

    @LoginCheck(authority = UserLevel.ADMIN)
    @PostMapping("/users/ban")
    public void restrictUsers(@RequestBody UserBanRequest requestDto) {
        adminService.updateBanUsers(requestDto);
    }
}