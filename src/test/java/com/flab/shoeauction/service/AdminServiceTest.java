package com.flab.shoeauction.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import com.flab.shoeauction.controller.dto.UserDto.UserBanRequest;
import com.flab.shoeauction.controller.dto.UserDto.UserListResponse;
import com.flab.shoeauction.domain.users.common.UserLevel;
import com.flab.shoeauction.domain.users.common.UserStatus;
import com.flab.shoeauction.domain.users.user.User;
import com.flab.shoeauction.domain.users.user.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    UserRepository userRepository;


    @InjectMocks
    AdminService adminService;

    private List<UserListResponse> setUsers() {
        List<UserListResponse> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            UserListResponse userListResponse = UserListResponse.builder()
                .id((long) i)
                .email("jungkh405@naver.com" + i)
                .userLevel(UserLevel.AUTH)
                .build();
            list.add(userListResponse);
        }
        return list;
    }

    @DisplayName("가입된 회원 전체를 조회한다.")
    @Test
    public void findAll() {
        List<UserListResponse> list = setUsers();
        long total = list.size();
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserListResponse> result = new PageImpl<>(list, pageable, total);
        given(userRepository.searchByUsers(any(), any())).willReturn(result);

        adminService.findUsers(any(), any());

        assertThat(result.getContent().size()).isEqualTo(list.size());
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        verify(userRepository, atLeastOnce()).searchByUsers(any(), any());
    }

    @DisplayName("요청한 ID에 해당하는 사용자를 BAN 처리 한다.")
    @Test
    public void updateBanUsers() {

        User user = User.builder()
            .id(1L)
            .email("jungkh405@naver.com")
            .userStatus(UserStatus.NORMAL)
            .build();

        UserBanRequest userBanRequest = UserBanRequest
            .builder()
            .id(1L)
            .userStatus(UserStatus.BAN)
            .build();
        given(userRepository.findById(any())).willReturn(Optional.of(user));

        adminService.updateBanUsers(userBanRequest);

        assertThat(user.getUserStatus()).isEqualTo(UserStatus.BAN);
        verify(userRepository, atLeastOnce()).findById(any());

    }

    @DisplayName("요청한 ID에 해당하는 사용자의 BAN을 해제 한다.")
    @Test
    public void updateBanUsers_normal() {

        User user = User.builder()
            .id(1L)
            .email("jungkh405@naver.com")
            .userStatus(UserStatus.BAN)
            .build();

        UserBanRequest userBanRequest = UserBanRequest
            .builder()
            .id(1L)
            .userStatus(UserStatus.NORMAL)
            .build();
        given(userRepository.findById(any())).willReturn(Optional.of(user));

        adminService.updateBanUsers(userBanRequest);

        assertThat(user.getUserStatus()).isEqualTo(UserStatus.NORMAL);
        verify(userRepository, atLeastOnce()).findById(any());
    }

}