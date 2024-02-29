package com.blogapp.api.security;

import java.util.Date;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JWTGenerator {
    private static final Key key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512);

    private static Key generateKey() {
        // TODO: Implement a secure key generation mechanism for production
        return Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        if (StringUtils.hasText(token)) {
            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
                return claims.getSubject();
            } catch (Exception ex) {
                // Log or handle the exception
                System.out.println("Error parsing JWT: " + ex.getMessage());
                return null;  // Or throw an exception if appropriate
            }
        } else {
            // Log or handle the case where the token is null or empty
            return null;  // Or throw an exception if appropriate
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}