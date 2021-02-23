package com.flab.shoeauction.domain.user.repository;

import com.flab.shoeauction.domain.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByEmailAndPassword(String email, String password);

    Optional<User> findByEmail(String email);

    void deleteByEmail(String email);
}
