package com.flab.shoeauction.domain.trade;

import static com.flab.shoeauction.domain.trade.QTrade.trade;

import com.flab.shoeauction.controller.dto.UserDto.TradeInfoResponse;
import com.flab.shoeauction.controller.dto.UserDto.TradeSearchCondition;
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
        QueryResults<TradeInfoResponse> results = jpaQueryFactory
            .select(Projections.fields(TradeInfoResponse.class,
                trade.id,
                trade.status,
                trade.seller.email))
            .from(trade)
            .where(
                tradeStatusEq(searchRequest.getTradeStatus()),
                tradeIdEq(searchRequest.getTradeId())
            ).offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetchResults();

        List<TradeInfoResponse> infoResponseList = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(infoResponseList, pageable, total);
    }

    private BooleanExpression tradeStatusEq(TradeStatus tradeStatus) {
        return tradeStatus != null ? trade.status.eq(tradeStatus) : null;
    }

    private BooleanExpression tradeIdEq(Long tradeId) {
        return tradeId != null ? trade.id.eq(tradeId) : null;
    }
}