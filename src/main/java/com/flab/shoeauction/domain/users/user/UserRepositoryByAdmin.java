package com.flab.shoeauction.domain.users.user;

import com.flab.shoeauction.controller.dto.UserDto.UserSearchCondition;
import com.flab.shoeauction.controller.dto.UserDto.UserListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryByAdmin {

    Page<UserListResponse> searchByUsers(UserSearchCondition searchRequest, Pageable pageable);
}
