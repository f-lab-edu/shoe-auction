package com.flab.shoeauction.domain.trade;

import com.flab.shoeauction.controller.dto.TradeDto.DateSearchCondition;
import com.flab.shoeauction.controller.dto.TradeDto.TradeInfoResponse;
import com.flab.shoeauction.controller.dto.TradeDto.TradeSearchCondition;
import com.flab.shoeauction.controller.dto.TradeDto.TradeVolumeResponse;
import com.flab.shoeauction.domain.users.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchTradeRepository {

    boolean existsProgressingByUser(User user);

    Page<TradeInfoResponse> searchByTradeStatusAndTradeId(TradeSearchCondition searchCondition,
        Pageable pageable);
}
