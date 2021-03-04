package com.flab.shoeauction.domain.users.admin;

import com.flab.shoeauction.controller.dto.UserDto.UserSearchCondition;
import com.flab.shoeauction.controller.dto.UserDto.UserListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminRepository {

    Page<UserListResponse> searchByUsers(UserSearchCondition searchRequest, Pageable pageable);
}
