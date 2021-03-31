package com.flab.shoeauction.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.flab.shoeauction.common.utils.file.FileNameUtils;
import com.flab.shoeauction.controller.dto.ProductDto.ProductInfoResponse;
import com.flab.shoeauction.controller.dto.ProductDto.SaveRequest;
import com.flab.shoeauction.domain.brand.Brand;
import com.flab.shoeauction.domain.product.Currency;
import com.flab.shoeauction.domain.product.Product;
import com.flab.shoeauction.domain.product.ProductRepository;
import com.flab.shoeauction.domain.product.SizeClassification;
import com.flab.shoeauction.domain.product.SizeUnit;
import com.flab.shoeauction.exception.file.ImageRoadFailedException;
import com.flab.shoeauction.exception.product.DuplicateModelNumberException;
import com.flab.shoeauction.exception.product.ProductNotFoundException;
import com.flab.shoeauction.service.storage.AwsS3Service;
import java.time.LocalDate;
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
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    AwsS3Service awsS3Service;

    @InjectMocks
    ProductService productService;

    private String brandOriginImagePath = "https://shoeauction-brands-origin.s3.ap-northeast-2.amazonaws.com/brand.png";
    private String brandThumbnailImagePath = "https://shoeauction-brands-thumbnail.s3.ap-northeast-2.amazonaws.com/brand.png";
    private String productOriginImagePath = "https://shoeauction-brands-origin.s3.ap-northeast-2.amazonaws.com/brand.png";
    private String productThumbnailImagePath = "https://shoeauction-brands-thumbnail.s3.ap-northeast-2.amazonaws.com/brand.png";
    private String productResizedImagePath = "https://shoeauction-brands-resized.s3.ap-northeast-2.amazonaws.com/brand.png";

    private Brand createBrand() {
        return Brand.builder()
            .nameKor("나이키")
            .nameEng("Nike")
            .originImagePath(brandOriginImagePath)
            .thumbnailImagePath(brandThumbnailImagePath)
            .build();
    }

    private Brand createAnotherBrand() {
        return Brand.builder()
            .nameKor("뉴발란스")
            .nameEng("New Balance")
            .originImagePath(brandOriginImagePath.replace("brand", "brand2"))
            .thumbnailImagePath(brandThumbnailImagePath.replace("brand", "brand2"))
            .build();
    }

    private Product createProduct() {
        return Product.builder()
            .nameKor("덩크 로우")
            .nameEng("Dunk Low")
            .modelNumber("DD1391-100")
            .color("WHITE/BLACK")
            .releaseDate(LocalDate.of(2021, 01, 04))
            .releasePrice(119000)
            .currency(Currency.KRW)
            .sizeClassification(SizeClassification.MENS)
            .sizeUnit(SizeUnit.MM)
            .minSize(240)
            .maxSize(320)
            .sizeGap(5)
            .brand(createBrand())
            .originImagePath(productOriginImagePath)
            .thumbnailImagePath(productThumbnailImagePath)
            .resizedImagePath(productResizedImagePath)
            .build();
    }

    private Product createAnotherProduct() {
        return Product.builder()
            .nameKor("992 메이드 인 USA")
            .nameEng("992 Made in USA")
            .modelNumber("M992GR")
            .color("GREY")
            .releaseDate(LocalDate.of(2020, 04, 13))
            .releasePrice(259000)
            .currency(Currency.KRW)
            .sizeClassification(SizeClassification.MENS)
            .sizeUnit(SizeUnit.MM)
            .minSize(215)
            .maxSize(320)
            .sizeGap(5)
            .brand(createAnotherBrand())
            .originImagePath(productOriginImagePath.replace("product", "product1"))
            .thumbnailImagePath(productThumbnailImagePath.replace("product", "product1"))
            .resizedImagePath(productResizedImagePath.replace("product", "product1"))
            .build();
    }

    private SaveRequest createSaveRequest() {
        return SaveRequest.builder()
            .nameKor("덩크 로우")
            .nameEng("Dunk Low")
            .modelNumber("DD1391-100")
            .color("WHITE/BLACK")
            .releaseDate(LocalDate.of(2021, 01, 04))
            .releasePrice(119000)
            .currency(Currency.KRW)
            .sizeClassification(SizeClassification.MENS)
            .sizeUnit(SizeUnit.MM)
            .minSize(240)
            .maxSize(320)
            .sizeGap(5)
            .brand(createBrand().toBrandInfo())
            .build();
    }

    private MultipartFile createImageFile() {
        return new MockMultipartFile("sample", "sample.png", MediaType.IMAGE_PNG_VALUE,
            "sample".getBytes());
    }

    private List<Product> createProductList() {
        List<Product> productList = new ArrayList<>();
        productList.add(createProduct());
        productList.add(createAnotherProduct());

        return productList;
    }

    @DisplayName("특정 id를 가진 상품이 존재하여 조회에 성공한다.")
    @Test
    public void getProductInfo() {
        Product product = createProduct();
        Long id = product.getId();
        given(productRepository.findById(id)).willReturn(java.util.Optional.of(product));

        ProductInfoResponse productInfo = productService.getProductInfo(id);

        assertThat(productInfo.getId()).isEqualTo(product.getId());
        assertThat(productInfo.getNameKor()).isEqualTo(product.getNameKor());
        assertThat(productInfo.getNameEng()).isEqualTo(product.getNameEng());
        assertThat(productInfo.getModelNumber()).isEqualTo(product.getModelNumber());
        assertThat(productInfo.getColor()).isEqualTo(product.getColor());
        assertThat(productInfo.getReleaseDate()).isEqualTo(product.getReleaseDate());
        assertThat(productInfo.getReleasePrice()).isEqualTo(product.getReleasePrice());
        assertThat(productInfo.getCurrency()).isEqualTo(product.getCurrency());
        assertThat(productInfo.getSizeClassification()).isEqualTo(product.getSizeClassification());
        assertThat(productInfo.getSizeUnit()).isEqualTo(product.getSizeUnit());
        assertThat(productInfo.getMinSize()).isEqualTo(product.getMinSize());
        assertThat(productInfo.getMaxSize()).isEqualTo(product.getMaxSize());
        assertThat(productInfo.getSizeGap()).isEqualTo(product.getSizeGap());
        assertThat(productInfo.getBrand().getId()).isEqualTo(product.getBrand().getId());
        assertThat(productInfo.getResizedImagePath()).isEqualTo(product.getResizedImagePath());
        verify(productRepository, times(1)).findById(id);
    }


    @DisplayName("특정 id를 가진 상품이 존재하지 않아 조회에 실패한다.")
    @Test
    public void failToGetProductInfoIfProductNotExist() {
        Long id = 1L;
        given(productRepository.findById(id)).willReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductInfo(id));
        verify(productRepository, times(1)).findById(id);
    }

    @DisplayName("이미지 없이 상품 저장에 성공한다.")
    @Test
    public void saveProductWithoutImage() {
        SaveRequest product = createSaveRequest();

        productService.saveProduct(product, null);

        verify(productRepository, times(1)).save(any());
    }

    @DisplayName("상품 모델명 중복으로 인해 상품 저장에 실패한다.")
    @Test
    public void failToSaveProductIfDuplicateModelNumber() {
        SaveRequest product = createSaveRequest();
        given(productRepository.existsByModelNumber(product.getModelNumber())).willReturn(true);

        assertThrows(DuplicateModelNumberException.class,
            () -> productService.saveProduct(product, null));
        verify(productRepository, never()).save(any());
    }

    @DisplayName("이미지와 함께 상품 저장에 성공한다.")
    @Test
    public void saveProductWithImage() {
        SaveRequest product = createSaveRequest();
        MultipartFile file = createImageFile();
        given(awsS3Service.uploadProductImage(file)).willReturn(productOriginImagePath);

        productService.saveProduct(product, file);

        assertThat(product.getOriginImagePath()).isEqualTo(productOriginImagePath);
        assertThat(product.getThumbnailImagePath()).isEqualTo(productThumbnailImagePath);
        assertThat(product.getResizedImagePath()).isEqualTo(productResizedImagePath);
        verify(awsS3Service, times(1)).uploadProductImage(file);
        verify(productRepository, times(1)).save(any());
    }

    @DisplayName("이미지 업로드 실패로 인해 상품 저장에 실패한다.")
    @Test
    public void failToSaveProductIfImageUploadFailed() {
        SaveRequest product = createSaveRequest();
        MultipartFile file = createImageFile();
        given(awsS3Service.uploadProductImage(file)).willThrow(ImageRoadFailedException.class);

        assertThrows(ImageRoadFailedException.class,
            () -> productService.saveProduct(product, file));

        assertThat(product.getOriginImagePath()).isNull();
        assertThat(product.getThumbnailImagePath()).isNull();
        assertThat(product.getResizedImagePath()).isNull();
        verify(productRepository, never()).save(any());
    }

    @DisplayName("이미지가 없는 상품 삭제에 성공한다.")
    @Test
    public void deleteProductWithoutImage() {
        Product product = createSaveRequest().toEntity();
        Long id = product.getId();
        given(productRepository.findById(id)).willReturn(java.util.Optional.of(product));

        productService.deleteProduct(id);

        verify(productRepository, times(1)).deleteById(id);
    }

    @DisplayName("상품이 존재하지 않아서 삭제에 실패한다.")
    @Test
    public void failToDeleteProductIfProductNotExist() {
        Long id = 1L;
        given(productRepository.findById(id)).willReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(id));
        verify(productRepository, never()).deleteById(id);
    }

    @DisplayName("이미지와 함께 상품 삭제에 성공한다.")
    @Test
    public void deleteProductWithImage() {
        Product product = createProduct();
        Long id = product.getId();
        given(productRepository.findById(id)).willReturn(java.util.Optional.of(product));

        productService.deleteProduct(id);

        verify(productRepository, times(1)).deleteById(id);
        verify(awsS3Service, times(1)).deleteProductImage(any());
    }

    @DisplayName("기존 상품은 이미지가 없으며, 새로운 이미지로 업데이트에 성공한다.")
    @Test
    public void updateProductWithAddImage() {
        Product savedProduct = createSaveRequest().toEntity();
        SaveRequest updateProductDto = createSaveRequest();
        MultipartFile productImage = createImageFile();
        Long id = savedProduct.getId();
        given(productRepository.findById(id)).willReturn(Optional.of(savedProduct));
        given(awsS3Service.uploadProductImage(productImage)).willReturn(productOriginImagePath);

        productService.updateProduct(id, updateProductDto, productImage);

        assertThat(savedProduct.getOriginImagePath()).isEqualTo(productOriginImagePath);
        assertThat(savedProduct.getThumbnailImagePath()).isEqualTo(productThumbnailImagePath);
        assertThat(savedProduct.getResizedImagePath()).isEqualTo(productResizedImagePath);
        verify(awsS3Service, times(1)).uploadProductImage(productImage);
    }

    @DisplayName("기존 상품의 이미지를 삭제하고, 새로운 이미지로 업데이트에 성공한다.")
    @Test
    public void updateProductWithUpdateImage() {
        Product savedProduct = createProduct();
        SaveRequest updateProductDto = createSaveRequest();
        MultipartFile productImage = createImageFile();
        Long id = savedProduct.getId();
        String key = FileNameUtils.getFileName(savedProduct.getOriginImagePath());
        String updatedProductImagePath = savedProduct.getOriginImagePath().replace(key, "updated");
        given(productRepository.findById(id)).willReturn(Optional.of(savedProduct));
        given(awsS3Service.uploadProductImage(productImage)).willReturn(updatedProductImagePath);

        productService.updateProduct(id, updateProductDto, productImage);

        assertThat(savedProduct.getOriginImagePath()).isEqualTo(updatedProductImagePath);
        assertThat(savedProduct.getThumbnailImagePath())
            .isEqualTo(updatedProductImagePath.replace("origin", "thumbnail"));
        assertThat(savedProduct.getResizedImagePath())
            .isEqualTo(updatedProductImagePath.replace("origin", "resized"));
        verify(awsS3Service, times(1)).deleteProductImage(key);
        verify(awsS3Service, times(1)).uploadProductImage(productImage);
    }

    @DisplayName("상품이 존재하지 않아서 업데이트에 실패한다.")
    @Test
    public void failToUpdateProductIfProductNotExist() {
        SaveRequest updateProductDto = createSaveRequest();
        Long id = 1L;
        given(productRepository.findById(id)).willReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
            () -> productService.updateProduct(id, updateProductDto, null));
    }

    @DisplayName("상품 모델번호 중복으로 업데이트에 실패한다.")
    public void failToUpdateProductIfDuplicateModelNumber() {
        Product savedProduct = createAnotherProduct();
        SaveRequest updateProductDto = createSaveRequest();
        Long id = savedProduct.getId();
        given(productRepository.existsByModelNumber(updateProductDto.getModelNumber()))
            .willReturn(true);

        assertThrows(DuplicateModelNumberException.class,
            () -> productService.updateProduct(id, updateProductDto, null));
    }
}