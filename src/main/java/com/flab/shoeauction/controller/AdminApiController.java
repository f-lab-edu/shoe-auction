package com.flab.shoeauction.controller;

import com.flab.shoeauction.common.annotation.LoginCheck;
import com.flab.shoeauction.controller.dto.UserDto.UserListResponse;
import com.flab.shoeauction.controller.dto.UserDto.UserSearchCondition;
import com.flab.shoeauction.domain.users.common.UserLevel;
import com.flab.shoeauction.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
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
}