package com.flab.shoeauction.service;

import com.flab.shoeauction.controller.dto.UserDto.IdRequest;
import com.flab.shoeauction.controller.dto.UserDto.UserDetailsResponse;
import com.flab.shoeauction.controller.dto.UserDto.UserListResponse;
import com.flab.shoeauction.controller.dto.UserDto.UserSearchCondition;
import com.flab.shoeauction.domain.users.user.User;
import com.flab.shoeauction.domain.users.user.UserRepository;
import com.flab.shoeauction.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public Page<UserListResponse> findUsers(UserSearchCondition requestDto, Pageable pageable) {
        return userRepository.searchByUsers(requestDto, pageable);
    }

    public UserDetailsResponse getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("존재하지 않는 회원입니다."));
        return user.toUserDetailsDto();
    }

    @Transactional
    public void updateBanUsers(IdRequest requestDto) {
        Long id = requestDto.getId();
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("존재하지 않는 회원입니다."));
        user.updateBan();
    }
}
