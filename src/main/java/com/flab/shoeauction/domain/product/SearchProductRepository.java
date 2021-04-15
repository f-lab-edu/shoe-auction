package com.flab.shoeauction.domain.product;

import com.flab.shoeauction.controller.dto.ProductDto.SearchCondition;
import com.flab.shoeauction.controller.dto.ProductDto.ThumbnailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchProductRepository {

    Page<ThumbnailResponse> findAllBySearchCondition(SearchCondition condition,
        Pageable pageable);
}