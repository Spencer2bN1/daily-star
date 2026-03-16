package com.dailystar.component;

import com.dailystar.constant.SecurityConstants;
import com.dailystar.enums.MessageCodeEnum;
import com.dailystar.exception.BusinessException;
import com.dailystar.model.LoginUser;
import com.dailystar.util.JwtTokenUtil;
import io.jsonwebtoken.JwtException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtTokenUtil jwtTokenUtil;

    public AuthInterceptor(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authorization = request.getHeader(SecurityConstants.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith(SecurityConstants.BEARER_PREFIX)) {
            throw new BusinessException(MessageCodeEnum.UNAUTHORIZED);
        }
        String token = authorization.substring(SecurityConstants.BEARER_PREFIX.length()).trim();
        try {
            LoginUser loginUser = jwtTokenUtil.parseToken(token);
            AuthContextHolder.set(loginUser);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            throw new BusinessException(MessageCodeEnum.TOKEN_INVALID);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContextHolder.clear();
    }
}
