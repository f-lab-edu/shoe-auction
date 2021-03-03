package com.flab.shoeauction.controller;

import static com.flab.shoeauction.common.utils.constants.ResponseConstants.CREATED;
import static com.flab.shoeauction.common.utils.constants.ResponseConstants.OK;

import com.flab.shoeauction.common.annotation.LoginCheck;
import com.flab.shoeauction.controller.dto.BrandDto.BrandInfo;
import com.flab.shoeauction.controller.dto.BrandDto.SaveRequest;
import com.flab.shoeauction.domain.users.common.UserLevel;
import com.flab.shoeauction.service.BrandService;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/brands")
@Controller
public class BrandApiController {

    private final BrandService brandService;

    @LoginCheck(authority = UserLevel.ADMIN)
    @PostMapping
    public ResponseEntity<Void> createBrand(@Valid @RequestPart SaveRequest requestDto,
        @RequestPart(required = false) MultipartFile brandImage) {
        if (brandImage != null) {
            brandService.saveBrand(requestDto, brandImage);
        } else {
            brandService.saveBrandWithoutImage(requestDto);
        }
        return CREATED;
    }

    @LoginCheck(authority = UserLevel.ADMIN)
    @GetMapping("/{id}")
    public ResponseEntity<BrandInfo> getBrandInfo(@PathVariable Long id) {
        BrandInfo brandInfoResponse = brandService.getBrandInfo(id);
        return ResponseEntity.ok(brandInfoResponse);
    }

    @GetMapping
    public ResponseEntity<List<BrandInfo>> getBrandInfos() {
        List<BrandInfo> brandInfoResponses = brandService.getBrandInfos();
        return ResponseEntity.ok(brandInfoResponses);
    }

    @LoginCheck(authority = UserLevel.ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return OK;
    }

    @LoginCheck(authority = UserLevel.ADMIN)
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateBrand(@PathVariable Long id,
        @Valid @RequestBody SaveRequest requestDto) {
        brandService.updateBrand(id, requestDto);
        return OK;
    }
}
