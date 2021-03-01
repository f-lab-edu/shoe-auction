package com.flab.shoeauction.domain.users.common;

/**
 * UNAUTH : 회원가입 후 이메일 인증을 완료하지 않은 사용자 (일부 기능 제한) AUTH : 회원 가입 후 이메일 인증을 완료한 사용자(관리자 기능 외 모든 기능 이용
 * 가능) BAN : 관리자에 의해 정지된 사용자(서비스 이용 불가능) ADMIN : 관리자
 */

public enum UserLevel {
    UNAUTH, AUTH, ADMIN
}
