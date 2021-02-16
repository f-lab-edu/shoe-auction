package com.flab.shoeauction.exception;

import static com.flab.shoeauction.common.utils.response.ResponseConstants.BAD_REQUEST;
import static com.flab.shoeauction.common.utils.response.ResponseConstants.BRAND_NOT_FOUND;
import static com.flab.shoeauction.common.utils.response.ResponseConstants.DUPLICATION_BRAND_NAME;
import static com.flab.shoeauction.common.utils.response.ResponseConstants.DUPLICATION_EMAIL;
import static com.flab.shoeauction.common.utils.response.ResponseConstants.DUPLICATION_MODEL_NUMBER;
import static com.flab.shoeauction.common.utils.response.ResponseConstants.DUPLICATION_NICKNAME;
import static com.flab.shoeauction.common.utils.response.ResponseConstants.PRODUCT_NOT_FOUND;
import static com.flab.shoeauction.common.utils.response.ResponseConstants.UNAUTHORIZED_USER;
import static com.flab.shoeauction.common.utils.response.ResponseConstants.USER_NOT_FOUND;
import static com.flab.shoeauction.common.utils.response.ResponseConstants.WRONG_PASSWORD;

import com.flab.shoeauction.exception.brand.BrandNotFoundException;
import com.flab.shoeauction.exception.brand.DuplicateBrandNameException;
import com.flab.shoeauction.exception.product.DuplicateModelNumberException;
import com.flab.shoeauction.exception.product.ProductNotFoundException;
import com.flab.shoeauction.exception.user.AuthenticationNumberMismatchException;
import com.flab.shoeauction.exception.user.DuplicateEmailException;
import com.flab.shoeauction.exception.user.DuplicateNicknameException;
import com.flab.shoeauction.exception.user.UnauthenticatedUserException;
import com.flab.shoeauction.exception.user.UserNotFoundException;
import com.flab.shoeauction.exception.user.WrongPasswordException;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public final ResponseEntity<String> duplicateEmailException(DuplicateEmailException exception) {
        log.debug("중복된 이메일입니다.", exception);
        return DUPLICATION_EMAIL;
    }

    @ExceptionHandler(DuplicateNicknameException.class)
    public final ResponseEntity<String> duplicateNicknameException(
        DuplicateNicknameException exception) {
        log.debug("중복된 닉네임입니다.", exception);
        return DUPLICATION_NICKNAME;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<String> methodArgumentNotValidException(
        MethodArgumentNotValidException exception) {
        log.debug(exception.getFieldError().getDefaultMessage(), exception);
        return new ResponseEntity<>(exception.getFieldError().getDefaultMessage(),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<String> handleUserNotFoundException(
        UserNotFoundException ex) {
        log.debug("로그인 실패 : 존재하지 않는 ID 또는 패스워드 불일치", ex);
        return USER_NOT_FOUND;
    }

    @ExceptionHandler(UnauthenticatedUserException.class)
    public final ResponseEntity<String> handleUnauthenticatedUserException(
        UnauthenticatedUserException ex, WebRequest request) {
        log.debug("Failed to Execution ::  {}, detection time={} ", request.getDescription(false),
            LocalDateTime.now(), ex);
        return UNAUTHORIZED_USER;
    }

    @ExceptionHandler(AuthenticationNumberMismatchException.class)
    public final ResponseEntity<Void> handleAuthenticationNumberMismatchException(
        AuthenticationNumberMismatchException ex) {
        log.debug("인증번호 불일치", ex);
        return BAD_REQUEST;
    }

    @ExceptionHandler(WrongPasswordException.class)
    public final ResponseEntity<String> wrongPasswordException(WrongPasswordException ex,
        WebRequest request) {
        log.debug("Wrong password ::  {}, detection time={} ", request.getDescription(false),
            LocalDateTime.now(), ex);
        return WRONG_PASSWORD;
    }

    @ExceptionHandler(DuplicateBrandNameException.class)
    public final ResponseEntity<String> duplicateBrandNameException(
        DuplicateBrandNameException ex) {
        log.debug("이미 존재하는 브랜드명입니다.", ex);
        return DUPLICATION_BRAND_NAME;
    }

    @ExceptionHandler(BrandNotFoundException.class)
    public final ResponseEntity<String> brandNotFoundException(
        BrandNotFoundException ex) {
        log.debug("존재하지 않는 브랜드입니다.", ex);
        return BRAND_NOT_FOUND;
    }

    @ExceptionHandler(DuplicateModelNumberException.class)
    public final ResponseEntity<String> duplicateModelNumberException(
        DuplicateModelNumberException ex) {
        log.debug("이미 존재하는 모델 넘버입니다.", ex);
        return DUPLICATION_MODEL_NUMBER;
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public final ResponseEntity<String> productNotFoundException(
        ProductNotFoundException ex) {
        log.debug("존재하지 않는 상품입니다.", ex);
        return PRODUCT_NOT_FOUND;
    }
}