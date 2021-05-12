package com.flab.shoeauction.domain.users.user;

import com.flab.shoeauction.domain.users.admin.AdminRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>, AdminRepository {

    @EntityGraph(attributePaths = {"addressBook", "cart"})
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByEmailAndPassword(String email, String password);

    void deleteByEmail(String email);

}
