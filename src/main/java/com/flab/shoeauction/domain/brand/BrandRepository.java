package com.flab.shoeauction.domain.brand;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    boolean existsByNameKor(String nameKor);

    boolean existsByNameEng(String nameEng);
}