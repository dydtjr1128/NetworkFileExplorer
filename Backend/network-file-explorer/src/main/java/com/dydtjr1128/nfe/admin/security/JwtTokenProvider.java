package com.dydtjr1128.nfe.admin.security;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    private static final String JWT_HEADER = "Authorization";
    private static final String jwtSecret = "JWTSuperSecretKey";
    private static final int jwtExpirationSecond = 60 * 60;

    public String generateToken() {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (jwtExpirationSecond * 1000));

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject("Admin access token")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public Boolean isTokenExpired(String jwt) {
        try {
            Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).getBody();
            Date exp = claims.getExpiration();
            Date now = new Date();
            if (exp.after(now))
                return false;

        } catch (ExpiredJwtException exception) {
            logger.info("Token is expired", exception);
            return true;
        } catch (JwtException exception) {
            logger.info("Token is different", exception);
            return true;
        }
        logger.error("Token is null");
        return true;
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        return false;
    }

    public String getJwtFromString(String fullToken) {
        if (StringUtils.hasText(fullToken) && fullToken.startsWith("Bearer ")) {
            return fullToken.substring(7);
        }
        return null;
    }

    String getJwtFromRequest(HttpServletRequest request) {
        String splitToken = request.getHeader(JWT_HEADER);
        if (StringUtils.hasText(splitToken) && splitToken.startsWith("Bearer ")) {
            return splitToken.substring(7);
        }
        return null;
    }
}