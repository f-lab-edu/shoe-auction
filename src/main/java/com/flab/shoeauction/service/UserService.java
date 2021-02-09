package com.flab.shoeauction.service;

import com.flab.shoeauction.controller.dto.UserDto.ChangeAddressRequest;
import com.flab.shoeauction.controller.dto.UserDto.ChangePasswordRequest;
import com.flab.shoeauction.controller.dto.UserDto.FindUserResponse;
import com.flab.shoeauction.controller.dto.UserDto.SaveRequest;
import com.flab.shoeauction.domain.AddressBook.Address;
import com.flab.shoeauction.domain.AddressBook.AddressBook;
import com.flab.shoeauction.domain.AddressBook.AddressBookRepository;
import com.flab.shoeauction.domain.user.Account;
import com.flab.shoeauction.domain.user.User;
import com.flab.shoeauction.domain.user.UserRepository;
import com.flab.shoeauction.exception.user.DuplicateEmailException;
import com.flab.shoeauction.exception.user.DuplicateNicknameException;
import com.flab.shoeauction.exception.user.UnauthenticatedUserException;
import com.flab.shoeauction.exception.user.UserNotFoundException;
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
            .orElseThrow(() -> new UnauthenticatedUserException("Unauthenticated user"));

        user.updatePassword(requestDto.getPasswordAfter());
    }

    @Transactional
    public void updatePassword(String email, ChangePasswordRequest requestDto) {
        requestDto.passwordEncryption(encryptionService);
        String passwordBefore = requestDto.getPasswordBefore();
        String passwordAfter = requestDto.getPasswordAfter();
        System.out.println(email);
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

        user.updateAddressBook(address);
    }

    @Transactional
    public void deleteAddressBook(ChangeAddressRequest requestDto) {
        Long addressBookId = requestDto.getId();
        addressBookRepository.deleteById(addressBookId);
    }

    @Transactional
    public void updateAddressBook(ChangeAddressRequest requestDto) {
        Long addressBookId = requestDto.getId();
        AddressBook addressBook = addressBookRepository.findById(addressBookId).orElseThrow();
        addressBook.updateAddressBook(requestDto);
    }

    @Transactional
    public void updateNickname(String email,SaveRequest requestDto) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        if (checkNicknameDuplicate(requestDto.getNickname())) {
            throw new DuplicateNicknameException();
        }
        user.updateNickname(requestDto);
    }
}