package com.flab.shoeauction.service;

import com.flab.shoeauction.controller.dto.ProductDto.ProductInfo;
import com.flab.shoeauction.controller.dto.TradeDto.SaveRequest;
import com.flab.shoeauction.controller.dto.TradeDto.TradeResource;
import com.flab.shoeauction.controller.dto.UserDto.TradeUserInfo;
import com.flab.shoeauction.domain.addressBook.Address;
import com.flab.shoeauction.domain.product.Product;
import com.flab.shoeauction.domain.product.ProductRepository;
import com.flab.shoeauction.domain.trade.Trade;
import com.flab.shoeauction.domain.trade.TradeRepository;
import com.flab.shoeauction.domain.users.user.User;
import com.flab.shoeauction.domain.users.user.UserRepository;
import com.flab.shoeauction.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final TradeRepository tradeRepository;

    public TradeResource getSellResource(String email, Long productId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        Product product = productRepository.findById(productId)
            .orElseThrow();

        return makeSellResource(user, product);

    }

    private TradeResource makeSellResource(User user, Product product) {
        ProductInfo productInfo = product.toProductInfoResponse();
        TradeUserInfo tradeUserInfo = user.createTradeUserInfo();

        return TradeResource.builder()
            .tradeUserInfo(tradeUserInfo)
            .productInfo(productInfo)
            .build();

    }

    @Transactional
    public void createSalesBid(String email, SaveRequest requestDto) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));
        Product product = productRepository.findById(requestDto.getProductId()).orElseThrow();
        Address address = user.findAddress(requestDto.getAddressId());

        Trade trade = requestDto.toEntityBySeller(user, product, address);

        tradeRepository.save(trade);
    }

    @Transactional
    public void createPurchaseBid(String email, SaveRequest requestDto) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));
        Product product = productRepository.findById(requestDto.getProductId()).orElseThrow();
        Address address = user.findAddress(requestDto.getAddressId());

        Trade trade = requestDto.toEntityByBuyer(user, product, address);

        tradeRepository.save(trade);
    }
}