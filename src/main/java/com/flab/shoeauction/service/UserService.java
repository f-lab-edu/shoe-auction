package com.flab.shoeauction.service;

import com.flab.shoeauction.controller.dto.AddressDto;
import com.flab.shoeauction.controller.dto.AddressDto.SaveRequest;
import com.flab.shoeauction.controller.dto.UserDto.ChangePasswordRequest;
import com.flab.shoeauction.controller.dto.UserDto.FindUserResponse;
import com.flab.shoeauction.domain.addressBook.Address;
import com.flab.shoeauction.domain.addressBook.AddressBook;
import com.flab.shoeauction.domain.addressBook.AddressBookRepository;
import com.flab.shoeauction.domain.addressBook.AddressRepository;
import com.flab.shoeauction.domain.cart.Cart;
import com.flab.shoeauction.domain.cart.CartRepository;
import com.flab.shoeauction.domain.users.common.Account;
import com.flab.shoeauction.domain.users.user.User;
import com.flab.shoeauction.domain.users.user.UserRepository;
import com.flab.shoeauction.exception.user.DuplicateEmailException;
import com.flab.shoeauction.exception.user.DuplicateNicknameException;
import com.flab.shoeauction.exception.user.HasProgressingTradeException;
import com.flab.shoeauction.exception.user.HasRemainingPointException;
import com.flab.shoeauction.exception.user.UnauthenticatedUserException;
import com.flab.shoeauction.exception.user.UserNotFoundException;
import com.flab.shoeauction.exception.user.WrongPasswordException;
import com.flab.shoeauction.service.certification.EmailCertificationService;
import com.flab.shoeauction.service.encrytion.EncryptionService;
import java.util.List;
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
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final TradeService tradeService;

    @Transactional(readOnly = true)
    public boolean checkEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean checkNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Transactional
    public void save(com.flab.shoeauction.controller.dto.UserDto.SaveRequest requestDto) {
        if (checkEmailDuplicate(requestDto.getEmail())) {
            throw new DuplicateEmailException();
        }
        if (checkNicknameDuplicate(requestDto.getNickname())) {
            throw new DuplicateNicknameException();
        }
        requestDto.passwordEncryption(encryptionService);

        User user = userRepository.save(requestDto.toEntity());
        createRequiredInformation(user);
    }

    private void createRequiredInformation(User user) {
        user.createCart(cartRepository.save(new Cart()));
        user.createAddressBook(addressBookRepository.save(new AddressBook()));
    }


    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public Account getAccount(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        return user.getAccount();

    }

    @Transactional(readOnly = true)
    public List<Address> getAddressBook(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        AddressBook addressBook = user.getAddressBook();
        return addressBook.getAddressList();

    }

    @Transactional
    public void addAddress(String email, SaveRequest requestDto) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        if (user.getAddressBook() == null) {
            AddressBook addressBook = addressBookRepository.save(new AddressBook());
            user.createAddressBook(addressBook);
        }

        user.addAddress(requestDto.toEntity());
    }

    @Transactional
    public void deleteAddress(String email, AddressDto.IdRequest idRequest) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        Long addressId = idRequest.getId();

        Address address = addressRepository.findById(addressId).orElseThrow();

        user.deleteAddress(address);

    }

    @Transactional
    public void updateAddress(SaveRequest requestDto) {

        Long addressId = requestDto.getId();
        Address address = addressRepository.findById(addressId).orElseThrow();
        address.updateAddress(requestDto);

    }

    @Transactional
    public void updateNickname(String email,
        com.flab.shoeauction.controller.dto.UserDto.SaveRequest requestDto) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        if (checkNicknameDuplicate(requestDto.getNickname())) {
            throw new DuplicateNicknameException();
        }
        user.updateNickname(requestDto);
    }

    @Transactional
    public void delete(String email, String password) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        if (!userRepository.existsByEmailAndPassword(email, encryptionService.encrypt(password))) {
            throw new WrongPasswordException();
        }
        if (tradeService.hasUsersProgressingTrade(user)) {
            throw new HasProgressingTradeException("진행중인 거래를 마친 후 탈퇴가 가능합니다.");
        }
        if (user.hasRemainingPoints()) {
            throw new HasRemainingPointException("잔여 포인트를 출금 후 탈퇴가 가능합니다.");
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
}