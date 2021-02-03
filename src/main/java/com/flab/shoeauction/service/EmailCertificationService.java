package com.flab.shoeauction.service;

import static com.flab.shoeauction.common.utils.certification.RandomNumberGeneration.makeRandomNumber;
import static com.flab.shoeauction.common.utils.certification.email.EmailConstants.TITLE;

import com.flab.shoeauction.common.utils.certification.email.EmailContentTemplate;
import com.flab.shoeauction.controller.dto.UserDto.EmailCertificationRequest;
import com.flab.shoeauction.dao.EmailCertificationDao;
import com.flab.shoeauction.exception.user.AuthenticationNumberMismatchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailCertificationService {

    @Value("${external.email}")
    private String FROM_ADDRESS;
    private final JavaMailSender mailSender;
    private final EmailCertificationDao emailCertificationDao;


    // 이메일 전송 및 인증번호 저장
    public void sendEmail(String email) {
        String randomNumber = makeRandomNumber();
        String content = makeEmailContent(randomNumber);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(FROM_ADDRESS);
        message.setSubject(TITLE);
        message.setText(content);
        mailSender.send(message);

        emailCertificationDao.createEmailCertification(email, randomNumber);

    }

    // 인증 이메일 내용 생성
    public String makeEmailContent(String certificationNumber) {
        EmailContentTemplate content = new EmailContentTemplate();
        return content.parse(certificationNumber);
    }

    // 인증번호 일치 여부 확인
    public void verifyEmail(EmailCertificationRequest requestDto) {
        if (isVerify(requestDto)) {
            throw new AuthenticationNumberMismatchException("인증번호가 일치하지 않습니다.");
        }
        emailCertificationDao.removeEmailCertification(requestDto.getEmail());

    }

    // 인증번호 일치 여부 확인 내부 로직
    private boolean isVerify(EmailCertificationRequest requestDto) {
        return !(emailCertificationDao.hasKey(requestDto.getEmail()) &&
            emailCertificationDao.getEmailCertification(requestDto.getEmail())
                .equals(requestDto.getCertificationNumber()));
    }
}

