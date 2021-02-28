package com.flab.shoeauction.domain.users.user;

import com.flab.shoeauction.controller.dto.AdminDto.SearchRequest;
import com.flab.shoeauction.controller.dto.AdminDto.UsersResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {

    Page<UsersResponse> searchByUsers(SearchRequest searchRequest, Pageable pageable);
}
