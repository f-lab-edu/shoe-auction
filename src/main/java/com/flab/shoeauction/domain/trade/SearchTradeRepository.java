package com.flab.shoeauction.domain.trade;

import com.flab.shoeauction.domain.users.user.User;

public interface SearchTradeRepository {

    boolean existsProgressingByUser(User user);
}
