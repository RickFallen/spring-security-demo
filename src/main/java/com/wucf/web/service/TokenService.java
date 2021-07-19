package com.wucf.web.service;

import com.wucf.util.uuid.IdUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenService {
    // 令牌秘钥
    @Value("${token.secret}")
    private String secret;
    public static final String LOGIN_USER_KEY = "login:user:name";
    public static final String CACHE_KEY = "user:cache:key:";

    public static final Map<String, UserDetails> CACHE = new ConcurrentHashMap<>();

    /**
     * 创建令牌
     *
     * @param userDetails 用户信息
     * @return 令牌
     */
    public String createToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        //先把randomId放入Token中
        //解析Token时把randomId取出来再去缓存拿user（缓存中也可只放userId，都可以）
        String randomId = IdUtils.fastUUID();
        claims.put(LOGIN_USER_KEY, randomId);
        CACHE.put(CACHE_KEY + randomId, userDetails);
        return createToken(claims);
    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims) {
        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从令牌中存储的信息
     *
     * @param token 令牌
     * @return 在Token中存储的login key
     */
    public String getLoginKeyFromToken(String token) {
        Claims claims = parseToken(token);
        return (String) claims.get(LOGIN_USER_KEY);
    }

    /**
     * 从令牌中获取token，再从缓存中获取用户信息
     *
     * @param token
     * @return
     */
    public UserDetails getUserFromToken(String token) {
        String loginKey = getLoginKeyFromToken(token);
        return CACHE.get(CACHE_KEY + loginKey);
    }
}
