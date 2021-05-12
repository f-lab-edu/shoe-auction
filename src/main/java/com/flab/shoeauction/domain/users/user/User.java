package com.flab.shoeauction.domain.users.user;

import com.flab.shoeauction.controller.dto.PointDto.PointHistoryDto;
import com.flab.shoeauction.controller.dto.ProductDto.WishItemResponse;
import com.flab.shoeauction.controller.dto.UserDto.FindUserResponse;
import com.flab.shoeauction.controller.dto.UserDto.SaveRequest;
import com.flab.shoeauction.controller.dto.UserDto.TradeUserInfo;
import com.flab.shoeauction.controller.dto.UserDto.UserDetailsResponse;
import com.flab.shoeauction.controller.dto.UserDto.UserInfoDto;
import com.flab.shoeauction.domain.addressBook.Address;
import com.flab.shoeauction.domain.addressBook.AddressBook;
import com.flab.shoeauction.domain.cart.Cart;
import com.flab.shoeauction.domain.cart.CartProduct;
import com.flab.shoeauction.domain.point.Point;
import com.flab.shoeauction.domain.point.PointDivision;
import com.flab.shoeauction.domain.users.common.Account;
import com.flab.shoeauction.domain.users.common.UserBase;
import com.flab.shoeauction.domain.users.common.UserLevel;
import com.flab.shoeauction.domain.users.common.UserStatus;
import com.flab.shoeauction.exception.trade.LowPointException;
import com.flab.shoeauction.exception.user.UnableToChangeNicknameException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends UserBase {

    private String nickname;

    private String phone;

    @Embedded
    private Account account;

    private Long point;

    private LocalDateTime nicknameModifiedDate;

    private UserStatus userStatus;

    @OneToMany(mappedBy = "user")
    private List<Point> pointBreakdown = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "ADDRESSBOOK_ID")
    private AddressBook addressBook;

    /**
     * USER는 하나의 CART만 가질 수 있고, CART 또한 여러명의 유저가 함께 사용할 수 없다. 따라서 일대일 매핑으로 처리한다.
     */
    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "CART_ID")
    private Cart cart;

    public UserInfoDto toUserInfoDto() {
        return UserInfoDto.builder()
            .email(this.getEmail())
            .nickname(this.getNickname())
            .phone(this.getPhone())
            .userLevel(this.userLevel)
            .build();
    }

    public FindUserResponse toFindUserDto() {
        return FindUserResponse.builder()
            .email(this.getEmail())
            .phone(this.getPhone())
            .build();
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateAccount(Account account) {
        this.account = account;
    }

    public void addAddress(Address address) {
        this.addressBook.addAddress(address);
    }

    public void updateNickname(SaveRequest requestDto) {
        if (canModifiedNickname()) {
            throw new UnableToChangeNicknameException("닉네임은 7일에 한번만 변경할 수 있습니다.");
        }
        String nickname = requestDto.getNickname();
        this.nickname = nickname;
        this.nicknameModifiedDate = LocalDateTime.now();
    }

    private boolean canModifiedNickname() {
        return !(this.nicknameModifiedDate.isBefore(LocalDateTime.now().minusDays(7)));
    }

    public void updateUserLevel() {
        this.userLevel = UserLevel.AUTH;
    }



    @Builder
    public User(Long id, String email, String password, UserLevel userLevel, String nickname,
        String phone,
        LocalDateTime nicknameModifiedDate, AddressBook addressBook, UserStatus userStatus,
        Long point, List<Point> pointBreakdown) {
        super(id, email, password, userLevel);
        this.nickname = nickname;
        this.phone = phone;
        this.userLevel = userLevel;
        this.nicknameModifiedDate = nicknameModifiedDate;
        this.addressBook = addressBook;
        this.userStatus = userStatus;
        this.point = point;
        this.pointBreakdown = pointBreakdown;
    }

    public UserDetailsResponse toUserDetailsDto() {
        return UserDetailsResponse.builder()
            .id(this.getId())
            .email(this.email)
            .nickname(this.nickname)
            .phone(this.phone)
            .account(this.account)
            .createDate(this.getCreatedDate())
            .modifiedDate(this.getModifiedDate())
            .userLevel(this.userLevel)
            .userStatus(this.userStatus)
            .build();
    }

    public void createAddressBook(AddressBook addressBook) {
        this.addressBook = addressBook;
    }

    public void deleteAddress(Address address) {
        this.addressBook.deleteAddress(address);
    }

    public void updateUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public boolean isBan() {
        return this.userStatus == UserStatus.BAN;
    }

    public void createCart(Cart cart) {
        this.cart = cart;
    }

    public void addCartItem(CartProduct cartItem) {
        cart.addCartProducts(cartItem);
    }


    public Set<WishItemResponse> getWishList() {

        return cart.getWishList()
            .stream()
            .map(CartProduct::toWishItemDto)
            .collect(Collectors.toSet());
    }

    public boolean checkCartItemDuplicate(CartProduct cartItem) {
        return cart.getWishList()
            .stream()
            .map(CartProduct::getProduct)
            .anyMatch(v -> v.getId() == cartItem.getProductId());
    }

    public TradeUserInfo createTradeUserInfo() {

        List<Address> addressList = addressBook.getAddressList()
            .stream()
            .collect(Collectors.toList());

        return TradeUserInfo.builder()
            .account(this.account)
            .addressBook(addressList)
            .build();

    }

    public Address findAddress(Long addressId) {
        return addressBook.findAddress(addressId);
    }

    public void chargingPoint(Long chargeAmount) {
        this.point += chargeAmount;
    }

    public boolean isCurrentEmail(String email) {
        return this.email.equals(email);
    }

    public void pointInspection(Long price) {
        if (this.point < price) {
            throw new LowPointException("포인트 충전 후 이용해주세요.");
        }
    }

    public void deductionOfPoints(Long price) {
        this.point -= price;
    }


    public List<PointHistoryDto> getDeductionHistory() {
        return pointBreakdown.stream()
            .filter(p -> p.getDivision().equals(PointDivision.WITHDRAW) || p.getDivision()
                .equals(PointDivision.PURCHASE_DEDUCTION))
            .map(Point::toPointHistoryDto)
            .collect(Collectors.toList());
    }

    public List<PointHistoryDto> getChargingHistory() {
        return pointBreakdown.stream()
            .filter(p -> p.getDivision().equals(PointDivision.CHARGE) || p.getDivision()
                .equals(PointDivision.SALES_REVENUE))
            .map(Point::toPointHistoryDto)
            .collect(Collectors.toList());
    }


    public boolean hasRemainingPoints() {
        return point > 0;
    }

}