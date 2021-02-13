package com.flab.shoeauction.controller;

import static com.flab.shoeauction.common.utils.response.ResponseConstants.CREATED;
import static com.flab.shoeauction.common.utils.response.ResponseConstants.OK;

import com.flab.shoeauction.controller.dto.BrandDto.BrandInfoResponse;
import com.flab.shoeauction.controller.dto.BrandDto.SaveRequest;
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

@RequiredArgsConstructor
@RequestMapping("/brands")
@Controller
public class BrandApiController {

    private final BrandService brandService;

    @PostMapping
    public ResponseEntity<Void> createBrand(@Valid @RequestBody SaveRequest requestDto) {
        brandService.saveBrand(requestDto);
        return CREATED;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandInfoResponse> getBrandInfo(@PathVariable Long id) {
        BrandInfoResponse brandInfoResponse = brandService.getBrandInfo(id);
        return ResponseEntity.ok(brandInfoResponse);
    }

    @GetMapping
    public ResponseEntity<List<BrandInfoResponse>> getBrandInfos() {
        List<BrandInfoResponse> brandInfoResponses = brandService.getBrandInfos();
        return ResponseEntity.ok(brandInfoResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return OK;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateBrand(@PathVariable Long id, @Valid @RequestBody SaveRequest requestDto) {
        brandService.updateBrand(id, requestDto);
        return OK;
    }
}
