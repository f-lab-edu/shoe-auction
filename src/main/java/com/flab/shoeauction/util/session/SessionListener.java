package com.flab.shoeauction.util.session;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import static com.flab.shoeauction.util.session.SessionConstants.LOGIN_TIMEOUT;


/*
* HttpSessionListener를 이용해 세션 유지시간 24시간으로 설정
*/
@WebListener
public class SessionListener implements HttpSessionListener {
    @Override
    public void sessionCreated(HttpSessionEvent sessionEvent) {
        sessionEvent.getSession().setMaxInactiveInterval(LOGIN_TIMEOUT);
    }
}
