package com.flab.shoeauction.service;

import static com.flab.shoeauction.domain.trade.TradeStatus.PRE_INSPECTION;
import static com.flab.shoeauction.domain.trade.TradeStatus.PRE_SHIPMENT;
import static com.flab.shoeauction.domain.trade.TradeStatus.PRE_WAREHOUSING;

import com.flab.shoeauction.controller.dto.ProductDto.ProductInfoByTrade;
import com.flab.shoeauction.controller.dto.TradeDto.ChangeRequest;
import com.flab.shoeauction.controller.dto.TradeDto.ImmediateTradeRequest;
import com.flab.shoeauction.controller.dto.TradeDto.SaveRequest;
import com.flab.shoeauction.controller.dto.TradeDto.TradeInfoResponse;
import com.flab.shoeauction.controller.dto.TradeDto.TradeResource;
import com.flab.shoeauction.controller.dto.TradeDto.TradeSearchCondition;
import com.flab.shoeauction.controller.dto.UserDto.TradeUserInfo;
import com.flab.shoeauction.domain.addressBook.Address;
import com.flab.shoeauction.domain.addressBook.AddressRepository;
import com.flab.shoeauction.domain.product.Product;
import com.flab.shoeauction.domain.product.ProductRepository;
import com.flab.shoeauction.domain.trade.Trade;
import com.flab.shoeauction.domain.trade.TradeRepository;
import com.flab.shoeauction.domain.users.user.User;
import com.flab.shoeauction.domain.users.user.UserRepository;
import com.flab.shoeauction.exception.user.NotAuthorizedException;
import com.flab.shoeauction.exception.user.UserNotFoundException;
import com.flab.shoeauction.service.message.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final TradeRepository tradeRepository;
    private final AddressRepository addressRepository;
    private final MessageService fcmService;
    private final PointService pointService;

    @Transactional(readOnly = true)
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

        user.pointInspection(requestDto.getPrice());

        Product product = productRepository.findById(requestDto.getProductId()).orElseThrow();
        Address address = user.findAddress(requestDto.getAddressId());

        Trade trade = requestDto.toEntityByBuyer(user, product, address);

        tradeRepository.save(trade);

        user.deductionOfPoints(requestDto.getPrice());
        pointService.purchasePointPayment(user, trade.getPrice());
    }

    @Transactional
    @CacheEvict(value = "product", key = "#requestDto.productId")
    public void immediatePurchase(String email, ImmediateTradeRequest requestDto) {
        User buyer = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        Trade trade = tradeRepository.findById(requestDto.getTradeId()).orElseThrow();

        buyer.pointInspection(trade.getPrice());

        Address shippingAddress = addressRepository.findById(requestDto.getAddressId())
            .orElseThrow();

        trade.makeImmediatePurchase(buyer, shippingAddress);
        buyer.deductionOfPoints(trade.getPrice());
        pointService.purchasePointPayment(buyer, trade.getPrice());

        fcmService.sendSaleCompletedMessage(trade.getPublisher().getEmail());
    }

    @Transactional
    @CacheEvict(value = "product", key = "#requestDto.productId")
    public void immediateSale(String email, ImmediateTradeRequest requestDto) {
        User seller = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        Address returnAddress = addressRepository.findById(requestDto.getAddressId()).orElseThrow();

        Trade trade = tradeRepository.findById(requestDto.getTradeId()).orElseThrow();

        trade.makeImmediateSale(seller, returnAddress);

        fcmService.sendPurchaseCompletedMessage(trade.getPublisher().getEmail());
    }

    @Transactional
    public void deleteTrade(ChangeRequest requestDto) {
        Trade trade = tradeRepository.findById(requestDto.getTradeId()).orElseThrow();
        if (trade.isPurchaseBid()) {
            trade.recoverBuyerPoints(trade.getPrice());
            pointService.purchasePointReturn(trade.getBuyer(), trade.getPrice());
        }

        tradeRepository.deleteById(trade.getId());
    }

    // 판매자가 회사에 상품 발송 후 운송장 번호를 입력 시 입고 대기로 상태 변경
    @Transactional
    public void updateReceivingTrackingNumber(Long tradeId, String email, String trackingNumber) {
        Trade trade = tradeRepository.findById(tradeId).orElseThrow();

        if (!trade.isSellersEmail(email)) {
            throw new NotAuthorizedException("해당 거래의 판매자만 접근 가능합니다.");
        }

        trade.updateReceivingTrackingNumber(trackingNumber);
        trade.updateStatus(PRE_WAREHOUSING);
    }

    // 관리자가 상품의 입고를 확인하고 상품을 입고 확인 처리(검수 대기로 변경)
    @Transactional
    public void confirmWarehousing(Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId).orElseThrow();

        trade.updateStatus(PRE_INSPECTION);
    }

    @Transactional
    public void inspectionSuccessful(Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId).orElseThrow();

        trade.updateStatus(PRE_SHIPMENT);
    }

    @Transactional
    public void inspectionFailed(Long tradeId, String reason) {
        Trade trade = tradeRepository.findById(tradeId).orElseThrow();

        trade.cancelBecauseOfInspection(reason);

        pointService.purchasePointReturn(trade.getBuyer(), trade.getPrice());
    }

    @Transactional
    public void updateReturnTrackingNumber(Long tradeId, String trackingNumber) {
        Trade trade = tradeRepository.findById(tradeId).orElseThrow();

        trade.updateReturnTrackingNumber(trackingNumber);
    }

    @Transactional
    public boolean hasUsersProgressingTrade(User user) {
        return tradeRepository.existsProgressingByUser(user);
    }

    @Transactional(readOnly = true)
    public Page<TradeInfoResponse> getTradeInfos(TradeSearchCondition tradeSearchCondition,
        Pageable pageable) {
        return tradeRepository.searchByTradeStatusAndTradeId(tradeSearchCondition, pageable);
    }

    @Transactional
    public void updateForwardingTrackingNumber(Long tradeId, String trackingNumber) {
        Trade trade = tradeRepository.findById(tradeId).orElseThrow();

        trade.updateStatusShipping(trackingNumber);
    }

    @Transactional
    public void confirmPurchase(Long tradeId, String email) {
        Trade trade = tradeRepository.findById(tradeId).orElseThrow();

        if (!trade.isBuyersEmail(email)) {
            throw new NotAuthorizedException("해당 거래의 구매자만 접근 가능합니다.");
        }

        trade.endTrade();

        pointService.salesPointReceive(trade.getSeller(), trade.getPrice());
    }
}