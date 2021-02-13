package com.flab.shoeauction.service;

import com.flab.shoeauction.controller.dto.BrandDto.BrandInfoResponse;
import com.flab.shoeauction.controller.dto.BrandDto.SaveRequest;
import com.flab.shoeauction.domain.brand.Brand;
import com.flab.shoeauction.domain.brand.BrandRepository;
import com.flab.shoeauction.exception.brand.BrandNotFoundException;
import com.flab.shoeauction.exception.brand.DuplicateBrandNameException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BrandService {

    private final BrandRepository brandRepository;

    public void saveBrand(SaveRequest requestDto) {
        if (checkDuplicateName(requestDto)) {
            throw new DuplicateBrandNameException();
        }
        brandRepository.save(requestDto.toEntity());
    }

    private boolean checkDuplicateName(SaveRequest requestDto) {
        if (brandRepository.existsByNameKor(requestDto.getNameKor())) {
            return true;
        } else if (brandRepository.existsByNameEng(requestDto.getNameEng())) {
            return true;
        }
        return false;
    }

    public BrandInfoResponse getBrandInfo(Long id) {
        return brandRepository.findById(id).orElseThrow(() -> new BrandNotFoundException())
            .toBrandInfoResponse();
    }

    public List<BrandInfoResponse> getBrandInfos() {
        return brandRepository.findAll().stream()
            .map(Brand::toBrandInfoResponse)
            .collect(Collectors.toList());
    }

    public void deleteBrand(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new BrandNotFoundException();
        }
        brandRepository.deleteById(id);
    }

    @Transactional
    public void updateBrand(Long id, SaveRequest requestDto) {
        Brand brand = brandRepository.findById(id).orElseThrow(() -> new BrandNotFoundException());

        checkDuplicateUpdatedNameKor(brand.getNameKor(), requestDto.getNameKor());
        checkDuplicateUpdatedNameEng(brand.getNameEng(), requestDto.getNameEng());

        brand.update(requestDto);
    }

    private void checkDuplicateUpdatedNameKor(String nameKor, String updatedNameKor) {
        if (nameKor.equals(updatedNameKor)) {
            return;
        } else if (!brandRepository.existsByNameKor(updatedNameKor)) {
            return;
        }
        throw new DuplicateBrandNameException();
    }

    private void checkDuplicateUpdatedNameEng(String nameEng, String updatedNameEng) {
        if (nameEng.equals(updatedNameEng)) {
            return;
        } else if (!brandRepository.existsByNameEng(updatedNameEng)) {
            return;
        }
        throw new DuplicateBrandNameException();
    }
}