package com.flab.shoeauction.domain.users.user;

import com.flab.shoeauction.domain.users.admin.AdminRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>, AdminRepository {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByEmailAndPassword(String email, String password);

    Optional<User> findByEmail(String email);

    void deleteByEmail(String email);



}
