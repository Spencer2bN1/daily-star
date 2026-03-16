package com.dailystar.util;

import com.dailystar.config.AuthProperties;
import com.dailystar.enums.LoginTypeEnum;
import com.dailystar.model.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtil {

    private static final String CLAIM_ACCOUNT_ID = "accountId";
    private static final String CLAIM_MOBILE = "mobile";
    private static final String CLAIM_LOGIN_TYPE = "loginType";

    private final AuthProperties authProperties;

    public JwtTokenUtil(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    public String generateToken(LoginUser loginUser) {
        Date now = new Date();
        Date expireAt = new Date(now.getTime() + authProperties.getExpireSeconds() * 1000L);
        return Jwts.builder()
            .setSubject(String.valueOf(loginUser.getAccountId()))
            .claim(CLAIM_ACCOUNT_ID, loginUser.getAccountId())
            .claim(CLAIM_MOBILE, loginUser.getMobile())
            .claim(CLAIM_LOGIN_TYPE, loginUser.getLoginType().name())
            .setIssuedAt(now)
            .setExpiration(expireAt)
            .signWith(getSecretKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public LoginUser parseToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSecretKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
        return LoginUser.builder()
            .accountId(((Number) claims.get(CLAIM_ACCOUNT_ID)).longValue())
            .mobile((String) claims.get(CLAIM_MOBILE))
            .loginType(LoginTypeEnum.valueOf((String) claims.get(CLAIM_LOGIN_TYPE)))
            .build();
    }

    public long getExpireSeconds() {
        return authProperties.getExpireSeconds();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(authProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
