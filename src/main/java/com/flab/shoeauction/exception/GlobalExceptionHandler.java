package com.flab.shoeauction.exception;

import static com.flab.shoeauction.common.utils.constants.ResponseConstants.BAD_REQUEST;
import static com.flab.shoeauction.common.utils.constants.ResponseConstants.BRAND_NOT_FOUND;
import static com.flab.shoeauction.common.utils.constants.ResponseConstants.DUPLICATION_BRAND_NAME;
import static com.flab.shoeauction.common.utils.constants.ResponseConstants.DUPLICATION_CART_ITEM;
import static com.flab.shoeauction.common.utils.constants.ResponseConstants.DUPLICATION_EMAIL;
import static com.flab.shoeauction.common.utils.constants.ResponseConstants.DUPLICATION_MODEL_NUMBER;
import static com.flab.shoeauction.common.utils.constants.ResponseConstants.DUPLICATION_NICKNAME;
import static com.flab.shoeauction.common.utils.constants.ResponseConstants.FAIL_TO_CHANGE_NICKNAME;
import static com.flab.shoeauction.common.utils.constants.ResponseConstants.ILLEGAL_MIME_TYPE;
import static com.flab.shoeauction.common.utils.constants.ResponseConstants.IMAGE_ROAD_FAILED;
import static com.flab.shoeauction.common.utils.constants.ResponseConstants.IMAGE_TOO_LARGE;
import static com.flab.shoeauction.common.utils.constants.ResponseConstants.NOT_AUTHORIZED;
import static com.flab.shoeauction.common.utils.constants.ResponseConstants.PRODUCT_NOT_FOUND;
import static com.flab.shoeauction.common.utils.constants.ResponseConstants.PROGRESSING_TRADE;
import static com.flab.shoeauction.common.utils.constants.ResponseConstants.PURCHASE_FAILED;
import static com.flab.shoeauction.common.utils.constants.ResponseConstants.REMAINING_POINT;
import static com.flab.shoeauction.common.utils.constants.ResponseConstants.TOKEN_EXPIRED;
import static com.flab.shoeauction.common.utils.constants.ResponseConstants.UNAUTHORIZED_USER;
import static com.flab.shoeauction.common.utils.constants.ResponseConstants.USER_NOT_FOUND;
import static com.flab.shoeauction.common.utils.constants.ResponseConstants.WRONG_PASSWORD;

