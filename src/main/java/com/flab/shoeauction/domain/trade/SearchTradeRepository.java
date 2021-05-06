package com.flab.shoeauction.domain.trade;

import com.flab.shoeauction.controller.dto.UserDto.TradeInfoResponse;
import com.flab.shoeauction.controller.dto.UserDto.TradeSearchCondition;
import com.flab.shoeauction.domain.users.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchTradeRepository {

    boolean existsProgressingByUser(User user);

    Page<TradeInfoResponse> searchByTradeStatusAndTradeId(TradeSearchCondition searchCondition, Pageable pageable);
}
