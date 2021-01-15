package com.flab.shoeauction.user.service;

import static com.flab.shoeauction.user.utils.UserConstants.NUMBER_GENERATION_COUNT;

import com.flab.shoeauction.common.utils.encrytion.EncryptionUtils;
import com.flab.shoeauction.user.domain.User;
import com.flab.shoeauction.user.dto.UserDto;
import com.flab.shoeauction.user.exception.EmailDuplicateException;
import com.flab.shoeauction.user.exception.NicknameDuplicateException;
import com.flab.shoeauction.user.repository.UserRepository;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class SignUpService {

  private final UserRepository userRepository;
  private final SmsCertificationService smsCertificationService;
  private final EncryptionUtils encryptionUtils;
  private final SmsSendService smsSendService;

  //데이터 조회용. 추후 삭제
  public List<User> findAll() {
    return userRepository.findAll();
  }

  public User saveUser(UserDto userDto) {
    if (emailDuplicateCheck(userDto.getEmail())) {
      throw new EmailDuplicateException("이미 존재하는 email 입니다. 다른 email을 사용해주세요.");
    }
    if (nicknameDuplicateCheck(userDto.getNickname())) {
      throw new NicknameDuplicateException("이미 존재하는 닉네임 입니다. 다른 닉네임을 사용해주세요.");
    }

    userDto.passwordEncryption(encryptionUtils);
    return userRepository.save(userDto.toUser());
  }

  public boolean emailDuplicateCheck(String email) {
    return userRepository.existsByEmail(email);
  }

  public boolean nicknameDuplicateCheck(String nickname) {
    return userRepository.existsByNickname(nickname);
  }

  public boolean certificationNumberInspection(String certificationNumber) {
    return smsCertificationService.getSmsCertificationService().equals(certificationNumber);
  }

  public void saveAuthenticationNumber(String phoneNumber) {
    Random rand = new Random();
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < NUMBER_GENERATION_COUNT; i++) {
      stringBuilder.append((rand.nextInt(10)));
    }
    smsCertificationService.setSmsCertificationService(stringBuilder.toString());
    smsSendService.sendMessage(phoneNumber, smsCertificationService.getSmsCertificationService());
  }
}
