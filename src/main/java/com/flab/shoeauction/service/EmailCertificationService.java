package com.flab.shoeauction.service;

import static com.flab.shoeauction.common.util.email.EmailConstants.FROM_ADDRESS;
import static com.flab.shoeauction.common.util.email.EmailConstants.TITLE;
import static com.flab.shoeauction.common.util.user.UserConstants.makeRandomNumber;

import com.flab.shoeauction.common.util.email.EmailContentTemplate;
import com.flab.shoeauction.controller.dto.UserDto.EmailCertificationRequest;
import com.flab.shoeauction.dao.EmailCertificationDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailCertificationService {

    private final JavaMailSender mailSender;
    private final EmailCertificationDao emailCertificationDao;


    //이메일 전송 및 인증번호 저장
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

        log.info(emailCertificationDao.getEmailCertification(email));

    }

    //인증 이메일 내용 생성
    public String makeEmailContent(String certificationNumber){
        EmailContentTemplate content = new EmailContentTemplate();
        content.setCertificationNumber(certificationNumber);
        return content.parse();
    }

    //인증번호 확인
    public boolean verifyEmail(EmailCertificationRequest requestDto){
        if (emailCertificationDao.hasKey(requestDto.getEmail()) &&
            emailCertificationDao.getEmailCertification(requestDto.getEmail()).equals(requestDto.getCertificationNumber())) {
            emailCertificationDao.removeSmsCertification(requestDto.getEmail());
            return true;
        }
        return false;
    }
}

