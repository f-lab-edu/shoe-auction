package com.flab.shoeauction.controller;

import com.flab.shoeauction.common.annotation.LoginCheck;
import com.flab.shoeauction.controller.dto.ProductDto.ProductInfoResponse;
import com.flab.shoeauction.controller.dto.ProductDto.SaveRequest;
import com.flab.shoeauction.domain.product.Currency;
import com.flab.shoeauction.domain.product.SizeClassification;
import com.flab.shoeauction.domain.product.SizeUnit;
import com.flab.shoeauction.domain.users.common.UserLevel;
import com.flab.shoeauction.service.BrandService;
import com.flab.shoeauction.service.ProductService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequiredArgsConstructor
@RequestMapping("/products")
@Controller
public class ProductApiController {

    private final ProductService productService;
    private final BrandService brandService;

    @LoginCheck(authority = UserLevel.ADMIN)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void createProduct(@Valid @RequestBody SaveRequest requestDto) {
        brandService.checkBrandExist(requestDto.getBrand());
        productService.saveProduct(requestDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductInfoResponse> getProductInfo(@PathVariable Long id) {
        ProductInfoResponse productInfoResponse = productService.getProductInfo(id);
        return ResponseEntity.ok(productInfoResponse);
    }

    @LoginCheck(authority = UserLevel.ADMIN)
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    @LoginCheck(authority = UserLevel.ADMIN)
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}")
    public void updateProduct(@PathVariable Long id,
        @Valid @RequestBody SaveRequest requestDto) {
        productService.updateProduct(id, requestDto);
    }

    @GetMapping("/currencies")
    public ResponseEntity<Currency[]> getCurrencies() {
        Currency[] currencies = Currency.values();
        return ResponseEntity.ok(currencies);
    }

    @GetMapping("/size-classifications")
    public ResponseEntity<SizeClassification[]> getSizeClassifications() {
        SizeClassification[] sizeClassifications = SizeClassification.values();
        return ResponseEntity.ok(sizeClassifications);
    }

    @GetMapping("/size-units")
    public ResponseEntity<SizeUnit[]> getSizeUnits() {
        SizeUnit[] sizeUnits = SizeUnit.values();
        return ResponseEntity.ok(sizeUnits);
    }
}