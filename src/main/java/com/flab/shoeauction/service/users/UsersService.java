package com.flab.shoeauction.service.users;

import com.flab.shoeauction.domain.users.UsersRepository;
import com.flab.shoeauction.exception.user.DuplicateEmailException;
import com.flab.shoeauction.exception.user.DuplicateNicknameException;
import com.flab.shoeauction.exception.user.WrongConfirmPasswordException;
import com.flab.shoeauction.web.dto.users.UsersSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UsersService {

    private final UsersRepository usersRepository;

    public boolean checkEmailUnique(String email) {
        return !usersRepository.existsByEmail(email);
    }

    public boolean checkNicknameUnique(String nickname) {
        return !usersRepository.existsByNickname(nickname);
    }

    public boolean checkPasswordSame(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    @Transactional
    public void save(UsersSaveRequestDto requestDto) {
        if (!checkEmailUnique(requestDto.getEmail()))
            throw new DuplicateEmailException();
        if (!checkNicknameUnique(requestDto.getNickname()))
            throw new DuplicateNicknameException();
        if (!checkPasswordSame(requestDto.getPassword(), requestDto.getConfirmPassword()))
            throw new WrongConfirmPasswordException();
        usersRepository.save(requestDto.toEntity());
    }
}