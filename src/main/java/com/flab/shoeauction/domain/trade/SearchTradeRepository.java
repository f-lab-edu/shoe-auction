package com.flab.shoeauction.domain.trade;

import com.flab.shoeauction.controller.dto.TradeDto.MonthlyTradingVolumesResponse;
import com.flab.shoeauction.controller.dto.TradeDto.TradeInfoResponse;
import com.flab.shoeauction.controller.dto.TradeDto.TradeMonthSearchCondition;
import com.flab.shoeauction.controller.dto.TradeDto.TradeSearchCondition;
import com.flab.shoeauction.domain.users.user.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchTradeRepository {

    boolean existsProgressingByUser(User user);

    Page<TradeInfoResponse> searchByTradeStatusAndTradeId(TradeSearchCondition searchCondition,
        Pageable pageable);

    List<MonthlyTradingVolumesResponse> searchTradeVolumeByMonth(
        TradeMonthSearchCondition tradeMonthSearchCondition);
}
