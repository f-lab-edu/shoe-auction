package com.flab.shoeauction.service;

import com.flab.shoeauction.controller.dto.ProductDto.ProductInfoResponse;
import com.flab.shoeauction.controller.dto.ProductDto.SaveRequest;
import com.flab.shoeauction.domain.product.Product;
import com.flab.shoeauction.domain.product.ProductRepository;
import com.flab.shoeauction.exception.product.DuplicateModelNumberException;
import com.flab.shoeauction.exception.product.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public void saveProduct(SaveRequest requestDto) {
        if (productRepository.existsByModelNumber(requestDto.getModelNumber())) {
            throw new DuplicateModelNumberException();
        }
        productRepository.save(requestDto.toEntity());
    }

    public ProductInfoResponse getProductInfo(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException())
            .toProductInfoResponse();
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException();
        }
        productRepository.deleteById(id);
    }

    public void updateProduct(Long id, SaveRequest updatedProduct) {
        Product savedProduct = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException());

        checkDuplicateUpdatedModelNumber(savedProduct.getModelNumber(),
            updatedProduct.getModelNumber());

        savedProduct.update(updatedProduct);
    }

    private void checkDuplicateUpdatedModelNumber(String modelNumber, String updatedModelNumber) {
        if (modelNumber.equals(updatedModelNumber)) {
            return;
        } else if (!productRepository.existsByModelNumber(updatedModelNumber)) {
            return;
        }
        throw new DuplicateModelNumberException();
    }
}
