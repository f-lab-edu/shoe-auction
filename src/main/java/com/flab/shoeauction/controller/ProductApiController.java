package com.flab.shoeauction.controller;

import com.flab.shoeauction.common.annotation.LoginCheck;
import com.flab.shoeauction.controller.dto.ProductDto.ProductInfoResponse;
import com.flab.shoeauction.controller.dto.ProductDto.SaveRequest;
import com.flab.shoeauction.controller.dto.ProductDto.SearchCondition;
import com.flab.shoeauction.controller.dto.ProductDto.ThumbnailResponse;
import com.flab.shoeauction.domain.product.common.Currency;
import com.flab.shoeauction.domain.product.common.OrderStandard;
import com.flab.shoeauction.domain.product.common.SizeClassification;
import com.flab.shoeauction.domain.product.common.SizeUnit;
import com.flab.shoeauction.domain.users.common.UserLevel;
import com.flab.shoeauction.service.BrandService;
import com.flab.shoeauction.service.ProductService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/products")
@RestController
public class ProductApiController {

    private final ProductService productService;

    private final BrandService brandService;

    @LoginCheck(authority = UserLevel.ADMIN)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void createProduct(@Valid @RequestBody SaveRequest requestDto,
        @RequestPart(required = false) MultipartFile productImage) {
        brandService.checkBrandExist(requestDto.getBrand());
        productService.saveProduct(requestDto, productImage);
    }

    @GetMapping("/{id}")
    public ProductInfoResponse getProductInfo(@PathVariable Long id) {
        ProductInfoResponse productInfoResponse = productService.getProductInfo(id);
        return productInfoResponse;
    }

    @GetMapping
    public Page<ThumbnailResponse> getProductsThumbnail(
        SearchCondition condition, Pageable pageable) {

        return productService.findProducts(condition, pageable);
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
        @Valid @RequestPart SaveRequest requestDto,
        @RequestPart(required = false) MultipartFile productImage) {
        productService.updateProduct(id, requestDto, productImage);
    }

    @GetMapping("/currencies")
    public Currency[] getCurrencies() {
        return Currency.values();
    }

    @GetMapping("/size-classifications")
    public SizeClassification[] getSizeClassifications() {
        return SizeClassification.values();
    }

    @GetMapping("/size-units")
    public SizeUnit[] getSizeUnits() {
        return SizeUnit.values();
    }

    @GetMapping("/order-standards")
    public OrderStandard[] getProductOrderStandards() {
        return OrderStandard.values();
    }
}