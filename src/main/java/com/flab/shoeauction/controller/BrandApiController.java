package com.flab.shoeauction.controller;

import com.flab.shoeauction.common.annotation.LoginCheck;
import com.flab.shoeauction.controller.dto.BrandDto.BrandInfo;
import com.flab.shoeauction.controller.dto.BrandDto.SaveRequest;
import com.flab.shoeauction.domain.users.common.UserLevel;
import com.flab.shoeauction.service.BrandService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/brands")
@RestController
public class BrandApiController {

    private final BrandService brandService;

    @LoginCheck(authority = UserLevel.ADMIN)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void createBrand(@Valid @RequestBody SaveRequest requestDto,
        @RequestPart(required = false) MultipartFile brandImage) {
        brandService.saveBrand(requestDto, brandImage);
    }

    @GetMapping("/{id}")
    public BrandInfo getBrandInfo(@PathVariable Long id) {
        return brandService.getBrandInfo(id);
    }

    @GetMapping
    public List<BrandInfo> getBrandInfos() {
        return brandService.getBrandInfos();
    }

    @LoginCheck(authority = UserLevel.ADMIN)
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
    }

    @LoginCheck(authority = UserLevel.ADMIN)
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}")
    public void updateBrand(@PathVariable Long id,
        @Valid @RequestPart SaveRequest requestDto,
        @RequestPart(required = false) MultipartFile brandImage) {
        brandService.updateBrand(id, requestDto, brandImage);
    }
}
