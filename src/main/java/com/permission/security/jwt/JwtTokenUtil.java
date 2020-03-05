package com.permission.security.jwt;

import com.alibaba.fastjson.JSON;
import com.permission.security.entity.SystemUser;
import com.permission.security.exception.JwtExpiredException;
import com.permission.security.exception.JwtSignatureException;
import com.permission.utils.global.result.ResultEnum;
import io.jsonwebtoken.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2019-05-28   *
 * * Time: 17:06        *
 * * to: lz&xm          *
 * **********************
 **/
public class JwtTokenUtil {

    /**
     * JWT签名加密key
     */
    private final static String secret;

    /**
     * token参数头
     */
    public final static String tokenHeader;

    /**
     * token分割
     */
    public static String tokenPrefix;

    /**
     * 权限参数头
     */
    public final static String authHeader;

    /**
     * token有效期
     */
    public final static Long tokenExpireTime;

    static {
        secret = "mySecret";
        tokenHeader = "Authorization";
        tokenPrefix = "Bearer ";
        authHeader = "role";
        tokenExpireTime = 3600000L;//token有效期为1小时
    }

    //生成token
    public static String generateAccessToken(UserDetails userDetails, String ip) {
        SystemUser user = (SystemUser) userDetails;
        //登陆成功生成JWT
        return Jwts.builder()
                //主题 放入用户名
                .setSubject(user.getUsername())
                //放入ip
                .setId(ip)
                //自定义属性 放入用户拥有权限
                .claim(authHeader, JSON.toJSONString(user.getAuthorities()))
                //失效时间
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpireTime))
                //签名算法和密钥
                .signWith(SignatureAlgorithm.HS512, secret)
                //压缩token
                .compressWith(CompressionCodecs.DEFLATE)
                .compact();
    }

    //获取存入token中的数据
    public static Claims getClaims(String accessToken) {
        Claims claims;
        if (!accessToken.startsWith(JwtTokenUtil.tokenPrefix)) {
            //token被修改
            throw new JwtSignatureException(ResultEnum.TOKEN_ERROR);
        }
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(accessToken.replace(tokenPrefix, ""))
                    .getBody();
        } catch (ExpiredJwtException e) {
            //token过期
            throw new JwtExpiredException(ResultEnum.TOKEN_INVALID);
        } catch (SignatureException | MalformedJwtException e) {
            //token被修改
            throw new JwtSignatureException(ResultEnum.TOKEN_ERROR);
        }
        return claims;
    }
}
