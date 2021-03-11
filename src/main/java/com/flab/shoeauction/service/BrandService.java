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
        String key = FileNameUtils.getFileName(path);

        brandRepository.deleteById(id);
        awsS3Service.deleteBrandImage(key);
    }

    @CacheEvict(value = "brands", allEntries = true)
    @Transactional
    public void updateBrand(Long id, SaveRequest updatedBrand, MultipartFile brandImage) {
        Brand savedBrand = brandRepository.findById(id)
            .orElseThrow(() -> new BrandNotFoundException());
        String savedImagePath = savedBrand.getOriginImagePath();

        checkDuplicateUpdatedNameKor(savedBrand.getNameKor(), updatedBrand.getNameKor());
        checkDuplicateUpdatedNameEng(savedBrand.getNameEng(), updatedBrand.getNameEng());

        if (isDeleteImage(updatedBrand.isImageDeleteCheck(), brandImage, savedImagePath)) {
            String key = FileNameUtils.getFileName(savedImagePath);
            awsS3Service.deleteBrandImage(key);
            updatedBrand.deleteImagePath();
        }
        if ((brandImage != null)) {
            String originImagePath = awsS3Service.uploadBrandImage(brandImage);
            String thumbnailImagePath = FileNameUtils.toThumbnail(originImagePath);
            updatedBrand.setImagePath(originImagePath, thumbnailImagePath);
        }

        savedBrand.update(updatedBrand);
    }

    private boolean isDeleteImage(boolean imageDeleteCheck, MultipartFile brandImage,
        String savedImagePath) {
        if (imageDeleteCheck || ((brandImage != null) && (savedImagePath != null))) {
            return true;
        }
        return false;
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