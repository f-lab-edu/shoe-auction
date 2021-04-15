package com.flab.shoeauction.domain.product;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, SearchProductRepository {

    boolean existsByModelNumber(String modelNumber);

    @Override
    @EntityGraph(attributePaths = {"trades", "brand"})
    Optional<Product> findById(Long id);

}