package com.flab.shoeauction.service;

import com.flab.shoeauction.controller.dto.AddressBookDto;
import com.flab.shoeauction.controller.dto.ProductDto.IdRequest;
import com.flab.shoeauction.controller.dto.ProductDto.WishItemResponse;
import com.flab.shoeauction.controller.dto.UserDto.ChangePasswordRequest;
import com.flab.shoeauction.controller.dto.UserDto.FindUserResponse;
import com.flab.shoeauction.controller.dto.UserDto.SaveRequest;
import com.flab.shoeauction.domain.addressBook.Address;
import com.flab.shoeauction.domain.addressBook.AddressBook;
import com.flab.shoeauction.domain.addressBook.AddressBookRepository;
import com.flab.shoeauction.domain.cart.Cart;
import com.flab.shoeauction.domain.cart.CartProduct;
import com.flab.shoeauction.domain.cart.CartProductRepository;
import com.flab.shoeauction.domain.cart.CartRepository;
import com.flab.shoeauction.domain.product.Product;
import com.flab.shoeauction.domain.product.ProductRepository;
import com.flab.shoeauction.domain.users.common.Account;
import com.flab.shoeauction.domain.users.user.User;
import com.flab.shoeauction.domain.users.user.UserRepository;
import com.flab.shoeauction.exception.user.DuplicateCartItemException;
import com.flab.shoeauction.exception.user.DuplicateEmailException;
import com.flab.shoeauction.exception.user.DuplicateNicknameException;
import com.flab.shoeauction.exception.user.UnauthenticatedUserException;
import com.flab.shoeauction.exception.user.UserNotFoundException;
import com.flab.shoeauction.exception.user.WrongPasswordException;
import com.flab.shoeauction.service.certification.EmailCertificationService;
import com.flab.shoeauction.service.encrytion.EncryptionService;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final EncryptionService encryptionService;
    private final AddressBookRepository addressBookRepository;
    private final EmailCertificationService emailCertificationService;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;

    public boolean checkEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean checkNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public void save(SaveRequest requestDto) {
        if (checkEmailDuplicate(requestDto.getEmail())) {
            throw new DuplicateEmailException();
        }
        if (checkNicknameDuplicate(requestDto.getNickname())) {
            throw new DuplicateNicknameException();
        }
        requestDto.passwordEncryption(encryptionService);

        userRepository.save(requestDto.toEntity());
    }

    public FindUserResponse getUserResource(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 email 입니다.")).toFindUserDto();
    }

    @Transactional
    public void updatePasswordByForget(ChangePasswordRequest requestDto) {
        String email = requestDto.getEmail();
        requestDto.passwordEncryption(encryptionService);

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        user.updatePassword(requestDto.getPasswordAfter());
    }

    @Transactional
    public void updatePassword(String email, ChangePasswordRequest requestDto) {
        requestDto.passwordEncryption(encryptionService);
        String passwordBefore = requestDto.getPasswordBefore();
        String passwordAfter = requestDto.getPasswordAfter();
        if (!userRepository.existsByEmailAndPassword(email, passwordBefore)) {
            throw new UnauthenticatedUserException("이전 비밀번호가 일치하지 않습니다.");
        }

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        user.updatePassword(passwordAfter);
    }

    @Transactional
    public void updateAccount(String email, Account account) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));
        user.updateAccount(account);
    }

    public Account getAccount(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        return user.getAccount();

    }

    public List<AddressBook> getAddressBooks(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));
        return user.getAddressesBook();
    }

    @Transactional
    public void addAddressBook(String email, Address address) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        user.addAddressBook(address);
    }

    @Transactional
    public void deleteAddressBook(AddressBookDto requestDto) {
        Long addressBookId = requestDto.getId();
        addressBookRepository.deleteById(addressBookId);
    }

    @Transactional
    public void updateAddressBook(AddressBookDto requestDto) {
        Long addressBookId = requestDto.getId();
        AddressBook addressBook = addressBookRepository.findById(addressBookId).orElseThrow();
        addressBook.updateAddressBook(requestDto);
    }

    @Transactional
    public void updateNickname(String email, SaveRequest requestDto) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        if (checkNicknameDuplicate(requestDto.getNickname())) {
            throw new DuplicateNicknameException();
        }
        user.updateNickname(requestDto);
    }

    @Transactional
    public void delete(String email, String password) {
        if (!userRepository.existsByEmailAndPassword(email, encryptionService.encrypt(password))) {
            throw new WrongPasswordException();
        }
        userRepository.deleteByEmail(email);
    }

    private void validToken(String token, String email) {
        emailCertificationService.verifyEmail(token, email);
    }

    @Transactional
    public void updateEmailVerified(String token, String email) {
        validToken(token, email);

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));
        user.updateUserLevel();
    }

    @Transactional
    public void addWishList(String email, IdRequest idRequest) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        if (user.getCart() == null) {
            Cart cart = cartRepository.save(new Cart());
            user.createCart(cart);
        }

        Product product = productRepository.findById(idRequest.getId()).orElseThrow();
        CartProduct cartItem = cartProductRepository.save(new CartProduct(user.getCart(), product));

        if (user.checkCartItemDuplicate(cartItem)) {
            throw new DuplicateCartItemException("장바구니 중복");
        }

        user.addCartItem(cartItem);
    }

    public Set<WishItemResponse> getWishList(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        if (user.getCart() == null) {
            Cart cart = cartRepository.save(new Cart());
            user.createCart(cart);
        }
        return user.getWishList();
    }

    @Transactional
    public void deleteWishList(IdRequest idRequest) {
        cartProductRepository.deleteById(idRequest.getId());
    }
}