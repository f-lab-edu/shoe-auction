package com.flab.shoeauction.controller.dto;

import com.flab.shoeauction.domain.users.common.UserLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class AdminDto {

    @Getter
    @NoArgsConstructor
    public static class UsersResponse {

        private Long id;
        private String email;
        private UserLevel userLevel;

        @Builder
        public UsersResponse(Long id, String email, UserLevel userLevel) {
            this.id = id;
            this.email = email;
            this.userLevel = userLevel;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SearchRequest {

        private Long id;
        private String email;
        private UserLevel userLevel;

        @Builder
        public SearchRequest(Long id, String email, UserLevel userLevel) {
            this.id = id;
            this.email = email;
            this.userLevel = userLevel;
        }
    }


}
