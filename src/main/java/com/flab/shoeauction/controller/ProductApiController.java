package com.flab.shoeauction.controller;

import static com.flab.shoeauction.common.utils.response.ResponseConstants.CREATED;
import static com.flab.shoeauction.common.utils.response.ResponseConstants.OK;

import com.flab.shoeauction.controller.dto.ProductDto.ProductInfoResponse;
import com.flab.shoeauction.controller.dto.ProductDto.SaveRequest;
import com.flab.shoeauction.service.BrandService;
import com.flab.shoeauction.service.ProductService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/products")
@Controller
public class ProductApiController {

    private final ProductService productService;
    private final BrandService brandService;

    @PostMapping
    public ResponseEntity<Void> createProduct(@Valid @RequestBody SaveRequest requestDto) {
        brandService.checkBrandExist(requestDto.getBrand());
        productService.saveProduct(requestDto);
        return CREATED;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductInfoResponse> getProductInfo(@PathVariable Long id) {
        ProductInfoResponse productInfoResponse = productService.getProductInfo(id);
        return ResponseEntity.ok(productInfoResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return OK;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable Long id,
        @Valid @RequestBody SaveRequest requestDto) {
        productService.updateProduct(id, requestDto);
        return OK;
    }
}