package com.flab.shoeauction.service;

import com.flab.shoeauction.controller.dto.AdminDto.SearchRequest;
import com.flab.shoeauction.controller.dto.AdminDto.UsersResponse;
import com.flab.shoeauction.domain.users.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public Page<UsersResponse> findUsers(SearchRequest requestDto, Pageable pageable) {
        return userRepository.searchByUsers(requestDto, pageable);
    }
}
