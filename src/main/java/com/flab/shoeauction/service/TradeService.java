package com.flab.shoeauction.service;

import com.flab.shoeauction.controller.dto.ProductDto.ProductInfoByTrade;
import com.flab.shoeauction.controller.dto.TradeDto.ImmediateTradeRequest;
import com.flab.shoeauction.controller.dto.TradeDto.SaveRequest;
import com.flab.shoeauction.controller.dto.TradeDto.TradeResource;
import com.flab.shoeauction.controller.dto.TradeDto.ChangeRequest;
import com.flab.shoeauction.controller.dto.UserDto.TradeUserInfo;
import com.flab.shoeauction.domain.addressBook.Address;
import com.flab.shoeauction.domain.addressBook.AddressRepository;
import com.flab.shoeauction.domain.product.Product;
import com.flab.shoeauction.domain.product.ProductRepository;
import com.flab.shoeauction.domain.trade.Trade;
import com.flab.shoeauction.domain.trade.TradeRepository;
import com.flab.shoeauction.domain.users.user.User;
import com.flab.shoeauction.domain.users.user.UserRepository;
import com.flab.shoeauction.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final TradeRepository tradeRepository;
    private final AddressRepository addressRepository;

    public TradeResource getResourceForBid(String email, Long productId, double size) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        Product product = productRepository.findById(productId)
            .orElseThrow();

        return makeTradeResource(user, product, size);

    }

    private TradeResource makeTradeResource(User user, Product product, double size) {
        ProductInfoByTrade productInfoByTrade = product.toProductInfoByTrade(user, size);
        TradeUserInfo tradeUserInfo = user.createTradeUserInfo();

        return TradeResource.builder()
            .tradeUserInfo(tradeUserInfo)
            .productInfoByTrade(productInfoByTrade)
            .build();
    }

    @Transactional
    @CacheEvict(value = "product", key = "#requestDto.productId")
    public void createSalesBid(String email, SaveRequest requestDto) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));
        Product product = productRepository.findById(requestDto.getProductId()).orElseThrow();
        Address address = user.findAddress(requestDto.getAddressId());

        Trade trade = requestDto.toEntityBySeller(user, product, address);

        tradeRepository.save(trade);
    }

    @Transactional
    @CacheEvict(value = "product", key = "#requestDto.productId")
    public void createPurchaseBid(String email, SaveRequest requestDto) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));
        Product product = productRepository.findById(requestDto.getProductId()).orElseThrow();
        Address address = user.findAddress(requestDto.getAddressId());

        Trade trade = requestDto.toEntityByBuyer(user, product, address);

        tradeRepository.save(trade);
    }

    @Transactional
    @CacheEvict(value = "product", key = "#requestDto.productId")
    public void immediatePurchase(String email, ImmediateTradeRequest requestDto) {
        User buyer = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        Address shippingAddress = addressRepository.findById(requestDto.getAddressId())
            .orElseThrow();

        Trade trade = tradeRepository.findById(requestDto.getTradeId()).orElseThrow();

        trade.makeImmediatePurchase(buyer, shippingAddress);
    }

    @Transactional
    @CacheEvict(value = "product", key = "#requestDto.productId")
    public void immediateSale(String email, ImmediateTradeRequest requestDto) {
        User seller = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        Address returnAddress = addressRepository.findById(requestDto.getAddressId()).orElseThrow();

        Trade trade = tradeRepository.findById(requestDto.getTradeId()).orElseThrow();

        trade.makeImmediateSale(seller, returnAddress);
    }

    @Transactional
    public void updateTrade(ChangeRequest requestDto) {
        Trade trade = tradeRepository.findById(requestDto.getTradeId()).orElseThrow();
        trade.updatePrice(requestDto.getPrice());
    }

    @Transactional
    public void deleteTrade(ChangeRequest requestDto) {
        tradeRepository.deleteById(requestDto.getTradeId());
    }
}