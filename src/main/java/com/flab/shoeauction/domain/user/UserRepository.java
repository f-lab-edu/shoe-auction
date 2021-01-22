package com.flab.shoeauction.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    public boolean existsByEmail(String email);

    public boolean existsByNickname(String nickname);

    public boolean existsByEmailAndPassword(String email, String password);

    public User findByEmail(String email);
}
