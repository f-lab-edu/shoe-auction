package com.flab.shoeauction.domain.trade;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<Trade, Long>, SearchTradeRepository {

}