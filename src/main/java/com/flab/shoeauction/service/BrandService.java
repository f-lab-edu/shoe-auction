package com.flab.shoeauction.service;

import com.flab.shoeauction.common.utils.file.FileNameUtils;
import com.flab.shoeauction.controller.dto.BrandDto.BrandInfo;
import com.flab.shoeauction.controller.dto.BrandDto.SaveRequest;
import com.flab.shoeauction.domain.brand.Brand;
import com.flab.shoeauction.domain.brand.BrandRepository;
import com.flab.shoeauction.exception.brand.BrandNotFoundException;
import com.flab.shoeauction.exception.brand.DuplicateBrandNameException;
import com.flab.shoeauction.service.storage.AwsS3Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
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

    @Transactional
    @CacheEvict(value = "brands", allEntries = true)
    public void saveBrand(SaveRequest requestDto, MultipartFile brandImage) {
        if (checkDuplicateName(requestDto)) {
            throw new DuplicateBrandNameException();
        }
        if (brandImage != null) {
            String originImagePath = awsS3Service.uploadBrandImage(brandImage);
            String thumbnailImagePath = FileNameUtils.toThumbnail(originImagePath);
            requestDto.setImagePath(originImagePath, thumbnailImagePath);
        }
        brandRepository.save(requestDto.toEntity());
    }

    @CacheEvict(value = "brands", allEntries = true)
    @Transactional
    public void deleteBrand(Long id) {
        Brand brand = brandRepository.findById(id)
            .orElseThrow(BrandNotFoundException::new);
        String path = brand.getOriginImagePath();

        brandRepository.deleteById(id);
        if (path != null) {
            String key = FileNameUtils.getFileName(path);
            awsS3Service.deleteBrandImage(key);
        }
    }

    /*
     * 기존 이미지 삭제 조건
     * 1) requestDto의 imagePath가 null이고, 저장된 brand의 imagePath가 null이 아니라면
     * 2) requestDto의 imagePath가 null이 아니고, 요청에 MultipartFile이 존재한다면
     * 스토리지에서 기존의 이미지를 delete하고, imagePath를 null로 초기화한다.
     * (기존 이미지 삭제 조건을 철저히 하여 스토리지 내 유령 파일을 만들지 않도록 한다.)
     */
    @CacheEvict(value = "brands", allEntries = true)
    @Transactional
    public void updateBrand(Long id, SaveRequest updatedBrand, @Nullable MultipartFile brandImage) {
        Brand savedBrand = brandRepository.findById(id)
            .orElseThrow(() -> new BrandNotFoundException());
        String savedImagePath = savedBrand.getOriginImagePath();
        String updatedImagePath = updatedBrand.getOriginImagePath();

        checkDuplicateUpdatedNameKor(savedBrand.getNameKor(), updatedBrand.getNameKor());
        checkDuplicateUpdatedNameEng(savedBrand.getNameEng(), updatedBrand.getNameEng());

        if (isDeleteSavedImage(savedImagePath, updatedImagePath, brandImage)) {
            String key = FileNameUtils.getFileName(savedImagePath);
            awsS3Service.deleteBrandImage(key);
            updatedBrand.deleteImagePath();
        }
        if (brandImage != null) {
            String originImagePath = awsS3Service.uploadBrandImage(brandImage);
            String thumbnailImagePath = FileNameUtils.toThumbnail(originImagePath);
            updatedBrand.setImagePath(originImagePath, thumbnailImagePath);
        }

        savedBrand.update(updatedBrand);
    }

    private boolean isDeleteSavedImage(String savedImagePath, String updatedImagePath,
        MultipartFile brandImage) {
        return ((updatedImagePath == null && savedImagePath != null) ||
            (updatedImagePath != null && brandImage != null));
    }

    public void checkBrandExist(BrandInfo productsBrand) {
        Optional<Brand> savedBrand = brandRepository.findById(productsBrand.getId());
        if (savedBrand.isEmpty() || !isSameName(savedBrand.get(), productsBrand)) {
            throw new BrandNotFoundException();
        }
    }

    private boolean checkDuplicateName(SaveRequest requestDto) {
        if (brandRepository.existsByNameKor(requestDto.getNameKor())) {
            return true;
        } else if (brandRepository.existsByNameEng(requestDto.getNameEng())) {
            return true;
        }
        return false;
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

    private boolean isSameName(Brand savedBrand, BrandInfo productsBrand) {
        if (!savedBrand.getNameEng().equals(productsBrand.getNameEng())) {
            return false;
        } else if (!savedBrand.getNameKor().equals(productsBrand.getNameKor())) {
            return false;
        }
        return true;
    }
}