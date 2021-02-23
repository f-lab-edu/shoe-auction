package com.flab.shoeauction.domain.user.repository;

import com.flab.shoeauction.domain.user.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {

}
