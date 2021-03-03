package com.flab.shoeauction.service;

import static com.flab.shoeauction.common.utils.constants.FilePathConstants.BRAND_IMAGES_DIR;

import com.flab.shoeauction.controller.dto.BrandDto.BrandInfo;
import com.flab.shoeauction.controller.dto.BrandDto.SaveRequest;
import com.flab.shoeauction.domain.brand.Brand;
import com.flab.shoeauction.domain.brand.BrandRepository;
import com.flab.shoeauction.exception.brand.BrandNotFoundException;
import com.flab.shoeauction.exception.brand.DuplicateBrandNameException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class BrandService {

    private final BrandRepository brandRepository;

    private final AwsS3Service awsS3Service;

    @Transactional
    @CacheEvict(value = "brands", allEntries = true)
    public void saveBrand(SaveRequest requestDto, MultipartFile brandImage) {
        if (checkDuplicateName(requestDto)) {
            throw new DuplicateBrandNameException();
        }
        String imagePath = awsS3Service.upload(brandImage, BRAND_IMAGES_DIR);
        requestDto.setImagePath(imagePath);
        brandRepository.save(requestDto.toEntity());
    }

    @Transactional
    @CacheEvict(value = "brands", allEntries = true)
    public void saveBrandWithoutImage(SaveRequest requestDto) {
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

    public BrandInfo getBrandInfo(Long id) {
        return brandRepository.findById(id).orElseThrow(() -> new BrandNotFoundException())
            .toBrandInfo();
    }

    @Cacheable(value = "brands")
    public List<BrandInfo> getBrandInfos() {
        return brandRepository.findAll().stream()
            .map(Brand::toBrandInfo)
            .collect(Collectors.toList());
    }

    @CacheEvict(value = "brands", allEntries = true)
    public void deleteBrand(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new BrandNotFoundException();
        }
        brandRepository.deleteById(id);
    }

    @CacheEvict(value = "brands", allEntries = true)
    @Transactional
    public void updateBrand(Long id, SaveRequest updatedBrand) {
        Brand savedBrand = brandRepository.findById(id)
            .orElseThrow(() -> new BrandNotFoundException());

        checkDuplicateUpdatedNameKor(savedBrand.getNameKor(), updatedBrand.getNameKor());
        checkDuplicateUpdatedNameEng(savedBrand.getNameEng(), updatedBrand.getNameEng());

        savedBrand.update(updatedBrand);
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

    public void checkBrandExist(BrandInfo productsBrand) {
        Optional<Brand> savedBrand = brandRepository.findById(productsBrand.getId());
        if (savedBrand.isEmpty() || !isSameName(savedBrand.get(), productsBrand)) {
            throw new BrandNotFoundException();
        }
    }

    private boolean isSameName(Brand savedBrand, BrandInfo productsBrand) {
        if (!savedBrand.getNameEng().equals(productsBrand.getNameEng())) {
            return false;
        } else if (!savedBrand.getNameKor().equals(productsBrand.getNameKor())) {
            return false;
        }
        return true;
    }
}