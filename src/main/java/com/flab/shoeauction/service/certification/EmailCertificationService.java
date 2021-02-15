package com.flab.shoeauction.service.certification;

import static com.flab.shoeauction.common.utils.certification.RandomNumberGeneration.makeRandomNumber;
import static com.flab.shoeauction.common.utils.certification.email.EmailConstants.TITLE_CERTIFICATION;
import static com.flab.shoeauction.common.utils.certification.email.EmailConstants.TITLE_EMAIL_CHECK;

import com.flab.shoeauction.common.config.AppProperties;
import com.flab.shoeauction.common.utils.certification.email.EmailContentTemplate;
import com.flab.shoeauction.controller.dto.UserDto.EmailCertificationRequest;
import com.flab.shoeauction.dao.EmailCertificationDao;
import com.flab.shoeauction.exception.user.AuthenticationNumberMismatchException;
import com.flab.shoeauction.exception.user.UnauthenticatedUserException;
import java.util.UUID;
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
    private final AppProperties appProperties;
    // 이메일 전송 및 인증번호 저장
    public void sendEmailForCertification(String email) {

        String randomNumber = makeRandomNumber();
        String content = makeEmailContent(randomNumber);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(appProperties.getEmailFromAddress());
        message.setSubject(TITLE_CERTIFICATION);
        message.setText(content);
        mailSender.send(message);

        emailCertificationDao.createEmailCertification(email, randomNumber);
    }

    public void sendEmailForEmailCheck(String email) {

        String token = UUID.randomUUID().toString();
        String content = makeEmailContent(token, email);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(appProperties.getEmailFromAddress());
        message.setSubject(TITLE_EMAIL_CHECK);
        message.setText(content);
        mailSender.send(message);

        emailCertificationDao.createEmailToken(email, token);
    }


    // 인증 이메일 내용 생성
    public String makeEmailContent(String certificationNumber) {
        EmailContentTemplate content = new EmailContentTemplate();
        return content.buildCertificationContent(certificationNumber);
    }

    //이메일 확인용 내용 생성
    public String makeEmailContent(String token, String email) {
        EmailContentTemplate content = new EmailContentTemplate();
        return content.buildEmailCheckContent(token, email);

    }

    // 토큰 일치 여부 검사
    public void verifyEmail(String token, String email) {
        if (isVerify(token, email)) {
            throw new UnauthenticatedUserException("인증 토큰이 만료되었습니다.");
        }
        emailCertificationDao.removeEmailCertification(email);
    }


    // 토큰 일치 여부 확인 내부 로직
    private boolean isVerify(String token, String email) {
        return !(emailCertificationDao.hasKey(email) &&
            emailCertificationDao.getEmailCertification(email)
                .equals(token));
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

