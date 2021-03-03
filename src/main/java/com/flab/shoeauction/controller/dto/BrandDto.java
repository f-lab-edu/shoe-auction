package com.flab.shoeauction.controller.dto;

import com.flab.shoeauction.domain.brand.Brand;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BrandDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaveRequest {

        @NotBlank(message = "브랜드 한글명을 입력해주세요.")
        private String nameKor;
        @NotBlank(message = "브랜드 영문명을 입력해주세요.")
        private String nameEng;

        private String imagePath;

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }

        public Brand toEntity() {
            return Brand.builder()
                .nameKor(this.nameKor)
                .nameEng(this.nameEng)
                .imagePath(this.imagePath)
                .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BrandInfo {

        private Long id;
        private String nameKor;
        private String nameEng;
        private String imagePath;

        public Brand toEntity() {
            return Brand.builder()
                .id(this.id)
                .nameKor(this.nameKor)
                .nameEng(this.nameEng)
                .imagePath(this.imagePath)
                .build();
        }
    }
}
