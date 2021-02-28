package com.flab.shoeauction.controller;

import com.flab.shoeauction.common.annotation.LoginCheck;
import com.flab.shoeauction.controller.dto.AdminDto.SearchRequest;
import com.flab.shoeauction.controller.dto.AdminDto.UsersResponse;
import com.flab.shoeauction.controller.dto.UserDto.SaveRequest;
import com.flab.shoeauction.domain.users.common.UserLevel;
import com.flab.shoeauction.domain.users.user.UserRepository;
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
    private final UserRepository userRepository;


    // 데이터 추가용. 테스트 후 삭제 예정
    @GetMapping("/temp")
    public void addUsers() {
        for (int i = 0; i < 30; i++) {
            SaveRequest saveRequest = SaveRequest.builder()
                .email("jungkh405@naver.con" + i)
                .nickname("test1234" + i)
                .phone("01020180103")
                .password("test1234")
                .build();

            userRepository.save(saveRequest.toEntity());
        }
    }


    @LoginCheck(authority = UserLevel.ADMIN)
    @GetMapping("/users")
    public Page<UsersResponse> findByUsers(SearchRequest requestDto, Pageable pageable) {
        return adminService.findUsers(requestDto, pageable);
    }
}