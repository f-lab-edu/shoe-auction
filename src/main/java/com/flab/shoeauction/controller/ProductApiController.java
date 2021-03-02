package com.flab.shoeauction.controller;

import static com.flab.shoeauction.common.utils.constants.ResponseConstants.CREATED;
import static com.flab.shoeauction.common.utils.constants.ResponseConstants.OK;

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

    @LoginCheck(authority = UserLevel.ADMIN)
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

    @LoginCheck(authority = UserLevel.ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return OK;
    }

    @LoginCheck(authority = UserLevel.ADMIN)
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable Long id,
        @Valid @RequestBody SaveRequest requestDto) {
        productService.updateProduct(id, requestDto);
        return OK;
    }

    @GetMapping("/currencies")
    public ResponseEntity<Currency[]> getCurrencies(){
        Currency[] currencies = Currency.values();
        return ResponseEntity.ok(currencies);
    }

    @GetMapping("/size-classifications")
    public ResponseEntity<SizeClassification[]> getSizeClassifications(){
        SizeClassification[] sizeClassifications = SizeClassification.values();
        return ResponseEntity.ok(sizeClassifications);
    }
    @GetMapping("/size-units")
    public ResponseEntity<SizeUnit[]> getSizeUnits(){
        SizeUnit[] sizeUnits = SizeUnit.values();
        return ResponseEntity.ok(sizeUnits);
    }
}