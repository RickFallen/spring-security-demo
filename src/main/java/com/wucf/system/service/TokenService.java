package com.wucf.system.service;

import com.wucf.core.common.Constants;
import com.wucf.core.model.LoginUser;
import com.wucf.utils.uuid.IdUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenService {
    // 令牌秘钥
    @Value("${token.secret}")
    private String secret;
    @Value("${token.header}")
    private String header;
    // 令牌有效期（默认30分钟）
    @Value("${token.expireTime}")
    private Integer expireTime;

    public static final Map<String, LoginUser> CACHE = new ConcurrentHashMap<>();

    /**
     * 创建令牌
     *
     * @param userDetails 用户信息
     * @return 令牌
     */
    public String createToken(LoginUser userDetails) {
        Map<String, Object> claims = new HashMap<>();
        //先把randomId放入Token中
        //解析Token时把randomId取出来再去缓存拿user（缓存中也可只放userId，都可以）
        String randomId = IdUtils.fastUUID();
        claims.put(Constants.LOGIN_USER_KEY, randomId);
        CACHE.put(Constants.CACHE_KEY + randomId, userDetails);
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
        return (String) claims.get(Constants.LOGIN_USER_KEY);
    }

    /**
     * 从令牌中获取token，再从缓存中获取用户信息
     * @param token
     * @return
     */
    public LoginUser getUserFromToken(String token) {
        String loginKey = getLoginKeyFromToken(token);
        return CACHE.get(Constants.CACHE_KEY + loginKey);
    }

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getUserFromRequest(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = getToken(request);
        if (StringUtils.isNotEmpty(token)) {
            return getUserFromToken(token);
        }
        return null;
    }

    /**
     * 获取请求token
     *
     * @param request
     * @return token
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(header);
        return token;
    }
}