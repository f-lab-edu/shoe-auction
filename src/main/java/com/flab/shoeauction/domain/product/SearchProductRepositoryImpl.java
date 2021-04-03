package com.flab.shoeauction.domain.product;

import static com.flab.shoeauction.domain.product.QProduct.product;
import static com.flab.shoeauction.domain.trade.QTrade.trade;
import static org.springframework.util.StringUtils.hasText;

import com.flab.shoeauction.controller.dto.ProductDto.SearchCondition;
import com.flab.shoeauction.controller.dto.ProductDto.ThumbnailResponse;
import com.flab.shoeauction.domain.product.common.OrderStandard;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class SearchProductRepositoryImpl implements SearchProductRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<ThumbnailResponse> findAllBySearchCondition(
        SearchCondition condition, Pageable pageable) {
        QueryResults<ThumbnailResponse> results = jpaQueryFactory
            .select(Projections.fields(ThumbnailResponse.class,
                product.id,
                product.thumbnailImagePath.as("productThumbnailImagePath"),
                product.brand.thumbnailImagePath.as("brandThumbnailImagePath"),
                product.nameKor,
                product.nameEng,
                trade.price.min().as("lowestPrice")
                ))
            .from(product)
            .leftJoin(product.trades, trade)
            .groupBy(product)
            .where(
                eqBrandId(condition.getBrandId()),
                containsKeyword(condition.getKeyword()),
                trade.buyer.isNull()
            ).orderBy(
                getOrderSpecifier(condition.getOrderStandard())
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetchResults();

        List<ThumbnailResponse> products = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(products, pageable, total);
    }

    private BooleanExpression eqBrandId(Long brandId) {
        return (brandId != null) ? product.brand.id.eq(brandId) : null;
    }

    private BooleanExpression containsKeyword(String keyword) {
        return (hasText(keyword)) ? product.nameKor.containsIgnoreCase(keyword)
            .or(product.nameEng.containsIgnoreCase(keyword))
            .or(product.modelNumber.containsIgnoreCase(keyword)) : null;
    }

    private OrderSpecifier getOrderSpecifier(OrderStandard orderStandard) {
        OrderSpecifier orderSpecifier = null;

        if (orderStandard == null) {
            orderSpecifier = new OrderSpecifier(Order.DESC, product.releaseDate);
        } else {
            switch (orderStandard) {
                case LOW_PRICE:
                    orderSpecifier = new OrderSpecifier(Order.ASC, product.releasePrice);
                    break;
                case HIGH_PRICE:
                    orderSpecifier = new OrderSpecifier(Order.DESC, product.releasePrice);
                    break;
                case RELEASE_DATE:
                    orderSpecifier = new OrderSpecifier(Order.DESC, product.releaseDate);
                    break;
                default:
                    orderSpecifier = new OrderSpecifier(Order.DESC, product.releaseDate);
            }
        }

        return orderSpecifier;
    }
}
