package com.ahuralearn.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JWTUtils {

    private static final String SIGN = "QRIH@EW#7";

    // generate jwt
    public static String createToken(Map<String, Object> map, Instant expireTime) {
        JWTCreator.Builder builder = JWT.create();
        if (map != null) {
            map.forEach((key, value) -> {
                if (value == null) return;
                switch (value) {
                    case String s -> builder.withClaim(key, s);
                    case Integer i -> builder.withClaim(key, i);
                    case Long l -> builder.withClaim(key, l);
                    case Double d -> builder.withClaim(key, d);
                    case Boolean b -> builder.withClaim(key, b);
                    case Date date -> builder.withClaim(key, date);
                    case Instant ins -> builder.withClaim(key, ins);
                    case List<?> list -> builder.withClaim(key, list);
                    case Map<?, ?> m -> builder.withClaim(key, (Map<String, ?>) m);
                    default -> builder.withClaim(key, value.toString());
                }
            });
        }
        return builder.withExpiresAt(expireTime).sign(Algorithm.HMAC256(SIGN));
    }

    public static DecodedJWT verifyToken(String token) {
        return JWT.require(Algorithm.HMAC256(SIGN)).build().verify(token);
    }
}
