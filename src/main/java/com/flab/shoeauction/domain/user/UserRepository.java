package com.flab.shoeauction.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByEmailAndPassword(String email, String password);

    User findByEmail(String email);

    void deleteByEmail(String email);
}
