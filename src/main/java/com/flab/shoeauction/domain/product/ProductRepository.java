package com.flab.shoeauction.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, SearchProductRepository {

    boolean existsByModelNumber(String modelNumber);
}