import com.flab.shoeauction.exception.brand.BrandNotFoundException;
import com.flab.shoeauction.exception.brand.DuplicateBrandNameException;
import com.flab.shoeauction.exception.file.IllegalMimeTypeException;
import com.flab.shoeauction.exception.file.ImageRoadFailedException;
import com.flab.shoeauction.exception.product.DuplicateModelNumberException;
import com.flab.shoeauction.exception.product.ProductNotFoundException;
import com.flab.shoeauction.exception.trade.LowPointException;
import com.flab.shoeauction.exception.user.AuthenticationNumberMismatchException;
import com.flab.shoeauction.exception.user.DuplicateCartItemException;
import com.flab.shoeauction.exception.user.DuplicateEmailException;
import com.flab.shoeauction.exception.user.DuplicateNicknameException;
import com.flab.shoeauction.exception.user.HasProgressingTradeException;
import com.flab.shoeauction.exception.user.HasRemainingPointException;
import com.flab.shoeauction.exception.user.NotAuthorizedException;
import com.flab.shoeauction.exception.user.TokenExpiredException;
import com.flab.shoeauction.exception.user.UnableToChangeNicknameException;
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
import org.springframework.web.multipart.MaxUploadSizeExceededException;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public final ResponseEntity<String> handleDuplicateEmailException(
        DuplicateEmailException exception) {
        log.debug("중복된 이메일입니다.", exception);
        return DUPLICATION_EMAIL;
    }

    @ExceptionHandler(DuplicateNicknameException.class)
    public final ResponseEntity<String> handleDuplicateNicknameException(
        DuplicateNicknameException exception) {
        log.debug("중복된 닉네임입니다.", exception);
        return DUPLICATION_NICKNAME;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<String> handleMethodArgumentNotValidException(
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

    @ExceptionHandler(UnableToChangeNicknameException.class)
    public final ResponseEntity handleUnableToChangeNicknameException(
        UnableToChangeNicknameException ex) {
        log.error("닉네임은 7일에 한번만 변경 가능합니다.", ex);
        return FAIL_TO_CHANGE_NICKNAME;
    }

    @ExceptionHandler(WrongPasswordException.class)
    public final ResponseEntity<String> handleWrongPasswordException(WrongPasswordException ex,
        WebRequest request) {
        log.debug("Wrong password ::  {}, detection time={} ", request.getDescription(false),
            LocalDateTime.now(), ex);
        return WRONG_PASSWORD;
    }

    @ExceptionHandler(HasRemainingPointException.class)
    public final ResponseEntity<String> handleHasRemainingPointException(
        HasRemainingPointException ex, WebRequest request) {
        log.debug("Has Remaining Point ::  {}, detection time={} ",
            request.getDescription(false),
            LocalDateTime.now(), ex);
        return REMAINING_POINT;
    }

    @ExceptionHandler(HasProgressingTradeException.class)
    public final ResponseEntity<String> handleHasProgressingTradeException(
        HasProgressingTradeException ex, WebRequest request) {
        log.debug("Has Progressing Trade ::  {}, detection time={} ",
            request.getDescription(false),
            LocalDateTime.now(), ex);
        return PROGRESSING_TRADE;
    }

    @ExceptionHandler(DuplicateBrandNameException.class)
    public final ResponseEntity<String> handleDuplicateBrandNameException(
        DuplicateBrandNameException ex) {
        log.debug("이미 존재하는 브랜드명입니다.", ex);
        return DUPLICATION_BRAND_NAME;
    }

    @ExceptionHandler(BrandNotFoundException.class)
    public final ResponseEntity<String> handleBrandNotFoundException(
        BrandNotFoundException ex) {
        log.debug("존재하지 않는 브랜드입니다.", ex);
        return BRAND_NOT_FOUND;
    }

    @ExceptionHandler(DuplicateModelNumberException.class)
    public final ResponseEntity<String> handleDuplicateModelNumberException(
        DuplicateModelNumberException ex) {
        log.debug("이미 존재하는 모델 넘버입니다.", ex);
        return DUPLICATION_MODEL_NUMBER;
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public final ResponseEntity<String> handleProductNotFoundException(
        ProductNotFoundException ex) {
        log.debug("존재하지 않는 상품입니다.", ex);
        return PRODUCT_NOT_FOUND;
    }

    @ExceptionHandler(TokenExpiredException.class)
    public final ResponseEntity handleTokenExpiredException(TokenExpiredException ex,
        WebRequest request) {
        log.debug("Token Expired :: {} , detection time={}", request.getDescription(false),
            LocalDateTime.now(), ex);
        return TOKEN_EXPIRED;
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public final ResponseEntity handleNotAuthorizedException(NotAuthorizedException ex,
        WebRequest webRequest) {
        log.debug("Not Authorized :: {}, detection time ={}", webRequest.getDescription(false),
            LocalDateTime.now(), ex);
        return NOT_AUTHORIZED;
    }

    @ExceptionHandler(IllegalMimeTypeException.class)
    public final ResponseEntity<String> handleIllegalMimeTypeException(
        IllegalMimeTypeException ex) {
        log.debug("올바르지 않은 확장자입니다.", ex);
        return ILLEGAL_MIME_TYPE;
    }

    @ExceptionHandler(ImageRoadFailedException.class)
    public final ResponseEntity<String> handleImageRoadFailedException(
        ImageRoadFailedException ex) {
        log.debug("이미지 로드에 실패하였습니다.", ex);
        return IMAGE_ROAD_FAILED;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public final ResponseEntity<String> handleMaxUploadSizeExceededException(
        MaxUploadSizeExceededException ex) {
        log.debug("허용된 용량을 초과한 이미지입니다.", ex);
        return IMAGE_TOO_LARGE;
    }

    @ExceptionHandler(DuplicateCartItemException.class)
    public final ResponseEntity<String> handleDuplicateCartItemException(
        DuplicateCartItemException ex) {
        log.debug("해당 상품은 이미 위시리스트에 등록되어 있는 상품입니다.", ex);
        return DUPLICATION_CART_ITEM;
    }

    @ExceptionHandler(LowPointException.class)
    public final ResponseEntity<String> handleLowPointException(LowPointException ex) {
        log.debug("입찰 가능 포인트 부족으로, 구매를 진행할 수 없습니다.", ex);
        return PURCHASE_FAILED;
    }
}