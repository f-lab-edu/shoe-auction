package com.flab.shoeauction.domain.users;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}
