package com.flab.shoeauction.domain.trade;

import static com.flab.shoeauction.domain.trade.QTrade.trade;

import com.flab.shoeauction.controller.dto.TradeDto.TradeInfoResponse;
import com.flab.shoeauction.controller.dto.TradeDto.TradeSearchCondition;
import com.flab.shoeauction.domain.users.user.User;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class SearchTradeRepositoryImpl implements SearchTradeRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsProgressingByUser(User user) {
        Integer result = jpaQueryFactory
            .selectOne()
            .from(trade)
            .where(isUsersTrade(user), isProgressing())
            .fetchFirst();

        return result != null;
    }

    private BooleanExpression isUsersTrade(User user) {
        return trade.buyer.eq(user)
            .or(trade.seller.eq(user));
    }

    private BooleanExpression isProgressing() {
        return trade.status.ne(TradeStatus.CANCEL)
            .and(trade.status.ne(TradeStatus.TRADE_COMPLETE));
    }

    @Override
    public Page<TradeInfoResponse> searchByTradeStatusAndTradeId(TradeSearchCondition searchRequest,
        Pageable pageable) {

        if (searchRequest.isSearchBySeller()) {
            return searchBySellerEmail(searchRequest.getSeller(), pageable);
        }

        if (searchRequest.isSearchByBuyer()) {
            return searchByBuyerEmail(searchRequest.getBuyer(), pageable);
        }

        return searchByTradeId(searchRequest.getTradeId(), pageable);

    }

    private Page<TradeInfoResponse> searchByTradeId(Long tradeId, Pageable pageable) {
        QueryResults<TradeInfoResponse> results = jpaQueryFactory
            .select(Projections.fields(TradeInfoResponse.class,
                trade.id,
                trade.status))
            .from(trade)
            .where(
                tradeIdEq(tradeId)
            ).offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetchResults();
        List<TradeInfoResponse> infoResponseList = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(infoResponseList, pageable, total);
    }

    private Page<TradeInfoResponse> searchByBuyerEmail(String buyerEmail, Pageable pageable) {
        QueryResults<TradeInfoResponse> results = jpaQueryFactory
            .select(Projections.fields(TradeInfoResponse.class,
                trade.id,
                trade.status))
            .from(trade)
            .innerJoin(trade.buyer)
            .where(
                tradeBuyerEq(buyerEmail)
            ).offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetchResults();
        List<TradeInfoResponse> infoResponseList = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(infoResponseList, pageable, total);
    }

    private Page<TradeInfoResponse> searchBySellerEmail(String sellerEmail, Pageable pageable) {
        QueryResults<TradeInfoResponse> results = jpaQueryFactory
            .select(Projections.fields(TradeInfoResponse.class,
                trade.id,
                trade.status))
            .from(trade)
            .innerJoin(trade.seller)
            .where(
                tradeSellerEq(sellerEmail)
            ).offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetchResults();
        List<TradeInfoResponse> infoResponseList = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(infoResponseList, pageable, total);
    }

    private BooleanExpression tradeSellerEq(String sellerEmail) {
        return sellerEmail != null ? trade.seller.email.eq(sellerEmail) : null;
    }

    private BooleanExpression tradeBuyerEq(String buyerEmail) {
        return buyerEmail != null ? trade.buyer.email.eq(buyerEmail) : null;
    }

    private BooleanExpression tradeIdEq(Long tradeId) {
        return tradeId != null ? trade.id.eq(tradeId) : null;
    }
}