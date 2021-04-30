package com.flab.shoeauction.domain.trade;

import static com.flab.shoeauction.domain.trade.QTrade.trade;

import com.flab.shoeauction.domain.users.user.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

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
}
