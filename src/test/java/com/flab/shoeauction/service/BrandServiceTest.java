package com.flab.shoeauction.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.flab.shoeauction.common.utils.file.FileNameUtils;
import com.flab.shoeauction.controller.dto.BrandDto.BrandInfo;
import com.flab.shoeauction.controller.dto.BrandDto.SaveRequest;
import com.flab.shoeauction.domain.brand.Brand;
import com.flab.shoeauction.domain.brand.BrandRepository;
import com.flab.shoeauction.exception.brand.BrandNotFoundException;
import com.flab.shoeauction.exception.brand.DuplicateBrandNameException;
import com.flab.shoeauction.exception.file.ImageRoadFailedException;
import com.flab.shoeauction.service.storage.AwsS3Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    BrandRepository brandRepository;

    @Mock
    AwsS3Service awsS3Service;

    @InjectMocks
    BrandService brandService;

    private String originImagePath = "https://shoeauction-brands-origin.s3.ap-northeast-2.amazonaws.com/sample.png";
    private String thumbnailImagePath = "https://shoeauction-brands-thumbnail.s3.ap-northeast-2.amazonaws.com/sample.png";
    private String changedOriginImagePath = "https://shoeauction-brands-origin.s3.ap-northeast-2.amazonaws.com/sample.png";
    private String changedThumbnailImagePath = "https://shoeauction-brands-thumbnail.s3.ap-northeast-2.amazonaws.com/sample.png";

    private Brand createBrand() {
        return Brand.builder()
            .nameKor("나이키")
            .nameEng("Nike")
            .originImagePath(originImagePath)
            .thumbnailImagePath(thumbnailImagePath)
            .build();
    }

    private Brand createBrandWithoutImage() {
        return Brand.builder()
            .nameKor("아디다스")
            .nameEng("Adidas")
            .build();
    }

    private List<Brand> createBrandList() {
        List<Brand> brandList = new ArrayList<>();
        brandList.add(createBrand());
        brandList.add(createBrandWithoutImage());

        return brandList;
    }

    private SaveRequest createSaveRequest() {
        return SaveRequest.builder()
            .nameKor("뉴발란스")
            .nameEng("NewBalance")
            .build();
    }

    private SaveRequest createUpdateRequest() {
        return SaveRequest.builder()
            .nameKor("뉴발란스")
            .nameEng("NewBalance")
            .originImagePath(originImagePath)
            .thumbnailImagePath(thumbnailImagePath)
            .build();
    }

    private SaveRequest createUpdateRequestWithoutImage() {
        return SaveRequest.builder()
            .nameKor("뉴발란스")
            .nameEng("NewBalance")
            .build();
    }

    private MultipartFile createImageFile() {
        return new MockMultipartFile("sample", "sample.png", MediaType.IMAGE_PNG_VALUE,
            "sample".getBytes());
    }

    @DisplayName("특정 id를 가진 브랜드가 존재하여 조회에 성공한다.")
    @Test
    public void getBrandInfo() {
        Brand brand = createBrand();
        Long id = brand.getId();
        given(brandRepository.findById(id)).willReturn(java.util.Optional.of(brand));

        BrandInfo brandInfo = brandService.getBrandInfo(id);

        assertThat(brandInfo.getId()).isEqualTo(id);
        assertThat(brandInfo.getNameKor()).isEqualTo(brand.getNameKor());
        assertThat(brandInfo.getNameEng()).isEqualTo(brand.getNameEng());
        assertThat(brandInfo.getOriginImagePath()).isEqualTo(brand.getOriginImagePath());
        assertThat(brandInfo.getThumbnailImagePath()).isEqualTo(brand.getThumbnailImagePath());
        verify(brandRepository, times(1)).findById(id);
    }

    @DisplayName("특정 id를 가진 브랜드가 존재하지 않아 조회에 실패한다.")
    @Test
    public void failToGetBrandInfoIfBrandNotExist() {
        Long id = 1L;
        given(brandRepository.findById(id)).willReturn(Optional.empty());

        assertThrows(BrandNotFoundException.class, () -> brandService.getBrandInfo(id));
        verify(brandRepository, times(1)).findById(id);
    }

    @DisplayName("전체 브랜드 조회에 성공한다.")
    @Test
    public void getBrandInfos() {
        List<Brand> brandList = createBrandList();
        given(brandRepository.findAll()).willReturn(brandList);

        List<BrandInfo> brandInfos = brandService.getBrandInfos();

        assertThat(brandInfos.size()).isEqualTo(brandList.size());
        verify(brandRepository, times(1)).findAll();
    }

    @DisplayName("이미지 없이 브랜드 저장에 성공한다.")
    @Test
    public void saveBrandWithoutImage() {
        SaveRequest brand = createSaveRequest();

        brandService.saveBrand(brand, null);

        verify(brandRepository, times(1)).save(any());
    }

    @DisplayName("브랜드 한글명 중복으로 인해 브랜드 저장에 실패한다.")
    @Test
    public void failToSaveBrandIfDuplicateKorName() {
        SaveRequest brand = createSaveRequest();
        given(brandRepository.existsByNameKor(brand.getNameKor())).willReturn(true);

        assertThrows(DuplicateBrandNameException.class, () -> brandService.saveBrand(brand, null));
        verify(brandRepository, never()).save(any());
    }

    @DisplayName("브랜드 영문명 중복으로 인해 브랜드 저장에 실패한다.")
    @Test
    public void failToSaveBrandIfDuplicateEngName() {
        SaveRequest brand = createSaveRequest();
        given(brandRepository.existsByNameEng(brand.getNameEng())).willReturn(true);

        assertThrows(DuplicateBrandNameException.class, () -> brandService.saveBrand(brand, null));
        verify(brandRepository, never()).save(any());
    }

    @DisplayName("이미지와 함께 브랜드 저장에 성공한다.")
    @Test
    public void saveBrandWithImage() {
        SaveRequest brand = createSaveRequest();
        MultipartFile file = createImageFile();
        given(awsS3Service.uploadBrandImage(file)).willReturn(originImagePath);

        brandService.saveBrand(brand, file);

        assertThat(brand.getOriginImagePath()).isEqualTo(originImagePath);
        assertThat(brand.getThumbnailImagePath()).isEqualTo(thumbnailImagePath);
        verify(awsS3Service, times(1)).uploadBrandImage(file);
        verify(brandRepository, times(1)).save(any());
    }

    @DisplayName("이미지 업로드 실패로 인해 브랜드 저장에 실패한다.")
    @Test
    public void failToSaveBrandIfImageUploadFailed() {
        SaveRequest brand = createSaveRequest();
        MultipartFile file = createImageFile();
        given(awsS3Service.uploadBrandImage(file)).willThrow(ImageRoadFailedException.class);

        assertThrows(ImageRoadFailedException.class, () -> brandService.saveBrand(brand, file));

        assertThat(brand.getOriginImagePath()).isNull();
        assertThat(brand.getThumbnailImagePath()).isNull();
        verify(awsS3Service, times(1)).uploadBrandImage(file);
        verify(brandRepository, never()).save(any());
    }


    @DisplayName("이미지가 없는 브랜드 삭제에 성공한다.")
    @Test
    public void deleteBrandWithoutImage() {
        Brand brand = createBrandWithoutImage();
        Long id = brand.getId();
        given(brandRepository.findById(id)).willReturn(java.util.Optional.of(brand));

        brandService.deleteBrand(id);

        verify(brandRepository, times(1)).deleteById(id);
    }

    @DisplayName("브랜드가 존재하지 않아서 삭제에 실패한다.")
    @Test
    public void failToDeleteBrandIfBrandNotExist() {
        Long id = 1L;
        given(brandRepository.findById(id)).willReturn(Optional.empty());

        assertThrows(BrandNotFoundException.class, () -> brandService.deleteBrand(id));
        verify(brandRepository, never()).deleteById(id);
    }

    @DisplayName("이미지와 함께 브랜드 삭제에 성공한다.")
    @Test
    public void deleteBrandWithImage() {
        Brand brand = createBrand();
        Long id = brand.getId();
        given(brandRepository.findById(id)).willReturn(java.util.Optional.of(brand));

        brandService.deleteBrand(id);

        verify(brandRepository, times(1)).deleteById(id);
        verify(awsS3Service, times(1)).deleteBrandImage(any());
    }

    @DisplayName("새로운 이미지 없이 브랜드 업데이트에 성공한다.")
    @Test
    public void updateBrandWithoutImage() {
        Brand savedBrand = createBrand();
        SaveRequest updateBrandDto = createUpdateRequest();
        Long id = savedBrand.getId();
        given(brandRepository.findById(id)).willReturn(java.util.Optional.of(savedBrand));
        given(brandRepository.existsByNameKor(updateBrandDto.getNameKor())).willReturn(false);
        given(brandRepository.existsByNameEng(updateBrandDto.getNameEng())).willReturn(false);

        brandService.updateBrand(id, updateBrandDto, null);

        assertThat(savedBrand.getNameKor()).isEqualTo(updateBrandDto.getNameKor());
        assertThat(savedBrand.getNameEng()).isEqualTo(updateBrandDto.getNameEng());
    }

    @DisplayName("기존의 이미지 삭제에 성공한다.")
    @Test
    public void updateBrandWithDeleteImage() {
        Brand savedBrand = createBrand();
        SaveRequest updateBrandDto = createUpdateRequestWithoutImage();
        Long id = savedBrand.getId();
        String key = FileNameUtils.getFileName(savedBrand.getOriginImagePath());
        given(brandRepository.findById(id)).willReturn(Optional.of(savedBrand));
        given(brandRepository.existsByNameKor(updateBrandDto.getNameKor())).willReturn(false);
        given(brandRepository.existsByNameEng(updateBrandDto.getNameEng())).willReturn(false);

        brandService.updateBrand(id, updateBrandDto, null);

        assertThat(savedBrand.getNameKor()).isEqualTo(updateBrandDto.getNameKor());
        assertThat(savedBrand.getNameEng()).isEqualTo(updateBrandDto.getNameEng());
        assertThat(savedBrand.getOriginImagePath()).isNull();
        assertThat(savedBrand.getThumbnailImagePath()).isNull();
        verify(awsS3Service, times(1)).deleteBrandImage(key);
    }

    @DisplayName("기존 브랜드는 이미지가 없으며, 새로운 이미지로 업데이트에 성공한다.")
    @Test
    public void updateBrandWithAddImage() {
        Brand savedBrand = createBrandWithoutImage();
        SaveRequest updateBrandDto = createUpdateRequestWithoutImage();
        MultipartFile file = createImageFile();
        Long id = savedBrand.getId();
        given(brandRepository.findById(id)).willReturn(Optional.of(savedBrand));
        given(brandRepository.existsByNameKor(updateBrandDto.getNameKor())).willReturn(false);
        given(brandRepository.existsByNameEng(updateBrandDto.getNameEng())).willReturn(false);
        given(awsS3Service.uploadBrandImage(file)).willReturn(originImagePath);

        brandService.updateBrand(id, updateBrandDto, file);

        assertThat(savedBrand.getNameKor()).isEqualTo(updateBrandDto.getNameKor());
        assertThat(savedBrand.getNameEng()).isEqualTo(updateBrandDto.getNameEng());
        assertThat(savedBrand.getOriginImagePath()).isEqualTo(originImagePath);
        assertThat(savedBrand.getThumbnailImagePath()).isEqualTo(thumbnailImagePath);
        verify(awsS3Service, times(1)).uploadBrandImage(file);
    }

    @DisplayName("기존 브랜드의 이미지를 삭제하고, 새로운 이미지로 업데이트에 성공한다.")
    @Test
    public void updateBrandWithUpdateImage() {
        Brand savedBrand = createBrand();
        SaveRequest updateBrandDto = createUpdateRequest();
        MultipartFile file = createImageFile();
        Long id = savedBrand.getId();
        String key = FileNameUtils.getFileName(savedBrand.getOriginImagePath());
        given(brandRepository.findById(id)).willReturn(Optional.of(savedBrand));
        given(brandRepository.existsByNameKor(updateBrandDto.getNameKor())).willReturn(false);
        given(brandRepository.existsByNameEng(updateBrandDto.getNameEng())).willReturn(false);
        given(awsS3Service.uploadBrandImage(file)).willReturn(changedOriginImagePath);

        brandService.updateBrand(id, updateBrandDto, file);

        assertThat(savedBrand.getNameKor()).isEqualTo(updateBrandDto.getNameKor());
        assertThat(savedBrand.getNameEng()).isEqualTo(updateBrandDto.getNameEng());
        assertThat(savedBrand.getOriginImagePath()).isEqualTo(changedOriginImagePath);
        assertThat(savedBrand.getThumbnailImagePath()).isEqualTo(changedThumbnailImagePath);
        verify(awsS3Service, times(1)).deleteBrandImage(key);
        verify(awsS3Service, times(1)).uploadBrandImage(file);
    }

    @DisplayName("브랜드가 존재하지 않아서 업데이트에 실패한다.")
    @Test
    public void failToUpdateBrandIfBrandNotExist() {
        SaveRequest updateBrandDto = createUpdateRequestWithoutImage();
        Long id = 1L;
        given(brandRepository.findById(id)).willReturn(Optional.empty());

        assertThrows(BrandNotFoundException.class,
            () -> brandService.updateBrand(id, updateBrandDto, null));
    }

    @DisplayName("브랜드 한글명 중복으로 업데이트에 실패한다.")
    @Test
    public void failToUpdateBrandIfDuplicateNameKor() {
        Brand savedBrand = createBrandWithoutImage();
        SaveRequest updateBrandDto = createUpdateRequestWithoutImage();
        Long id = savedBrand.getId();
        given(brandRepository.findById(id)).willReturn(Optional.of(savedBrand));
        given(brandRepository.existsByNameKor(updateBrandDto.getNameKor())).willReturn(true);

        assertThrows(DuplicateBrandNameException.class,
            () -> brandService.updateBrand(id, updateBrandDto, null));
    }

    @DisplayName("브랜드 영문명 중복으로 업데이트에 실패한다.")
    @Test
    public void failToUpdateBrandIfDuplicateNameEng() {
        Brand savedBrand = createBrandWithoutImage();
        SaveRequest updateBrandDto = createUpdateRequestWithoutImage();
        Long id = savedBrand.getId();
        given(brandRepository.findById(id)).willReturn(Optional.of(savedBrand));
        given(brandRepository.existsByNameKor(updateBrandDto.getNameKor())).willReturn(false);
        given(brandRepository.existsByNameEng(updateBrandDto.getNameEng())).willReturn(true);

        assertThrows(DuplicateBrandNameException.class,
            () -> brandService.updateBrand(id, updateBrandDto, null));
    }
}